package com.orange.lo.sample.kerlink2lo.lo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Definition {

    private String clientId;
    
    @JsonCreator
    public Definition(@JsonProperty("clientId") String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }
}
