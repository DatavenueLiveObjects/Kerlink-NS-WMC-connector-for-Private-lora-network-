/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo.lo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkApi;
import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.LOApiClientParameters;
import com.orange.lo.sdk.externalconnector.DataManagementExtConnector;
import com.orange.lo.sdk.fifomqtt.DataManagementFifoCallback;
import com.orange.lo.sdk.rest.devicemanagement.DeviceManagement;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Map;

@Configuration
@ConfigurationPropertiesScan
public class LoConfig {

    @Bean
    public LOApiClientParameters loApiClientParameters(LoProperties loProperties, DataManagementFifoCallback dataManagementFifoCallback) {
        return LOApiClientParameters.builder()
                .apiKey(loProperties.getApiKey())
                .connectionTimeout(loProperties.getConnectionTimeout())
                .automaticReconnect(loProperties.getAutomaticReconnect())
                .hostname(loProperties.getHostname())
                .topics(Collections.singletonList(loProperties.getTopic()))
                .messageQos(loProperties.getMessageQos())
                .keepAliveIntervalSeconds(loProperties.getKeepAliveIntervalSeconds())
                .mqttPersistenceDataDir(loProperties.getMqttPersistenceDir())
                .dataManagementMqttCallback(dataManagementFifoCallback)
                .build();
    }

    @Bean
    public LOApiClient LOApiClient(LOApiClientParameters parameters) {
        return new LOApiClient(parameters);
    }

    @Bean
    public DataManagementFifoCallback messageListener(CommandMapper commandMapper, Map<String, KerlinkApi> kerlinkApiMap, LoDeviceCache deviceCache, ObjectMapper objectMapper) {
        return new MessageListener(commandMapper, kerlinkApiMap, deviceCache, objectMapper);
    }

    @Bean
    public DeviceManagement deviceManagement(LOApiClient loApiClient) {
        return loApiClient.getDeviceManagement();
    }

    @Bean
    public DataManagementExtConnector externalConnector(LOApiClient loApiClient) {
        return loApiClient.getDataManagementExtConnector();
    }
}
