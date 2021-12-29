/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo.lo;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkApi;
import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.LOApiClientParameters;
import com.orange.lo.sdk.externalconnector.DataManagementExtConnector;
import com.orange.lo.sdk.externalconnector.DataManagementExtConnectorCommandCallback;
import com.orange.lo.sdk.rest.devicemanagement.DeviceManagement;

@Configuration
@ConfigurationPropertiesScan
public class LoConfig {

    @Bean
    public LOApiClientParameters loApiClientParameters(LoProperties loProperties, DataManagementExtConnectorCommandCallback dataManagementExtConnectorCommandCallback) {
        return LOApiClientParameters.builder()
                .apiKey(loProperties.getApiKey())
                .connectionTimeout(loProperties.getConnectionTimeout())
                .automaticReconnect(loProperties.getAutomaticReconnect())
                .hostname(loProperties.getHostname())
                .messageQos(loProperties.getMessageQos())
                .keepAliveIntervalSeconds(loProperties.getKeepAliveIntervalSeconds())
                .mqttPersistenceDataDir(loProperties.getMqttPersistenceDir())
                .dataManagementExtConnectorCommandCallback(dataManagementExtConnectorCommandCallback)
                .connectorType(loProperties.getConnectorType())
                .connectorVersion(getConnectorVersion())
                .build();
    }

    @Bean
    public LOApiClient loApiclient(LOApiClientParameters parameters) {
        return new LOApiClient(parameters);
    }

    @Bean
    public MessageListener messageListener(CommandMapper commandMapper, Map<String, KerlinkApi> kerlinkApiMap, LoDeviceCache deviceCache) {
        return new MessageListener(commandMapper, kerlinkApiMap, deviceCache);
    }

    @Bean
    public DeviceManagement deviceManagement(LOApiClient loApiClient) {
        return loApiClient.getDeviceManagement();
    }

    @Bean
    public DataManagementExtConnector externalConnector(LOApiClient loApiClient) {
        return loApiClient.getDataManagementExtConnector();
    }

    @Bean
    public GroupCache groupCache() {
        return new GroupCache();
    }
    
    private String getConnectorVersion() {
    	MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = null;
        try {			
	        if ((new File("pom.xml")).exists()) {
	          model = reader.read(new FileReader("pom.xml"));
	        } else {
	          model = reader.read(
	            new InputStreamReader(
	            	LoConfig.class.getResourceAsStream(
	                "/META-INF/maven/com.orange.lo.sample/kerlink2lo/pom.xml"
	              )
	            )
	          );
	        }
	        return model.getVersion().replace(".", "_");
        } catch (Exception e) {
			return "";
		}
    }
}
