/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo.lo;

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

import java.lang.invoke.MethodHandles;
import java.util.*;

import static com.orange.lo.sdk.rest.devicemanagement.Groups.DEFAULT_GROUP_ID;

@Component
public class LoDeviceProvider {
    public static final String X_CONNECTOR = "x-connector";
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String ID_PADDING = "urn:lo:nsid:x-connector:";
    private final Integer pageSize;
    private final Policy<Device> deviceRetryPolicy;
    private final Policy<List<Device>> deviceListRetryPolicy;
    private final DeviceManagement deviceManagement;
    private final LoDeviceCache deviceCache;
    private final GroupCache groupCache;

    public LoDeviceProvider(LoProperties loProperties,
                            DeviceManagement deviceManagement,
                            LoDeviceCache deviceCache,
                            GroupCache groupCache) {
        this.deviceManagement = deviceManagement;
        this.deviceCache = deviceCache;

        pageSize = loProperties.getPageSize();
        this.groupCache = groupCache;
        deviceRetryPolicy = new RetryPolicy<>();
        deviceListRetryPolicy = new RetryPolicy<>();
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
        deviceCache.add(kerlinkAccountName, deviceId);
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

}
