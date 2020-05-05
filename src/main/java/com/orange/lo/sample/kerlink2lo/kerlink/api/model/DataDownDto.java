package com.orange.lo.sample.kerlink2lo.kerlink.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) 
public class DataDownDto {
    @JsonProperty("fPort") private Integer fPort = null;

    @JsonProperty("endDevice") private EndDeviceDto endDevice = null;

    @JsonProperty("payload") private String payload = null;

    @JsonProperty("contentType") private String contentType = null;

    @JsonProperty("confirmed") private Boolean confirmed = null;

    @JsonProperty("ttl") private Integer ttl = null;

    @JsonProperty("maxAttempts") private Integer maxAttempts = null;

    public Integer getfPort() {
        return fPort;
    }

    public void setfPort(Integer fPort) {
        this.fPort = fPort;
    }

    public EndDeviceDto getEndDevice() {
        return endDevice;
    }

    public void setEndDevice(EndDeviceDto endDevice) {
        this.endDevice = endDevice;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    @Override
    public String toString() {
        return "DataDownDto [fPort=" + fPort + ", endDevice=" + endDevice + ", payload=" + payload + ", contentType=" + contentType + ", confirmed=" + confirmed + ", ttl=" + ttl + ", maxAttempts=" + maxAttempts + "]";
    }

//    @Override public String toString() {
//        return "DataDownDto [fPort=" + fPort + ", endDevice=" + endDevice + ", payload=" + payload + ", contentType=" + contentType + "]";
//    }
}
