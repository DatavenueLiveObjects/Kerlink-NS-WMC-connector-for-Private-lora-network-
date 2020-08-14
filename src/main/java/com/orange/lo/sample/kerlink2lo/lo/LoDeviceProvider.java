/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlink2lo.lo;

import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkPropertiesList;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class LoDeviceProvider {
    private static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private RestTemplate restTemplate;
    private LoProperties loProperties;
    private KerlinkPropertiesList kerlinkPropertiesList;
    private HttpHeaders authenticationHeaders;
    private HttpEntity<Void> authenticationEntity;
    private Map<String, String> loGroupsMap;

    private final String DEVICES_PAGED_URL_TEMPLATE;
    private final String GROUPS_PAGED_URL_TEMPLATE;

    private static final String X_TOTAL_COUNT_HEADER = "X-Total-Count";
    private static final String X_RATELIMIT_REMAINING_HEADER = "X-Ratelimit-Remaining";
    private static final String X_RATELIMIT_RESET_HEADER = "X-Ratelimit-Reset";

    private static final String DEVICES_ENDPOINT = "/v1/deviceMgt/devices";
    private static final String GROOUPS_ENDPOINT = "/v1/deviceMgt/groups";

    @Autowired
    public LoDeviceProvider(LoProperties loProperties, KerlinkPropertiesList kerlinkPropertiesList, HttpHeaders authenticationHeaders, @Qualifier("loRestTemplate") RestTemplate restTemplate) {
        this.loProperties = loProperties;
        this.kerlinkPropertiesList = kerlinkPropertiesList;
        this.authenticationHeaders = authenticationHeaders;
        this.restTemplate = restTemplate;

        this.loGroupsMap = new HashMap<String, String>();
        this.authenticationEntity = new HttpEntity<Void>(authenticationHeaders);
        this.DEVICES_PAGED_URL_TEMPLATE = loProperties.getApiUrl() + DEVICES_ENDPOINT + "?limit=" + loProperties.getPageSize() + "&offset=%d&groupId=%s&fields=id,name,group";
        this.GROUPS_PAGED_URL_TEMPLATE = loProperties.getApiUrl() + GROOUPS_ENDPOINT + "?limit=" + loProperties.getPageSize() + "&offset=" + "%d";
    }

    @PostConstruct
    public void postConstruct() {
        LOG.info("Managing group of devices");
        try {
            Set<String> kerlinkAccountNames = kerlinkPropertiesList.getKerlinkList().stream().map(p -> p.getKerlinkAccountName()).collect(Collectors.toSet());
            LOG.debug("Trying to get existing groups");

            int retrievedGroups = 0;
            for (int offset = 0;; offset++) {
                try {
                    ResponseEntity<LoGroup[]> response = restTemplate.exchange(getPagedGroupsUrl(offset), HttpMethod.GET, authenticationEntity, LoGroup[].class);
                    if (response.getBody().length == 0) {
                        break;
                    }
                    retrievedGroups += response.getBody().length;
                    Arrays.stream(response.getBody()).forEach(g -> loGroupsMap.put(g.getPathNode(), g.getId()));

                    if (retrievedGroups >= Integer.parseInt(response.getHeaders().get(X_TOTAL_COUNT_HEADER).get(0))) {
                        break;
                    }
                } catch (HttpClientErrorException e) {
                    LOG.error("Cannot retrieve information about groups \n {}", e.getResponseBodyAsString());
                    System.exit(1);
                }
            }

            kerlinkAccountNames.forEach(accountName -> {
                if (!loGroupsMap.containsKey(accountName)) {
                    LOG.debug("Group {} not found, trying to create new group", accountName);
                    LoGroup group = new LoGroup(null, accountName);
                    HttpEntity<LoGroup> httpEntity = new HttpEntity<LoGroup>(group, authenticationHeaders);
                    ResponseEntity<LoGroup> response = restTemplate.exchange(loProperties.getApiUrl() + GROOUPS_ENDPOINT, HttpMethod.POST, httpEntity, LoGroup.class);
                    loGroupsMap.put(accountName, response.getBody().getId());
                    LOG.debug("Group {} created", accountName);
                }
            });
            LOG.debug("kerlinkAccountNames: {}", kerlinkAccountNames);
            LOG.debug("existing groups: {}", loGroupsMap);
        } catch (HttpClientErrorException e) {
            LOG.error("Cannot create group \n {}", e.getResponseBodyAsString());
            System.exit(1);
        } catch (Exception e) {
            LOG.error("Unexpected error while managing group {}", e.getMessage());
            System.exit(1);
        }
    }

    public List<LoDevice> getDevices(String groupName) {
        List<LoDevice> devices = new ArrayList<>(loProperties.getPageSize());
        for (int offset = 0;; offset++) {
            LOG.trace("Calling LO url {}", getPagedDevicesUrl(offset, loGroupsMap.get(groupName)));
            ResponseEntity<LoDevice[]> response = restTemplate.exchange(getPagedDevicesUrl(offset, loGroupsMap.get(groupName)), HttpMethod.GET, authenticationEntity, LoDevice[].class);
            LOG.trace("Got {} devices", response.getBody().length);
            if (response.getBody().length == 0) {
                break;
            }
            devices.addAll(Arrays.asList(response.getBody()));
            if (devices.size() >= Integer.parseInt(response.getHeaders().get(X_TOTAL_COUNT_HEADER).get(0))) {
                break;
            }
            if (Integer.parseInt(response.getHeaders().get(X_RATELIMIT_REMAINING_HEADER).get(0)) == 0) {
                long reset = Long.parseLong(response.getHeaders().get(X_RATELIMIT_RESET_HEADER).get(0));
                long current = System.currentTimeMillis();
                try {
                    Thread.sleep(reset - current);
                } catch (InterruptedException e) {
                    // no matter
                }
            }
        }
        LOG.trace("Devices: " + devices.toString());
        return devices;
    }

    public void addDevice(String deviceId, String kerlinkAccountName) {
        LOG.trace("Trying to add device {} to LO group {}", deviceId, kerlinkAccountName);

        LoDevice device = new LoDevice(deviceId, loGroupsMap.get(kerlinkAccountName), loProperties.getDevicePrefix(), true);
        HttpEntity<LoDevice> httpEntity = new HttpEntity<LoDevice>(device, authenticationHeaders);

        restTemplate.exchange(loProperties.getApiUrl() + DEVICES_ENDPOINT, HttpMethod.POST, httpEntity, Void.class);
    }

    public void deleteDevice(String deviceId) {
        LOG.trace("Trying to delete device {} from LO", deviceId);
        restTemplate.exchange(loProperties.getApiUrl() + DEVICES_ENDPOINT + "/" + deviceId, HttpMethod.DELETE, authenticationEntity, Void.class);
    }

    private String getPagedDevicesUrl(int offset, String groupName) {
        return String.format(DEVICES_PAGED_URL_TEMPLATE, offset * loProperties.getPageSize(), groupName);
    }

    private String getPagedGroupsUrl(int offset) {
        return String.format(GROUPS_PAGED_URL_TEMPLATE, offset * loProperties.getPageSize());
    }
}