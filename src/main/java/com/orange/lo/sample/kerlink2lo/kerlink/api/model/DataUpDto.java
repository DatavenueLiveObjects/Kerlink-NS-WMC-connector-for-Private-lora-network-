/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlink2lo.kerlink.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataUpDto {

    @JsonProperty("id")
    private String id = null;

    @JsonProperty("fPort")
    private Integer fPort = null;

    @JsonProperty("fCntUp")
    private Integer fCntUp = null;

    @JsonProperty("fCntDown")
    private Integer fCntDown = null;

    @JsonProperty("adr")
    private Boolean adr = null;

    @JsonProperty("encrypted")
    private Boolean encrypted = null;

    @JsonProperty("confirmed")
    private Boolean confirmed = null;

    @JsonProperty("payload")
    private String payload = null;

    @JsonProperty("encodingType")
    private String encodingType = null;

    @JsonProperty("recvTime")
    private Long recvTime = null;

    @JsonProperty("classB")
    private Boolean classB = null;

    @JsonProperty("delayed")
    private Boolean delayed = null;

    @JsonProperty("endDevice")
    private EndDeviceDto endDevice = null;

    public Long getRecvTime() {
        return recvTime;
    }

    public void setRecvTime(Long recvTime) {
        this.recvTime = recvTime;
    }

    public Integer getFPort() {
        return fPort;
    }

    public void setFPort(Integer fPort) {
        this.fPort = fPort;
    }

    public Boolean isClassB() {
        return classB;
    }

    public void setClassB(Boolean classB) {
        this.classB = classB;
    }

    public Boolean isDelayed() {
        return delayed;
    }

    public void setDelayed(Boolean delayed) {
        this.delayed = delayed;
    }

    public Integer getFCntUp() {
        return fCntUp;
    }

    public void setFCntUp(Integer fCntUp) {
        this.fCntUp = fCntUp;
    }

    public Boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public Boolean isAdr() {
        return adr;
    }

    public void setAdr(Boolean adr) {
        this.adr = adr;
    }

    public EndDeviceDto getEndDevice() {
        return endDevice;
    }

    public void setEndDevice(EndDeviceDto endDevice) {
        this.endDevice = endDevice;
    }

    public Boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(Boolean encrypted) {
        this.encrypted = encrypted;
    }

    public Integer getFCntDown() {
        return fCntDown;
    }

    public void setFCntDown(Integer fCntDown) {
        this.fCntDown = fCntDown;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "DataUpDto [id=" + id + ", fPort=" + fPort + ", fCntUp=" + fCntUp + ", fCntDown=" + fCntDown + ", adr="
                + adr + ", encrypted=" + encrypted + ", confirmed=" + confirmed + ", payload=" + payload
                + ", encodingType=" + encodingType + ", recvTime=" + recvTime + ", classB=" + classB + ", delayed="
                + delayed + ", endDevice=" + endDevice + "]";
    }
}