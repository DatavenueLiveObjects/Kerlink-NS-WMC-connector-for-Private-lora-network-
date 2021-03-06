/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlink2lo;

import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkPropertiesList;
import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkApi;
import com.orange.lo.sample.kerlink2lo.kerlink.model.EndDeviceDto;
import com.orange.lo.sample.kerlink2lo.lo.ExternalConnectorService;
import com.orange.lo.sample.kerlink2lo.lo.LoDeviceCache;
import com.orange.lo.sample.kerlink2lo.lo.LoDeviceProvider;
import com.orange.lo.sample.kerlink2lo.lo.LoProperties;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.orange.lo.sample.kerlink2lo.lo.model.LoDevice;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@EnableScheduling
@Component
public class IotDeviceManagement {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private LoProperties loProperties;
    private KerlinkPropertiesList kerlinkPropertiesList;
    private Map<String, KerlinkApi> kerlinkApiMap;
    private LoDeviceProvider loDeviceProvider;
    private ExternalConnectorService externalConnectorService;
    private LoDeviceCache deviceCache;

    public IotDeviceManagement(Map<String, KerlinkApi> kerlinkApiMap, LoDeviceProvider loDeviceProvider, ExternalConnectorService externalConnectorService, LoProperties loProperties, KerlinkPropertiesList kerlinkPropertiesList, LoDeviceCache deviceCache) {
        this.kerlinkApiMap = kerlinkApiMap;
        this.loDeviceProvider = loDeviceProvider;
        this.externalConnectorService = externalConnectorService;
        this.loProperties = loProperties;
        this.kerlinkPropertiesList = kerlinkPropertiesList;
        this.deviceCache = deviceCache;
    }

    @Scheduled(fixedRateString = "${lo.synchronization-device-interval}")
    public void synchronizeDevices() {
        kerlinkPropertiesList.getKerlinkList().forEach(kerlinkProperties -> {
            String kerlinkAccountName = kerlinkProperties.getKerlinkAccountName();
            LOG.info("Synchronizing devices for group {}", kerlinkAccountName);

            try {
                Set<String> kerlinkIds = kerlinkApiMap.get(kerlinkProperties.getKerlinkAccountName())
                        .getEndDevices()
                        .stream()
                        .map(endDeviceDto -> StringEscapeUtils.escapeJava(endDeviceDto.getDevEui()))
                        .collect(Collectors.toSet());
                LOG.debug("Got {} devices from Kerlink", kerlinkIds.size());

                Set<String> loIds = loDeviceProvider.getDevices(kerlinkAccountName)
                        .stream()
                        .map(loDevice -> StringEscapeUtils.escapeJava(loDevice.getId()))
                        .collect(Collectors.toSet());
                LOG.debug("Got {} devices from LO", loIds.size());
                Set<String> loIdsWithoutPrefix = loIds.stream().map(loId -> loId.substring(loProperties.getDevicePrefix().length())).collect(Collectors.toSet());
                deviceCache.addAll(loIdsWithoutPrefix, kerlinkAccountName);

                // add devices to LO
                Set<String> devicesToAddToLo = new HashSet<>(kerlinkIds);
                devicesToAddToLo.removeAll(loIdsWithoutPrefix);
                LOG.debug("Devices to add to LO: {}", devicesToAddToLo);

                // remove devices from LO
                Set<String> devicesToRemoveFromLo = new HashSet<>(loIds);
                devicesToRemoveFromLo.removeAll(kerlinkIds.stream().map(kerlinkId -> loProperties.getDevicePrefix() + kerlinkId).collect(Collectors.toSet()));
                LOG.debug("Devices to remove from LO: {}", devicesToRemoveFromLo);

                if (devicesToAddToLo.size() + devicesToRemoveFromLo.size() > 0) {
                    ThreadPoolExecutor synchronizingExecutor = new ThreadPoolExecutor(loProperties.getSynchronizationThreadPoolSize(), loProperties.getSynchronizationThreadPoolSize(), 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(devicesToAddToLo.size() + devicesToRemoveFromLo.size()));

                    for (String deviceId : devicesToAddToLo) {
                        synchronizingExecutor.execute(() -> {
                            externalConnectorService.createDevice(deviceId, kerlinkAccountName);
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Device created for {}", StringEscapeUtils.escapeHtml4(deviceId));
                            }
                        });
                    }
                    for (String deviceId : devicesToRemoveFromLo) {
                        synchronizingExecutor.execute(() -> {
                            externalConnectorService.deleteDevice(deviceId);
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Device deleted for {}", StringEscapeUtils.escapeHtml4(deviceId));
                            }
                        });
                    }
                }
            } catch (HttpClientErrorException e) {
                LOG.error("Error in device synchronization process \n{}", e.getResponseBodyAsString());
            } catch (Exception e) {
                LOG.error("Error in device synchronization process \n{} \n {}", e.getMessage(), e);
            }
        });
    }
}
