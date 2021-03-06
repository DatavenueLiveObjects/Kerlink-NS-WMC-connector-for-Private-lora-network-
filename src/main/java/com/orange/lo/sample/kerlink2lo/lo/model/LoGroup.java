package com.orange.lo.sample.kerlink2lo.lo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoGroup {

    private String id;
    private String pathNode;
    
    @JsonCreator
    public LoGroup(@JsonProperty("id") String id, @JsonProperty("pathNode") String pathNode) {
        this.id = id;
        this.pathNode = pathNode;
    }
    
    public LoGroup(@JsonProperty("id") String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getPathNode() {
        return pathNode;
    }
}
