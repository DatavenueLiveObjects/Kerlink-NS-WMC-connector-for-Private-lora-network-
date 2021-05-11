/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlink2lo.lo;

import com.orange.lo.sample.kerlink2lo.exceptions.EncodingTypeException;
import com.orange.lo.sample.kerlink2lo.kerlink.model.DataDownEventDto;
import com.orange.lo.sample.kerlink2lo.kerlink.model.DataUpDto;

import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.externalconnector.model.DataMessage;
import com.orange.lo.sdk.externalconnector.model.Metadata;
import com.orange.lo.sdk.externalconnector.model.NodeStatus;
import com.orange.lo.sdk.externalconnector.model.NodeStatus.Capabilities;
import com.orange.lo.sdk.externalconnector.model.Status;
import com.orange.lo.sdk.rest.devicemanagement.Inventory;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class ExternalConnectorService {

    private ExternalConnector externalConnector;
    private LoDeviceCache deviceCache;
    private LoProperties loProperties;
    private LOApiClient loApiClient;
    
    public ExternalConnectorService(ExternalConnector externalConnector, LoDeviceProvider loDeviceProvider, LoDeviceCache deviceCache, LoProperties loProperties, LOApiClient loApiClient) {
        this.externalConnector = externalConnector;
        this.deviceCache = deviceCache;
        this.loProperties = loProperties;
        this.loApiClient = loApiClient;
    }
    
    public void sendMessage(DataUpDto dataUpDto, String kerlinkAccountName) {
        String deviceId = dataUpDto.getEndDevice().getDevEui();
        if (!deviceCache.contains(deviceId)) {
            createDevice(deviceId, kerlinkAccountName);
        }
        DataMessage dataMessage = new DataMessage();
        convertPayload(dataUpDto);
        dataMessage.setValue(dataUpDto);

        String messageDecoder = loProperties.getMessageDecoder();
        if (messageDecoder != null && !messageDecoder.isEmpty()) {
            dataMessage.setMetadata(new Metadata(messageDecoder));
        }
        loApiClient.getDataManagementExtConnector().sendMessage(deviceId, dataMessage);
        // externalConnector.sendMessage(dataUpDto);
    }
    
    public void sendCommandResponse(DataDownEventDto dataDownEventDto) {
        externalConnector.sendCommandResponse(dataDownEventDto);
    }
    
    public void createDevice(String kerlinkDeviceId, String kerlinkAccountName) {
        loApiClient.getDeviceManagement().getInventory().createDevice(kerlinkDeviceId);
        NodeStatus nodeStatus = new NodeStatus();
        nodeStatus.setStatus(Status.ONLINE);
        nodeStatus.setCapabilities(new Capabilities(true));
        loApiClient.getDataManagementExtConnector().sendStatus(kerlinkDeviceId, nodeStatus);

        //loDeviceProvider.addDevice(kerlinkDeviceId, kerlinkAccountName);
        //externalConnector.sendStatus(kerlinkDeviceId);

        deviceCache.add(kerlinkDeviceId, kerlinkAccountName);        
    }
    
    public void deleteDevice(String loDeviceId) {
        loApiClient.getDeviceManagement().getInventory().deleteDevice(loDeviceId);

        //loDeviceProvider.deleteDevice(loDeviceId);
        //String kerlinkDeviceId = loDeviceId.substring(loProperties.getDevicePrefix().length());
        //deviceCache.delete(kerlinkDeviceId);
        deviceCache.delete(loDeviceId);
    }

    private static void convertPayload(DataUpDto dataUpDto) {
        switch (dataUpDto.getEncodingType()) {
            case "BASE64":
                byte[] payload = Base64.getDecoder().decode(dataUpDto.getPayload());
                dataUpDto.setPayload(new String(payload));
                break;
            case "HEXA":
                try {
                    byte[] decodeHex = Hex.decodeHex(dataUpDto.getPayload());
                    dataUpDto.setPayload(new String(decodeHex));
                } catch (DecoderException e) {
                    throw new EncodingTypeException(e);
                }
                break;
            default:
                break;
        }
    }
}
