package com.orange.lo.sample.kerlink2lo.lo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "lo")
@ConstructorBinding
public class LoProperties {

    private final String hostname;
    private final String apiKey;
    private final String topic;
    private final String mqttPersistenceDir;
    private final String messageDecoder;
    private final Boolean automaticReconnect;
    private final Integer messageQos;
    private final Integer keepAliveIntervalSeconds;
    private final Integer connectionTimeout;
    private final Integer pageSize;

    public LoProperties(
            String hostname,
            String apiKey,
            String topic,
            Integer messageQos,
            String mqttPersistenceDir,
            Integer keepAliveIntervalSeconds,
            Integer connectionTimeout,
            Boolean automaticReconnect, String messageDecoder, Integer pageSize) {
        this.hostname = hostname;
        this.apiKey = apiKey;
        this.topic = topic;
        this.messageQos = messageQos;
        this.mqttPersistenceDir = mqttPersistenceDir;
        this.keepAliveIntervalSeconds = keepAliveIntervalSeconds;
        this.connectionTimeout = connectionTimeout;
        this.automaticReconnect = automaticReconnect;
        this.messageDecoder = messageDecoder;
        this.pageSize = pageSize;
    }

    public String getHostname() {
        return hostname;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getTopic() {
        return topic;
    }

    public Integer getMessageQos() {
        return messageQos;
    }

    public String getMqttPersistenceDir() {
        return mqttPersistenceDir;
    }

    public Integer getKeepAliveIntervalSeconds() {
        return keepAliveIntervalSeconds;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public Boolean getAutomaticReconnect() {
        return automaticReconnect;
    }

    public String getMessageDecoder() {
        return messageDecoder;
    }

    public Integer getPageSize() {
        return pageSize;
    }
}