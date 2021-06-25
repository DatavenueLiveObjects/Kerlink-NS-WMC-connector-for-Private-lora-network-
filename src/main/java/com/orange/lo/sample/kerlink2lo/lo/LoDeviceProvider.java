/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo.lo;

import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkProperties;
import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkPropertiesList;
import com.orange.lo.sdk.rest.devicemanagement.DeviceManagement;
import com.orange.lo.sdk.rest.devicemanagement.GetDevicesFilter;
import com.orange.lo.sdk.rest.devicemanagement.GetGroupsFilter;
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
    public static final String X_CONNECTOR = "x-connector";
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String ID_PADDING = "urn:lo:nsid:x-connector:";
    private final Integer pageSize;
    private final Policy<Device> deviceRetryPolicy;
    private final Policy<List<Device>> deviceListRetryPolicy;
    private final RetryPolicy<Group> groupRetryPolicy;
    private final RetryPolicy<List<Group>> groupListRetryPolicy;
    private final DeviceManagement deviceManagement;
    private final LoDeviceCache deviceCache;
    private final GroupCache groupCache;
    private final Set<String> kerlinkAccountNames;

    public LoDeviceProvider(LoProperties loProperties,
                            DeviceManagement deviceManagement,
                            LoDeviceCache deviceCache,
                            GroupCache groupCache, KerlinkPropertiesList kerlinkPropertiesList) {
        this.deviceManagement = deviceManagement;
        this.deviceCache = deviceCache;

        this.pageSize = loProperties.getPageSize();
        this.groupCache = groupCache;
        this.deviceRetryPolicy = new RetryPolicy<>();
        this.deviceListRetryPolicy = new RetryPolicy<>();
        this.groupRetryPolicy = new RetryPolicy<>();
        this.groupListRetryPolicy = new RetryPolicy<>();
        this.kerlinkAccountNames = kerlinkPropertiesList.getKerlinkList()
                .stream()
                .map(KerlinkProperties::getKerlinkAccountName)
                .collect(Collectors.toSet());
    }

    public List<Device> getDevices(String groupName) {
        List<Device> devices = new ArrayList<>(pageSize);
        Inventory inventory = deviceManagement.getInventory();
        Group group = groupCache.get(groupName);
        String groupIdOrDefault = group != null ? group.getId() : DEFAULT_GROUP_ID;
        for (int offset = 0; ; offset++) {
            GetDevicesFilter devicesFilter = new GetDevicesFilter()
                    .withGroupId(groupIdOrDefault)
                    .withLimit(pageSize)
                    .withOffset(offset * pageSize);
            List<Device> loDevices = Failsafe.with(deviceListRetryPolicy)
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

        Group group = groupCache.get(kerlinkAccountName);
        Device device = buildDevice(deviceId, group);
        Device added = Failsafe.with(deviceRetryPolicy)
                .get(() ->
                        deviceManagement
                                .getInventory()
                                .createDevice(device)
                );

        LOG.debug("Added device: {}", added);
        deviceCache.add(deviceId, kerlinkAccountName);
    }

    private Device buildDevice(String deviceId, Group group) {
        InterfaceCapability interfaceCapability = new InterfaceCapability()
                .withAvailable(true);
        Capabilities capabilities = new Capabilities()
                .withCommand(interfaceCapability);
        Definition definition = new Definition()
                .withClientId(deviceId);
        Interface anInterface = new Interface()
                .withConnector(X_CONNECTOR)
                .withDefinition(definition)
                .withCapabilities(capabilities);
        return new Device()
                .withGroup(group)
                .withId(ID_PADDING + deviceId)
                .withName(deviceId)
                .withInterfaces(Collections.singletonList(anInterface));
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
        LOG.debug("Trying to get existing groups");

        Map<String, Group> allGroups = retrieveGroups();
        this.groupCache.putAll(allGroups);

        this.kerlinkAccountNames.forEach(accountName -> {
            if (!allGroups.containsKey(accountName)) {
                LOG.debug("Group {} not found, trying to create new group", accountName);
                Group group = Failsafe.with(this.groupRetryPolicy)
                        .get(() -> this.deviceManagement
                                .getGroups()
                                .createGroup(accountName)
                        );
                this.groupCache.put(accountName, group);
                LOG.debug("Group {} created", accountName);
            }
        });
        LOG.debug("kerlinkAccountNames: {}", this.kerlinkAccountNames);
        LOG.debug("existing groups: {}", this.groupCache);
    }

    public Map<String, Group> retrieveGroups() {
        Map<String, Group> allGroups = new HashMap<>();
        for (int offset = 0; ; offset++) {
            GetGroupsFilter getGroupsFilter = new GetGroupsFilter()
                    .withLimit(this.pageSize)
                    .withOffset(offset * this.pageSize);
            List<Group> groupSubset = Failsafe.with(this.groupListRetryPolicy)
                    .get(() -> this.deviceManagement.getGroups().getGroups(getGroupsFilter));
            groupSubset.forEach(g -> allGroups.put(g.getPathNode(), g));

            if (groupSubset.size() < this.pageSize) {
                break;
            }
        }

        return allGroups;
    }

}
