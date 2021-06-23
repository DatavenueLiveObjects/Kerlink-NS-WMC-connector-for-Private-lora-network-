package com.orange.lo.sample.kerlink2lo.lo;

public class LoPropertiesTestData {
    public static LoProperties loPropertiesTestData() {
        String hostname = "http://localhost:8080/";
        String apiKey = "apikey";
        Integer messageQos = 1;
        String mqttPersistenceDir = "temp";
        Integer keepAliveIntervalSeconds = 90;
        Integer connectionTimeout = 10000;
        Boolean automaticReconnect = true;
        String messageDecoder = "";
        Integer pageSize = 10;
        return new LoProperties(hostname,
                apiKey,
                messageQos,
                mqttPersistenceDir,
                keepAliveIntervalSeconds,
                connectionTimeout,
                automaticReconnect,
                messageDecoder,
                pageSize);
    }
}
