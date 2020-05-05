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
public class DataDownEventDto {

    @JsonProperty("dataDownId")
    private String dataDownId = null;
    
    @JsonProperty("event")
    private String event = null;
    
    @JsonProperty("status")
    private String status = null;

    @JsonProperty("clusterId")
    private Integer clusterId = null;
    
    @JsonProperty("nbAttempts")
    private Integer nbAttempts = null;
    
    @JsonProperty("multicast")
    private Boolean multicast = null;

    public String getDataDownId() {
        return dataDownId;
    }

    public void setDataDownId(String dataDownId) {
        this.dataDownId = dataDownId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    public Integer getNbAttempts() {
        return nbAttempts;
    }

    public void setNbAttempts(Integer nbAttempts) {
        this.nbAttempts = nbAttempts;
    }

    public Boolean getMulticast() {
        return multicast;
    }

    public void setMulticast(Boolean multicast) {
        this.multicast = multicast;
    }

    @Override
    public String toString() {
        return "DataDownEventDto [dataDownId=" + dataDownId + ", event=" + event + ", status=" + status + ", clusterId=" + clusterId + ", nbAttempts=" + nbAttempts + ", multicast=" + multicast + "]";
    }
    
}
