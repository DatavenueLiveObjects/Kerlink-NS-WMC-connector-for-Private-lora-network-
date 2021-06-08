/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo.lo;

import com.orange.lo.sample.kerlink2lo.kerlink.model.DataDownEventDto;
import com.orange.lo.sample.kerlink2lo.kerlink.model.DataUpDto;
import com.orange.lo.sdk.externalconnector.DataManagementExtConnector;
import com.orange.lo.sdk.externalconnector.model.DataMessage;
import com.orange.lo.sdk.externalconnector.model.Metadata;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoApiExternalConnectorService implements ExternalConnectorService {

    private final ExternalConnectorCommandResponseWrapper externalConnectorCommandResponseWrapper;
    private final LoDeviceCache deviceCache;
    private final DataManagementExtConnector loApiDataManagementExtConnector;
    private final LoDeviceProvider loDeviceProvider;
    private final PayloadDecoder payloadDecoder;

    public LoApiExternalConnectorService(ExternalConnectorCommandResponseWrapper externalConnectorCommandResponseWrapper, LoDeviceCache deviceCache, LoDeviceProvider loDeviceProvider, PayloadDecoder payloadDecoder, DataManagementExtConnector dataManagementExtConnector) {
        this.externalConnectorCommandResponseWrapper = externalConnectorCommandResponseWrapper;
        this.deviceCache = deviceCache;
        this.loApiDataManagementExtConnector = dataManagementExtConnector;
        this.loDeviceProvider = loDeviceProvider;
        this.payloadDecoder = payloadDecoder;
    }

    @Override
    public void sendMessage(DataUpDto dataUpDto, String kerlinkAccountName) {
        String deviceId = dataUpDto.getEndDevice().getDevEui();
        if (!deviceCache.contains(deviceId)) {
            createDevice(deviceId, kerlinkAccountName);
        }
        DataMessage dataMessage = new DataMessage();
        String decodedPayload = payloadDecoder.decode(dataUpDto.getPayload());
        dataMessage.setValue(decodedPayload);

        Optional<String> metadataName = payloadDecoder.metadataName();
        if (metadataName.isPresent()) {
            dataMessage.setMetadata(new Metadata(metadataName.get()));
        }
        loApiDataManagementExtConnector.sendMessage(deviceId, dataMessage);
    }

    @Override
    public void sendCommandResponse(DataDownEventDto dataDownEventDto) {
        externalConnectorCommandResponseWrapper.sendCommandResponse(dataDownEventDto);
    }

    @Override
    public void createDevice(String kerlinkDeviceId, String kerlinkAccountName) {
        // TODO: Is status send needed or sent in SDK?
        loDeviceProvider.addDevice(kerlinkDeviceId, kerlinkAccountName);
    }

    @Override
    public void deleteDevice(String loDeviceId) {
        loDeviceProvider.deleteDevice(loDeviceId);
    }

}
