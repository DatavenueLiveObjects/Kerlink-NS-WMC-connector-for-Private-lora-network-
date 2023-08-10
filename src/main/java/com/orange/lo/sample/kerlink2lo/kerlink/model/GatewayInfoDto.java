package com.orange.lo.sample.kerlink2lo.kerlink.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GatewayInfoDto {

    @JsonProperty("altitude")
    private Integer altitude = null;

    @JsonProperty("antenna")
    private Integer antenna = null;

    @JsonProperty("channel")
    private Integer channel = null;

    @JsonProperty("fineTimestamp")
    private Integer fineTimestamp = null;

    @JsonProperty("frequencyOffset")
    private Integer frequencyOffset = null;

    @JsonProperty("gwEui")
    private String gwEui = null;

    @JsonProperty("latitude")
    private Double latitude = null;

    @JsonProperty("longitude")
    private Double longitude = null;

    @JsonProperty("radioId")
    private Integer radioId = null;

    @JsonProperty("rfRegion")
    private String rfRegion = null;

    @JsonProperty("rssi")
    private Integer rssi = null;

    @JsonProperty("rssis")
    private Integer rssis = null;

    @JsonProperty("rssisd")
    private Integer rssisd = null;

    @JsonProperty("snr")
    private Double snr = null;

    public Integer getAltitude() {
        return altitude;
    }

    public void setAltitude(Integer altitude) {
        this.altitude = altitude;
    }

    public Integer getAntenna() {
        return antenna;
    }

    public void setAntenna(Integer antenna) {
        this.antenna = antenna;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public Integer getFineTimestamp() {
        return fineTimestamp;
    }

    public void setFineTimestamp(Integer fineTimestamp) {
        this.fineTimestamp = fineTimestamp;
    }

    public Integer getFrequencyOffset() {
        return frequencyOffset;
    }

    public void setFrequencyOffset(Integer frequencyOffset) {
        this.frequencyOffset = frequencyOffset;
    }

    public String getGwEui() {
        return gwEui;
    }

    public void setGwEui(String gwEui) {
        this.gwEui = gwEui;
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

    public Integer getRadioId() {
        return radioId;
    }

    public void setRadioId(Integer radioId) {
        this.radioId = radioId;
    }

    public String getRfRegion() {
        return rfRegion;
    }

    public void setRfRegion(String rfRegion) {
        this.rfRegion = rfRegion;
    }

    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

    public Integer getRssis() {
        return rssis;
    }

    public void setRssis(Integer rssis) {
        this.rssis = rssis;
    }

    public Integer getRssisd() {
        return rssisd;
    }

    public void setRssisd(Integer rssisd) {
        this.rssisd = rssisd;
    }

    public Double getSnr() {
        return snr;
    }

    public void setSnr(Double snr) {
        this.snr = snr;
    }

    @Override
    public String toString() {
        return "GatewayInfoDto{" +
                "altitude=" + altitude +
                ", antenna=" + antenna +
                ", channel=" + channel +
                ", fineTimestamp=" + fineTimestamp +
                ", frequencyOffset=" + frequencyOffset +
                ", gwEui='" + gwEui + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", radioId=" + radioId +
                ", rfRegion='" + rfRegion + '\'' +
                ", rssi=" + rssi +
                ", rssis=" + rssis +
                ", rssisd=" + rssisd +
                ", snr=" + snr +
                '}';
    }
}
