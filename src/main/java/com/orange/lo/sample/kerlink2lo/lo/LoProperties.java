/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlink2lo.lo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "lo")
public class LoProperties {

    private String apiKey;
    private String apiUrl;
    private int pageSize;
    private int synchronizationThreadPoolSize;
    private int messageSenderMaxThreadPoolSize;
    private int messageSenderMinThreadPoolSize;
    private int messageQos;
    private String connectorApiKey;
    private String connectorUser;
    private String connectorMqttUrl;
    private String deviceGroupName;
    private String deviceGroupId;
    private String devicePrefix;
    private String messageDecoder;
    
    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getSynchronizationThreadPoolSize() {
        return synchronizationThreadPoolSize;
    }

    public void setSynchronizationThreadPoolSize(int synchronizationThreadPoolSize) {
        this.synchronizationThreadPoolSize = synchronizationThreadPoolSize;
    }

    public int getMessageSenderMaxThreadPoolSize() {
        return messageSenderMaxThreadPoolSize;
    }

    public void setMessageSenderMaxThreadPoolSize(int messageSenderMaxThreadPoolSize) {
        this.messageSenderMaxThreadPoolSize = messageSenderMaxThreadPoolSize;
    }

    public int getMessageSenderMinThreadPoolSize() {
        return messageSenderMinThreadPoolSize;
    }

    public void setMessageSenderMinThreadPoolSize(int messageSenderMinThreadPoolSize) {
        this.messageSenderMinThreadPoolSize = messageSenderMinThreadPoolSize;
    }

    public int getMessageQos() {
        return messageQos;
    }

    public void setMessageQos(int messageQos) {
        this.messageQos = messageQos;
    }

    public String getConnectorApiKey() {
        return connectorApiKey;
    }

    public void setConnectorApiKey(String connectorApiKey) {
        this.connectorApiKey = connectorApiKey;
    }

    public String getConnectorUser() {
        return connectorUser;
    }

    public void setConnectorUser(String connectorUser) {
        this.connectorUser = connectorUser;
    }

    public String getConnectorMqttUrl() {
        return connectorMqttUrl;
    }

    public void setConnectorMqttUrl(String connectorMqttUrl) {
        this.connectorMqttUrl = connectorMqttUrl;
    }

    public String getDeviceGroupId() {
        return deviceGroupId;
    }

    public void setDeviceGroupId(String deviceGroupId) {
        this.deviceGroupId = deviceGroupId;
    }

    public String getDevicePrefix() {
        return devicePrefix;
    }

    public void setDevicePrefix(String devicePrefix) {
        this.devicePrefix = devicePrefix;
    }

    public String getMessageDecoder() {
        return messageDecoder;
    }

    public void setMessageDecoder(String messageDecoder) {
        this.messageDecoder = messageDecoder;
    }

    public String getDeviceGroupName() {
        return deviceGroupName;
    }

    public void setDeviceGroupName(String deviceGroupName) {
        this.deviceGroupName = deviceGroupName;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
}
