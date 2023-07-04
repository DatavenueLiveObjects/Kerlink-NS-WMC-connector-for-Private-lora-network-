package com.orange.lo.sample.kerlink2lo.kerlink.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClusterDto {

    @JsonProperty("id")
    private Integer id = null;

    @JsonProperty("geolocEnabled")
    private Boolean geolocEnabled = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("hexa")
    private Boolean hexa = null;

    @JsonProperty("geolocExpirationDate")
    private Integer geolocExpirationDate = null;

    @JsonProperty("pushEnabled")
    private Boolean pushEnabled = null;

    @JsonProperty("links")
    private List<LinkDto> links = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getGeolocEnabled() {
        return geolocEnabled;
    }

    public void setGeolocEnabled(Boolean geolocEnabled) {
        this.geolocEnabled = geolocEnabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getHexa() {
        return hexa;
    }

    public void setHexa(Boolean hexa) {
        this.hexa = hexa;
    }

    public Integer getGeolocExpirationDate() {
        return geolocExpirationDate;
    }

    public void setGeolocExpirationDate(Integer geolocExpirationDate) {
        this.geolocExpirationDate = geolocExpirationDate;
    }

    public Boolean getPushEnabled() {
        return pushEnabled;
    }

    public void setPushEnabled(Boolean pushEnabled) {
        this.pushEnabled = pushEnabled;
    }

    public List<LinkDto> getLinks() {
        return links;
    }

    public void setLinks(List<LinkDto> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "ClusterDto{" +
                "id=" + id +
                ", geolocEnabled=" + geolocEnabled +
                ", name='" + name + '\'' +
                ", hexa=" + hexa +
                ", geolocExpirationDate=" + geolocExpirationDate +
                ", pushEnabled=" + pushEnabled +
                ", links=" + links +
                '}';
    }
}
