/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo;

import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkApi;
import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkPropertiesList;
import com.orange.lo.sample.kerlink2lo.lo.LoApiExternalConnectorService;
import com.orange.lo.sample.kerlink2lo.lo.LoDeviceCache;
import com.orange.lo.sample.kerlink2lo.lo.LoDeviceProvider;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EnableScheduling
@Component
public class IotDeviceManagement {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final KerlinkPropertiesList kerlinkPropertiesList;
    private final Map<String, KerlinkApi> kerlinkApiMap;
    private final LoDeviceProvider loDeviceProvider;
    private final LoApiExternalConnectorService externalConnectorService;
    private final LoDeviceCache deviceCache;
    private static final int SYNCHRONIZATION_THREAD_POOL_SIZE = 10;
    private static final String devicePrefix = "urn:lo:nsid:x-connector:";

    public IotDeviceManagement(Map<String, KerlinkApi> kerlinkApiMap, LoDeviceProvider loDeviceProvider, LoApiExternalConnectorService externalConnectorService, KerlinkPropertiesList kerlinkPropertiesList, LoDeviceCache deviceCache) {
        this.kerlinkApiMap = kerlinkApiMap;
        this.loDeviceProvider = loDeviceProvider;
        this.externalConnectorService = externalConnectorService;
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
                Set<String> loIdsWithoutPrefix = loIds.stream().map(loId -> loId.substring(devicePrefix.length())).collect(Collectors.toSet());
                deviceCache.addAll(loIdsWithoutPrefix, kerlinkAccountName);

                // add devices to LO
                Set<String> devicesToAddToLo = new HashSet<>(kerlinkIds);
                devicesToAddToLo.removeAll(loIdsWithoutPrefix);
                LOG.debug("Devices to add to LO: {}", devicesToAddToLo);

                // remove devices from LO
                Set<String> devicesToRemoveFromLo = new HashSet<>(loIds);
                devicesToRemoveFromLo.removeAll(kerlinkIds.stream().map(kerlinkId -> devicePrefix + kerlinkId).collect(Collectors.toSet()));
                LOG.debug("Devices to remove from LO: {}", devicesToRemoveFromLo);

                // update device status to LO
                Set<String> devicesToSetStatus = new HashSet<>(kerlinkIds);
                devicesToSetStatus.retainAll(loIdsWithoutPrefix);
                devicesToAddToLo.removeAll(loIdsWithoutPrefix);
                LOG.debug("Devices to update status to LO: {}", devicesToSetStatus);

                if (devicesToAddToLo.size() + devicesToRemoveFromLo.size() > 0 || !devicesToSetStatus.isEmpty()) {
                    ThreadPoolExecutor synchronizingExecutor = new ThreadPoolExecutor(SYNCHRONIZATION_THREAD_POOL_SIZE, SYNCHRONIZATION_THREAD_POOL_SIZE, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(devicesToAddToLo.size() + devicesToRemoveFromLo.size() + devicesToSetStatus.size()));

                    List<Callable<Void>> createTasks = devicesToAddToLo.stream().map(deviceId -> (Callable<Void>) () -> {
                    	externalConnectorService.createDevice(deviceId, kerlinkAccountName);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Device created for {}", StringEscapeUtils.escapeHtml4(deviceId));
                        }
                        return null;
                    }).collect(Collectors.toList());
                    
                    List<Callable<Void>> deleteTasks = devicesToRemoveFromLo.stream().map(deviceId -> (Callable<Void>) () -> {
                    	externalConnectorService.deleteDevice(deviceId);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Device deleted for {}", StringEscapeUtils.escapeHtml4(deviceId));
                        }
                        return null;
                    }).collect(Collectors.toList());

                    List<Callable<Void>> updateTasks = devicesToSetStatus.stream().map(deviceId -> (Callable<Void>) () -> {
                        externalConnectorService.setDeviceStatus(deviceId);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Device status updated for {}", StringEscapeUtils.escapeHtml4(deviceId));
                        }
                        return null;
                    }).collect(Collectors.toList());


                    List<Callable<Void>> tasks = Stream.concat(Stream.concat(createTasks.stream(), deleteTasks.stream()), updateTasks.stream()).collect(Collectors.toList());
                    synchronizingExecutor.invokeAll(tasks);
                    synchronizingExecutor.shutdown();
                }
            } catch (HttpClientErrorException e) {
                LOG.error("Error in device synchronization process \n{}", e.getResponseBodyAsString());
            } catch (Exception e) {
                LOG.error("Error in device synchronization process \n{} \n {}", e.getMessage(), e);
            }
        });
    }
}