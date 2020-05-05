/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlink2lo.lo;

import com.orange.lo.sample.kerlink2lo.kerlink.api.model.DataDownEventDto;
import com.orange.lo.sample.kerlink2lo.kerlink.api.model.DataUpDto;

import org.springframework.stereotype.Service;

@Service
public class ExternalConnectorService {

    private ExternalConnector externalConnector;
    private LoDeviceProvider loDeviceProvider;
    private LoDeviceCache deviceCache;
    private LoProperties loProperties;
    
    public ExternalConnectorService(ExternalConnector externalConnector, LoDeviceProvider loDeviceProvider, LoDeviceCache deviceCache, LoProperties loProperties) {
        this.externalConnector = externalConnector;
        this.loDeviceProvider = loDeviceProvider;
        this.deviceCache = deviceCache;
        this.loProperties = loProperties;
    }
    
    public void sendMessage(DataUpDto dataUpDto) {
        if (!deviceCache.contains(dataUpDto.getEndDevice().getDevEui())) {
            createDevice(dataUpDto.getEndDevice().getDevEui());
        }
        externalConnector.sendMessage(dataUpDto);            
    }
    
    public void sendCommandResponse(DataDownEventDto dataDownEventDto) {
        externalConnector.sendCommandResponse(dataDownEventDto);
    }
    
    public void createDevice(String kerlinkDeviceId) {
        loDeviceProvider.addDevice(kerlinkDeviceId);
        externalConnector.sendStatus(kerlinkDeviceId);
        deviceCache.add(kerlinkDeviceId);        
    }
    
    public void deleteDevice(String loDeviceId) {
        loDeviceProvider.deleteDevice(loDeviceId);
        String kerlinkDeviceId = loDeviceId.substring(loProperties.getDevicePrefix().length());
        deviceCache.delete(kerlinkDeviceId);
    }
}
