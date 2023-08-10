/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo.kerlink.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataUpDto {

    @JsonProperty("id")
    private String id = null;

    @JsonProperty("adr")
    private Boolean adr = null;

    @JsonProperty("classB")
    private Boolean classB = null;

    @JsonProperty("codingRate")
    private String codingRate = null;

    @JsonProperty("confirmed")
    private Boolean confirmed = null;

    @JsonProperty("dataRate")
    private String dataRate = null;

    @JsonProperty("delayed")
    private Boolean delayed = null;

    @JsonProperty("encodingType")
    private String encodingType = null;

    @JsonProperty("encrypted")
    private Boolean encrypted = null;

    @JsonProperty("endDevice")
    private EndDeviceDto endDevice = null;

    @JsonProperty("fCntDown")
    private Integer fCntDown = null;

    @JsonProperty("fCntUp")
    private Integer fCntUp = null;

    @JsonProperty("fPort")
    private Integer fPort = null;

    @JsonProperty("gwCnt")
    private Integer gwCnt = null;

    @JsonProperty("gwInfo")
    private List<GatewayInfoDto> gwInfo = null;

    @JsonProperty("gwRecvTime")
    private Long gwRecvTime = null;

    @JsonProperty("modulation")
    private String modulation = null;

    @JsonProperty("payload")
    private String payload = null;

    @JsonProperty("recvTime")
    private Long recvTime = null;

    @JsonProperty("ulFrequency")
    private Double ulFrequency = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getAdr() {
        return adr;
    }

    public void setAdr(Boolean adr) {
        this.adr = adr;
    }

    public Boolean getClassB() {
        return classB;
    }

    public void setClassB(Boolean classB) {
        this.classB = classB;
    }

    public String getCodingRate() {
        return codingRate;
    }

    public void setCodingRate(String codingRate) {
        this.codingRate = codingRate;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getDataRate() {
        return dataRate;
    }

    public void setDataRate(String dataRate) {
        this.dataRate = dataRate;
    }

    public Boolean getDelayed() {
        return delayed;
    }

    public void setDelayed(Boolean delayed) {
        this.delayed = delayed;
    }

    public String getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    public Boolean getEncrypted() {
        return encrypted;
    }

    public void setEncrypted(Boolean encrypted) {
        this.encrypted = encrypted;
    }

    public EndDeviceDto getEndDevice() {
        return endDevice;
    }

    public void setEndDevice(EndDeviceDto endDevice) {
        this.endDevice = endDevice;
    }

    public Integer getFCntDown() {
        return fCntDown;
    }

    public void setFCntDown(Integer fCntDown) {
        this.fCntDown = fCntDown;
    }

    public Integer getFCntUp() {
        return fCntUp;
    }

    public void setFCntUp(Integer fCntUp) {
        this.fCntUp = fCntUp;
    }

    public Integer getFPort() {
        return fPort;
    }

    public void setFPort(Integer fPort) {
        this.fPort = fPort;
    }

    public Integer getGwCnt() {
        return gwCnt;
    }

    public void setGwCnt(Integer gwCnt) {
        this.gwCnt = gwCnt;
    }

    public List<GatewayInfoDto> getGwInfo() {
        return gwInfo;
    }

    public void setGwInfo(List<GatewayInfoDto> gwInfo) {
        this.gwInfo = gwInfo;
    }

    public Long getGwRecvTime() {
        return gwRecvTime;
    }

    public void setGwRecvTime(Long gwRecvTime) {
        this.gwRecvTime = gwRecvTime;
    }

    public String getModulation() {
        return modulation;
    }

    public void setModulation(String modulation) {
        this.modulation = modulation;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Long getRecvTime() {
        return recvTime;
    }

    public void setRecvTime(Long recvTime) {
        this.recvTime = recvTime;
    }

    public Double getUlFrequency() {
        return ulFrequency;
    }

    public void setUlFrequency(Double ulFrequency) {
        this.ulFrequency = ulFrequency;
    }

    @Override
    public String toString() {
        return "DataUpDto{" +
                "id='" + id + '\'' +
                ", adr=" + adr +
                ", classB=" + classB +
                ", codingRate='" + codingRate + '\'' +
                ", confirmed=" + confirmed +
                ", dataRate='" + dataRate + '\'' +
                ", delayed=" + delayed +
                ", encodingType='" + encodingType + '\'' +
                ", encrypted=" + encrypted +
                ", endDevice=" + endDevice +
                ", fCntDown=" + fCntDown +
                ", fCntUp=" + fCntUp +
                ", fPort=" + fPort +
                ", gwCnt=" + gwCnt +
                ", gwInfo=" + gwInfo +
                ", gwRecvTime=" + gwRecvTime +
                ", modulation='" + modulation + '\'' +
                ", payload='" + payload + '\'' +
                ", recvTime=" + recvTime +
                ", ulFrequency=" + ulFrequency +
                '}';
    }
}
