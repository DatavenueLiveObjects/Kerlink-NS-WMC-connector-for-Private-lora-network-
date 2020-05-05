/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlink2lo;

import com.orange.lo.sample.kerlink2lo.lo.LoProperties;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Kerlink2LoApplicationConfig {

    private LoProperties loProperties;
    
    public Kerlink2LoApplicationConfig(LoProperties loProperties) {
        this.loProperties = loProperties;
    }
    
    @Bean
    WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
                ThreadPoolTaskExecutor t = new ThreadPoolTaskExecutor();
                t.setCorePoolSize(loProperties.getMessageSenderMinThreadPoolSize());
                t.setMaxPoolSize(loProperties.getMessageSenderMaxThreadPoolSize());
                t.initialize();
                configurer.setTaskExecutor(t);
            }
        };
    }
    
    @Bean
    public IMqttClient loMqttClient() throws MqttException {
        MqttConnectOptions opts = new MqttConnectOptions();
        opts.setUserName(loProperties.getConnectorUser());
        opts.setPassword(loProperties.getConnectorApiKey().toCharArray());
        opts.setAutomaticReconnect(true);
        IMqttClient client = new MqttClient(loProperties.getConnectorMqttUrl(), UUID.randomUUID().toString());
        client.connect(opts);
        return client;
    }
    
    
}