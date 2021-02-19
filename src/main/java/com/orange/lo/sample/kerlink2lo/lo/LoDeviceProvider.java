/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlink2lo.lo;

import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkProperties;
import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkPropertiesList;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.orange.lo.sample.kerlink2lo.lo.model.LoDevice;
import com.orange.lo.sample.kerlink2lo.lo.model.LoGroup;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private RestTemplate restTemplate;
    private LoProperties loProperties;
    private KerlinkPropertiesList kerlinkPropertiesList;
    private HttpHeaders authenticationHeaders;
    private HttpEntity<Void> authenticationEntity;
    private Map<String, String> loGroupsMap;

    private final String devicesPagedUrlTemplate;
    private final String groupsPagedUrlTemplate;

    private static final String X_TOTAL_COUNT_HEADER = "X-Total-Count";
    private static final String X_RATELIMIT_REMAINING_HEADER = "X-Ratelimit-Remaining";
    private static final String X_RATELIMIT_RESET_HEADER = "X-Ratelimit-Reset";

    private static final String DEVICES_ENDPOINT = "/v1/deviceMgt/devices";
    private static final String GROOUPS_ENDPOINT = "/v1/deviceMgt/groups";

    public LoDeviceProvider(LoProperties loProperties, KerlinkPropertiesList kerlinkPropertiesList, HttpHeaders authenticationHeaders, @Qualifier("loRestTemplate") RestTemplate restTemplate) {
        this.loProperties = loProperties;
        this.kerlinkPropertiesList = kerlinkPropertiesList;
        this.authenticationHeaders = authenticationHeaders;
        this.restTemplate = restTemplate;

        this.loGroupsMap = new HashMap<>();
        this.authenticationEntity = new HttpEntity<>(authenticationHeaders);
        this.devicesPagedUrlTemplate = loProperties.getApiUrl() + DEVICES_ENDPOINT + "?limit=" + loProperties.getPageSize() + "&offset=%d&groupId=%s&fields=id,name,group";
        this.groupsPagedUrlTemplate = loProperties.getApiUrl() + GROOUPS_ENDPOINT + "?limit=" + loProperties.getPageSize() + "&offset=" + "%d";
    }

    @PostConstruct
    public void postConstruct() {

        LOG.info("Managing group of devices");
        try {
            synchronizeGroups();
        } catch (HttpClientErrorException e) {
            LOG.error("Cannot create group \n {}", e.getResponseBodyAsString());
            System.exit(1);
        } catch (Exception e) {
            LOG.error("Unexpected error while managing group {}", e.getMessage());
            System.exit(1);
        }
    }

    private void synchronizeGroups() {
        ArrayList<LoGroup> emptyList = new ArrayList<>();
        Set<String> kerlinkAccountNames = kerlinkPropertiesList.getKerlinkList().stream().map(KerlinkProperties::getKerlinkAccountName).collect(Collectors.toSet());
        LOG.debug("Trying to get existing groups");

        int retrievedGroups = 0;
        for (int offset = 0;; offset++) {
            try {
                ResponseEntity<LoGroup[]> response = restTemplate.exchange(getPagedGroupsUrl(offset), HttpMethod.GET, authenticationEntity, LoGroup[].class);
                List<LoGroup> loGroups = Optional.ofNullable(response.getBody())
                        .map(Arrays::asList)
                        .orElse(emptyList);

                retrievedGroups += loGroups.size();
                loGroups.forEach(g -> loGroupsMap.put(g.getPathNode(), g.getId()));

                if (loGroups.isEmpty() || retrievedGroups >= getTotalCount(response)) {
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
                HttpEntity<LoGroup> httpEntity = new HttpEntity<>(group, authenticationHeaders);
                ResponseEntity<LoGroup> response = restTemplate.exchange(loProperties.getApiUrl() + GROOUPS_ENDPOINT, HttpMethod.POST, httpEntity, LoGroup.class);
                loGroupsMap.put(accountName, response.getBody().getId());
                LOG.debug("Group {} created", accountName);
            }
        });
        LOG.debug("kerlinkAccountNames: {}", kerlinkAccountNames);
        LOG.debug("existing groups: {}", loGroupsMap);
    }

    public List<LoDevice> getDevices(String groupName) {
        List<LoDevice> devices = new ArrayList<>(loProperties.getPageSize());
        ArrayList<LoDevice> emptyList = new ArrayList<>();

        for (int offset = 0;; offset++) {
            LOG.trace("Calling LO url {}", getPagedDevicesUrl(offset, loGroupsMap.get(groupName)));
            ResponseEntity<LoDevice[]> response = restTemplate.exchange(getPagedDevicesUrl(offset, loGroupsMap.get(groupName)), HttpMethod.GET, authenticationEntity, LoDevice[].class);
            List<LoDevice> loDevices = Optional.ofNullable(response.getBody())
                    .map(Arrays::asList)
                    .orElse(emptyList);

            LOG.trace("Got {} devices", loDevices.size());
            devices.addAll(loDevices);
            if (loDevices.isEmpty() || devices.size() >= getTotalCount(response)) {
                break;
            }
            if (Integer.parseInt(getHeaderValue(response, X_RATELIMIT_REMAINING_HEADER)) == 0) {
                long reset = Long.parseLong(getHeaderValue(response, X_RATELIMIT_RESET_HEADER));
                long current = System.currentTimeMillis();
                try {
                    Thread.sleep(reset - current);
                } catch (Exception e) {
                    LOG.error("Exception while getting devices: {}", e.getMessage());
                }
            }
        }
        LOG.trace("Devices: {}", devices);
        return devices;
    }

    public void addDevice(String deviceId, String kerlinkAccountName) {
        if (LOG.isTraceEnabled()) {
            String cleanDeviceId = StringEscapeUtils.escapeHtml4(deviceId);
            String clearAccountName = StringEscapeUtils.escapeHtml4(kerlinkAccountName);
            LOG.trace("Trying to add device {} to LO group {}", cleanDeviceId, clearAccountName);
        }

        LoDevice device = new LoDevice(deviceId, loGroupsMap.get(kerlinkAccountName), loProperties.getDevicePrefix(), true);
        HttpEntity<LoDevice> httpEntity = new HttpEntity<>(device, authenticationHeaders);

        restTemplate.exchange(loProperties.getApiUrl() + DEVICES_ENDPOINT, HttpMethod.POST, httpEntity, Void.class);
    }

    public void deleteDevice(String deviceId) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Trying to delete device {} from LO", StringEscapeUtils.escapeHtml4(deviceId));
        }
        restTemplate.exchange(loProperties.getApiUrl() + DEVICES_ENDPOINT + "/" + deviceId, HttpMethod.DELETE, authenticationEntity, Void.class);
    }

    private String getPagedDevicesUrl(int offset, String groupName) {
        return String.format(devicesPagedUrlTemplate, offset * loProperties.getPageSize(), groupName);
    }

    private String getPagedGroupsUrl(int offset) {
        return String.format(groupsPagedUrlTemplate, offset * loProperties.getPageSize());
    }

    private static int getTotalCount(ResponseEntity<?> response) {
        String headerValue = getHeaderValue(response, X_TOTAL_COUNT_HEADER);
        return Integer.parseInt(headerValue);
    }

    private static String getHeaderValue(ResponseEntity<?> response, String headerName) {
        List<String> strings = response.getHeaders().get(headerName);
        return strings != null && !strings.isEmpty() ? strings.get(0) : null;
    }
}