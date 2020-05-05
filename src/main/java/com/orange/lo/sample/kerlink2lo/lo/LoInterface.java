package com.orange.lo.sample.kerlink2lo.lo;

import com.orange.lo.sample.kerlink2lo.lo.model.NodeStatus.Capabilities;

public class LoInterface {

    private String connector;
    private Definition definition;
    private Capabilities capabilities;
    
    public String getConnector() {
        return connector;
    }
    
    public void setConnector(String connector) {
        this.connector = connector;
    }
    
    public Definition getDefinition() {
        return definition;
    }
    
    public void setDefinition(Definition definition) {
        this.definition = definition;
    }
    
    public Capabilities getCapabilities() {
        return capabilities;
    }
    
    public void setCapabilities(Capabilities capabilities) {
        this.capabilities = capabilities;
    }
}
