/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlink2lo;

import com.orange.lo.sample.kerlink2lo.kerlink.api.KerlinkApi;
import com.orange.lo.sample.kerlink2lo.lo.ExternalConnectorService;
import com.orange.lo.sample.kerlink2lo.lo.LoDeviceCache;
import com.orange.lo.sample.kerlink2lo.lo.LoDeviceProvider;
import com.orange.lo.sample.kerlink2lo.lo.LoProperties;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@EnableScheduling
@Component
public class IotDeviceManagement {

    private static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private LoProperties loProperties;
    private KerlinkApi kerlinkApi;
    private LoDeviceProvider loDeviceProvider;
    private ExternalConnectorService externalConnectorService;
    private LoDeviceCache deviceCache;
    
    public IotDeviceManagement(KerlinkApi kerlinkApi, LoDeviceProvider loDeviceProvider, ExternalConnectorService externalConnectorService, LoProperties loProperties, LoDeviceCache deviceCache) {
        this.kerlinkApi = kerlinkApi;
        this.loDeviceProvider = loDeviceProvider;
        this.externalConnectorService = externalConnectorService;
        this.loProperties = loProperties;
        this.deviceCache = deviceCache;
    }
    
    @Scheduled(fixedRateString = "${lo.synchronization-device-interval}")
    public void synchronizeDevices() throws InterruptedException {
        LOG.info("Synchronizing devices... ");

        try {
            Set<String> kerlinkIds = kerlinkApi.getEndDevices().stream().map(d -> d.getDevEui()).collect(Collectors.toSet());
            LOG.debug("Got {} devices from Kerlink", kerlinkIds.size());
            
            Set<String> loIds = loDeviceProvider.getDevices().stream().map(d -> d.getId()).collect(Collectors.toSet());
            LOG.debug("Got {} devices from LO", loIds.size());
            Set<String> loIdsWithoutPrefix = loIds.stream().map(loId -> loId.substring(loProperties.getDevicePrefix().length())).collect(Collectors.toSet());
            deviceCache.addAll(loIdsWithoutPrefix);
            
            // add devices to LO
            Set<String> devicesToAddToLo = new HashSet<String>(kerlinkIds);
            devicesToAddToLo.removeAll(loIdsWithoutPrefix);   
            LOG.debug("Devices to add to LO: {}", devicesToAddToLo.toString());
            
            // remove devices from LO
            Set<String> devicesToRemoveFromLo = new HashSet<String>(loIds);
            devicesToRemoveFromLo.removeAll(kerlinkIds.stream().map(kerlinkId -> loProperties.getDevicePrefix() + kerlinkId).collect(Collectors.toSet()));
            LOG.debug("Devices to remove from LO: {}", devicesToRemoveFromLo.toString());
            
            if (devicesToAddToLo.size() + devicesToRemoveFromLo.size() > 0) {
                ThreadPoolExecutor synchronizingExecutor = new ThreadPoolExecutor(
                        loProperties.getSynchronizationThreadPoolSize(), loProperties.getSynchronizationThreadPoolSize(),
                        10, TimeUnit.SECONDS,
                        new ArrayBlockingQueue<>(devicesToAddToLo.size() + devicesToRemoveFromLo.size()));
                
                for (String deviceId : devicesToAddToLo) {
                    synchronizingExecutor.execute(() -> {
                        externalConnectorService.createDevice(deviceId);
                        LOG.debug("Device created for {}", deviceId);
                    });
                }
                for (String deviceId : devicesToRemoveFromLo) {
                    synchronizingExecutor.execute(() -> {
                        externalConnectorService.deleteDevice(deviceId);
                        LOG.debug("Device deleted for {}", deviceId);
                    });
                }
            }
        } catch (HttpClientErrorException e) {
            LOG.error("Error in device synchronization process \n{}", e.getResponseBodyAsString());
        } catch (Exception e) {
            LOG.error("Error in device synchronization process \n{}", e);
        }
    }
}
