/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo.lo;

import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkProperties;
import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkPropertiesList;
import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.rest.devicemanagement.DeviceManagement;
import com.orange.lo.sdk.rest.devicemanagement.GetDevicesFilter;
import com.orange.lo.sdk.rest.devicemanagement.Inventory;
import com.orange.lo.sdk.rest.model.*;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.Policy;
import net.jodah.failsafe.RetryPolicy;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.stream.Collectors;

import static com.orange.lo.sdk.rest.devicemanagement.Groups.DEFAULT_GROUP_ID;

@Component
public class LoDeviceProvider {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final KerlinkPropertiesList kerlinkPropertiesList;
    private final Map<String, Group> loGroupsMap;
    private final Integer pageSize;
    private final Policy<List<Device>> deviceRetryPolicy;
    private final Policy<Group> groupRetryPolicy;
    private final DeviceManagement deviceManagement;
    private final LoDeviceCache deviceCache;

    public LoDeviceProvider(LoProperties loProperties, KerlinkPropertiesList kerlinkPropertiesList, DeviceManagement deviceManagement, LoDeviceCache deviceCache) {
        this.kerlinkPropertiesList = kerlinkPropertiesList;
        this.deviceManagement = deviceManagement;
        this.deviceCache = deviceCache;

        this.loGroupsMap = new HashMap<>();
        pageSize = loProperties.getPageSize();
        deviceRetryPolicy = new RetryPolicy<>();
        groupRetryPolicy = new RetryPolicy<>();
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
        Set<String> kerlinkAccountNames = kerlinkPropertiesList.getKerlinkList().stream().map(KerlinkProperties::getKerlinkAccountName).collect(Collectors.toSet());
        LOG.debug("Trying to get existing groups");

        List<Group> groups = deviceManagement
                .getGroups()
                .getGroups();
        groups.forEach(g -> loGroupsMap.put(g.getPathNode(), g));

        kerlinkAccountNames.forEach(accountName -> {
            if (!loGroupsMap.containsKey(accountName)) {
                LOG.debug("Group {} not found, trying to create new group", accountName);
                Group group = Failsafe.with(groupRetryPolicy)
                        .get(() -> deviceManagement
                                .getGroups()
                                .createGroup(accountName)
                        );
                loGroupsMap.put(accountName, group);
                LOG.debug("Group {} created", accountName);
            }
        });
        LOG.debug("kerlinkAccountNames: {}", kerlinkAccountNames);
        LOG.debug("existing groups: {}", loGroupsMap);
    }

    public List<Device> getDevices(String groupName) {
        List<Device> devices = new ArrayList<>(pageSize);
        Inventory inventory = deviceManagement.getInventory();
        Group group = loGroupsMap.get(groupName);
        String groupIdOrDefault = group != null ? group.getId() : DEFAULT_GROUP_ID;
        for (int offset = 0; ; offset++) {
            GetDevicesFilter devicesFilter = new GetDevicesFilter()
                    .withGroupId(groupIdOrDefault)
                    .withLimit(pageSize)
                    .withOffset(offset * pageSize);
            List<Device> loDevices = Failsafe.with(deviceRetryPolicy)
                    .get(() -> inventory.getDevices(devicesFilter));
            LOG.trace("Got {} devices", loDevices.size());
            devices.addAll(loDevices);
            if (loDevices.size() < pageSize) {
                break;
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

        Group s = loGroupsMap.get(kerlinkAccountName);
        InterfaceCapability interfaceCapability = new InterfaceCapability()
                .withAvailable(true);
        Capabilities capabilities = new Capabilities()
                .withCommand(interfaceCapability);
        Interface anInterface = new Interface()
                .withCapabilities(capabilities);
        Device device = new Device()
                .withGroup(s)
                .withId(deviceId)
                .withInterfaces(Arrays.asList(anInterface));
        Failsafe.with(deviceRetryPolicy)
                .run(() ->
                        deviceManagement
                                .getInventory()
                                .createDevice(device)
                );
        deviceCache.add(kerlinkAccountName, deviceId);
    }

    public void deleteDevice(String deviceId) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Trying to delete device {} from LO", StringEscapeUtils.escapeHtml4(deviceId));
        }
        Failsafe.with(deviceRetryPolicy)
                .run(() ->
                        deviceManagement
                                .getInventory()
                                .deleteDevice(deviceId)
                );
        deviceCache.delete(deviceId);
    }

}