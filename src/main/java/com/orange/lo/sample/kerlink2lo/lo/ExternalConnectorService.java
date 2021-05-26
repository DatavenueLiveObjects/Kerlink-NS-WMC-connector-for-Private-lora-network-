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
    private LoDeviceProvider loDeviceProvider;

    public ExternalConnectorService(ExternalConnector externalConnector, LoDeviceCache deviceCache, LoProperties loProperties, LOApiClient loApiClient, LoDeviceProvider loDeviceProvider) {
        this.externalConnector = externalConnector;
        this.deviceCache = deviceCache;
        this.loProperties = loProperties;
        this.loApiClient = loApiClient;
        this.loDeviceProvider = loDeviceProvider;
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
    }
    
    public void sendCommandResponse(DataDownEventDto dataDownEventDto) {
        externalConnector.sendCommandResponse(dataDownEventDto);
    }
    
    public void createDevice(String kerlinkDeviceId, String kerlinkAccountName) {
        // TODO: Is status send needed or sent in SDK?
        loDeviceProvider.addDevice(kerlinkDeviceId, kerlinkAccountName);

        deviceCache.add(kerlinkDeviceId, kerlinkAccountName);        
    }
    
    public void deleteDevice(String loDeviceId) {
        loDeviceProvider.deleteDevice(loDeviceId);
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
