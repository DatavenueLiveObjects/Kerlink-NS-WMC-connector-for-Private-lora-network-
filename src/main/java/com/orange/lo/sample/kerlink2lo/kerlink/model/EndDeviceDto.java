/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo.kerlink.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EndDeviceDto {

    @JsonProperty("devEui")
    private String devEui = null;

    @JsonProperty("activation")
    private String activation = null;

    @JsonProperty("classType")
    private String classType = null;

    @JsonProperty("cluster")
    private ClusterDto cluster = null;

    @JsonProperty("geolocation")
    private String geolocation = null;

    @JsonProperty("macVersion")
    private String macVersion = null;

    @JsonProperty("profile")
    private String profile = null;

    @JsonProperty("regParamsRevision")
    private String regParamsRevision = null;

    @JsonProperty("rfRegion")
    private String rfRegion = null;

    @JsonProperty("adrEnabled")
    private Boolean adrEnabled = null;

    @JsonProperty("altitude")
    private Integer altitude = null;

    @JsonProperty("appEui")
    private String appEui = null;

    @JsonProperty("appKey")
    private String appKey = null;

    @JsonProperty("appSKey")
    private String appSKey = null;

    @JsonProperty("cfList")
    private List<String> cfList = null;

    @JsonProperty("country")
    private String country = null;

    @JsonProperty("devAddr")
    private String devAddr = null;

    @JsonProperty("devNonceCounter")
    private Boolean devNonceCounter = null;

    @JsonProperty("dwellTime")
    private Boolean dwellTime = null;

    @JsonProperty("fNwkSIntKey")
    private String fNwkSIntKey = null;

    @JsonProperty("fcntDown")
    private Integer fcntDown = null;

    @JsonProperty("fcntUp")
    private Integer fcntUp = null;

    @JsonProperty("latitude")
    private Double latitude = null;

    @JsonProperty("longitude")
    private Double longitude = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("nwkSKey")
    private String nwkSKey = null;

    @JsonProperty("pingSlotDr")
    private String pingSlotDr = null;

    @JsonProperty("pingSlotFreq")
    private Double pingSlotFreq = null;

    @JsonProperty("rx1Delay")
    private Integer rx1Delay = null;

    @JsonProperty("rx1DrOffset")
    private Integer rx1DrOffset = null;

    @JsonProperty("rx2Dr")
    private Integer rx2Dr = null;

    @JsonProperty("rx2Freq")
    private Double rx2Freq = null;

    @JsonProperty("rxWindows")
    private String rxWindows = null;

    @JsonProperty("sNwkSIntKey")
    private String sNwkSIntKey = null;

    @JsonProperty("lastDataDownDate")
    private Integer lastDataDownDate = null;

    @JsonProperty("lastDataUpDataRate")
    private String lastDataUpDataRate = null;

    @JsonProperty("lastDataUpDate")
    private Integer lastDataUpDate = null;

    @JsonProperty("status")
    private String status = null;

    @JsonProperty("links")
    private List<LinkDto> links = null;

    public String getDevEui() {
        return devEui;
    }

    public void setDevEui(String devEui) {
        this.devEui = devEui;
    }

    public String getActivation() {
        return activation;
    }

    public void setActivation(String activation) {
        this.activation = activation;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public ClusterDto getCluster() {
        return cluster;
    }

    public void setCluster(ClusterDto cluster) {
        this.cluster = cluster;
    }

    public String getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(String geolocation) {
        this.geolocation = geolocation;
    }

    public String getMacVersion() {
        return macVersion;
    }

    public void setMacVersion(String macVersion) {
        this.macVersion = macVersion;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getRegParamsRevision() {
        return regParamsRevision;
    }

    public void setRegParamsRevision(String regParamsRevision) {
        this.regParamsRevision = regParamsRevision;
    }

    public String getRfRegion() {
        return rfRegion;
    }

    public void setRfRegion(String rfRegion) {
        this.rfRegion = rfRegion;
    }

    public Boolean getAdrEnabled() {
        return adrEnabled;
    }

    public void setAdrEnabled(Boolean adrEnabled) {
        this.adrEnabled = adrEnabled;
    }

    public Integer getAltitude() {
        return altitude;
    }

    public void setAltitude(Integer altitude) {
        this.altitude = altitude;
    }

    public String getAppEui() {
        return appEui;
    }

    public void setAppEui(String appEui) {
        this.appEui = appEui;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSKey() {
        return appSKey;
    }

    public void setAppSKey(String appSKey) {
        this.appSKey = appSKey;
    }

    public List<String> getCfList() {
        return cfList;
    }

    public void setCfList(List<String> cfList) {
        this.cfList = cfList;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDevAddr() {
        return devAddr;
    }

    public void setDevAddr(String devAddr) {
        this.devAddr = devAddr;
    }

    public Boolean getDevNonceCounter() {
        return devNonceCounter;
    }

    public void setDevNonceCounter(Boolean devNonceCounter) {
        this.devNonceCounter = devNonceCounter;
    }

    public Boolean getDwellTime() {
        return dwellTime;
    }

    public void setDwellTime(Boolean dwellTime) {
        this.dwellTime = dwellTime;
    }

    public String getFNwkSIntKey() {
        return fNwkSIntKey;
    }

    public void setFNwkSIntKey(String fNwkSIntKey) {
        this.fNwkSIntKey = fNwkSIntKey;
    }

    public Integer getFcntDown() {
        return fcntDown;
    }

    public void setFcntDown(Integer fcntDown) {
        this.fcntDown = fcntDown;
    }

    public Integer getFcntUp() {
        return fcntUp;
    }

    public void setFcntUp(Integer fcntUp) {
        this.fcntUp = fcntUp;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNwkSKey() {
        return nwkSKey;
    }

    public void setNwkSKey(String nwkSKey) {
        this.nwkSKey = nwkSKey;
    }

    public String getPingSlotDr() {
        return pingSlotDr;
    }

    public void setPingSlotDr(String pingSlotDr) {
        this.pingSlotDr = pingSlotDr;
    }

    public Double getPingSlotFreq() {
        return pingSlotFreq;
    }

    public void setPingSlotFreq(Double pingSlotFreq) {
        this.pingSlotFreq = pingSlotFreq;
    }

    public Integer getRx1Delay() {
        return rx1Delay;
    }

    public void setRx1Delay(Integer rx1Delay) {
        this.rx1Delay = rx1Delay;
    }

    public Integer getRx1DrOffset() {
        return rx1DrOffset;
    }

    public void setRx1DrOffset(Integer rx1DrOffset) {
        this.rx1DrOffset = rx1DrOffset;
    }

    public Integer getRx2Dr() {
        return rx2Dr;
    }

    public void setRx2Dr(Integer rx2Dr) {
        this.rx2Dr = rx2Dr;
    }

    public Double getRx2Freq() {
        return rx2Freq;
    }

    public void setRx2Freq(Double rx2Freq) {
        this.rx2Freq = rx2Freq;
    }

    public String getRxWindows() {
        return rxWindows;
    }

    public void setRxWindows(String rxWindows) {
        this.rxWindows = rxWindows;
    }

    public String getSNwkSIntKey() {
        return sNwkSIntKey;
    }

    public void setSNwkSIntKey(String sNwkSIntKey) {
        this.sNwkSIntKey = sNwkSIntKey;
    }

    public Integer getLastDataDownDate() {
        return lastDataDownDate;
    }

    public void setLastDataDownDate(Integer lastDataDownDate) {
        this.lastDataDownDate = lastDataDownDate;
    }

    public String getLastDataUpDataRate() {
        return lastDataUpDataRate;
    }

    public void setLastDataUpDataRate(String lastDataUpDataRate) {
        this.lastDataUpDataRate = lastDataUpDataRate;
    }

    public Integer getLastDataUpDate() {
        return lastDataUpDate;
    }

    public void setLastDataUpDate(Integer lastDataUpDate) {
        this.lastDataUpDate = lastDataUpDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<LinkDto> getLinks() {
        return links;
    }

    public void setLinks(List<LinkDto> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "EndDeviceDto{" +
                "devEui='" + devEui + '\'' +
                ", activation='" + activation + '\'' +
                ", classType='" + classType + '\'' +
                ", cluster=" + cluster +
                ", geolocation='" + geolocation + '\'' +
                ", macVersion='" + macVersion + '\'' +
                ", profile='" + profile + '\'' +
                ", regParamsRevision='" + regParamsRevision + '\'' +
                ", rfRegion='" + rfRegion + '\'' +
                ", adrEnabled=" + adrEnabled +
                ", altitude=" + altitude +
                ", appEui='" + appEui + '\'' +
                ", appKey='" + appKey + '\'' +
                ", appSKey='" + appSKey + '\'' +
                ", cfList=" + cfList +
                ", country='" + country + '\'' +
                ", devAddr='" + devAddr + '\'' +
                ", devNonceCounter=" + devNonceCounter +
                ", dwellTime=" + dwellTime +
                ", fNwkSIntKey='" + fNwkSIntKey + '\'' +
                ", fcntDown=" + fcntDown +
                ", fcntUp=" + fcntUp +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", name='" + name + '\'' +
                ", nwkSKey='" + nwkSKey + '\'' +
                ", pingSlotDr='" + pingSlotDr + '\'' +
                ", pingSlotFreq=" + pingSlotFreq +
                ", rx1Delay=" + rx1Delay +
                ", rx1DrOffset=" + rx1DrOffset +
                ", rx2Dr=" + rx2Dr +
                ", rx2Freq=" + rx2Freq +
                ", rxWindows='" + rxWindows + '\'' +
                ", sNwkSIntKey='" + sNwkSIntKey + '\'' +
                ", lastDataDownDate=" + lastDataDownDate +
                ", lastDataUpDataRate='" + lastDataUpDataRate + '\'' +
                ", lastDataUpDate=" + lastDataUpDate +
                ", status='" + status + '\'' +
                ", links=" + links +
                '}';
    }
}