/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlink2lo.lo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.orange.lo.sample.kerlink2lo.lo.model.NodeStatus.Capabilities;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoDevice {

    private String id;
    private String name;
    private LoGroup group;
    private List<LoInterface> interfaces;    
    
    public LoDevice() {}
    
    public LoDevice(String id, String groupId, String prefix, boolean commandAvailable) {
        this.id = prefix + id;
        this.name = id;
        this.group = new LoGroup(groupId);
        
        LoInterface loInterface = new LoInterface(); 
        loInterface.setConnector("x-connector");
        loInterface.setDefinition(new Definition(id));
        loInterface.setCapabilities(new Capabilities(true));
        this.interfaces = Collections.singletonList(loInterface);        
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public LoGroup getGroup() {
        return group;
    }
    
    public void setGroup(LoGroup group) {
        this.group = group;
    }
    
    public List<LoInterface> getInterfaces() {
        return interfaces;
    }
    
    public void setInterfaces(List<LoInterface> interfaces) {
        this.interfaces = interfaces;
    }

    @Override
    public String toString() {
        return "LoDevice [id=" + id + ", name=" + name + ", group=" + group + ", interfaces=" + interfaces + "]";
    }
}