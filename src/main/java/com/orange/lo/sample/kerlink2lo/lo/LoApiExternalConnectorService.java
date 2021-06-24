/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo.lo;

import com.orange.lo.sample.kerlink2lo.kerlink.model.DataDownEventDto;
import com.orange.lo.sample.kerlink2lo.kerlink.model.DataUpDto;
import com.orange.lo.sdk.externalconnector.DataManagementExtConnector;
import com.orange.lo.sdk.externalconnector.model.DataMessage;
import com.orange.lo.sdk.externalconnector.model.Metadata;
import com.orange.lo.sdk.externalconnector.model.NodeStatus;
import com.orange.lo.sdk.externalconnector.model.NodeStatus.Capabilities;
import com.orange.lo.sdk.externalconnector.model.NodeStatus.Command;
import com.orange.lo.sdk.externalconnector.model.Status;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Service
public class LoApiExternalConnectorService implements ExternalConnectorService {

    private final ExternalConnectorCommandResponseWrapper externalConnectorCommandResponseWrapper;
    private final LoDeviceCache deviceCache;
    private final DataManagementExtConnector loApiDataManagementExtConnector;
    private final LoDeviceProvider loDeviceProvider;
    private final Optional<String> connectorDecoderName;

    public LoApiExternalConnectorService(ExternalConnectorCommandResponseWrapper externalConnectorCommandResponseWrapper, LoDeviceCache deviceCache, LoDeviceProvider loDeviceProvider, PayloadDecoder connectorDecoder, DataManagementExtConnector dataManagementExtConnector) {
        this.externalConnectorCommandResponseWrapper = externalConnectorCommandResponseWrapper;
        this.deviceCache = deviceCache;
        this.loApiDataManagementExtConnector = dataManagementExtConnector;
        this.loDeviceProvider = loDeviceProvider;
        this.connectorDecoderName = connectorDecoder.metadataName();
    }

    @PostConstruct
    public void connect() {
        loApiDataManagementExtConnector.connect();
    }

    @Override
    public void sendMessage(DataUpDto dataUpDto, String kerlinkAccountName) {
        String deviceId = dataUpDto.getEndDevice().getDevEui();
        if (!deviceCache.contains(deviceId)) {
            createDevice(deviceId, kerlinkAccountName);
        }
        DataMessage dataMessage = new DataMessage();
        PayloadDecoder messageDecoder = PayloadDecoderFactory.payloadDecoder(dataUpDto.getEncodingType());
        String decodedPayload = messageDecoder.decode(dataUpDto.getPayload());
        dataMessage.setValue(decodedPayload);

        if (connectorDecoderName.isPresent()) {
            dataMessage.setMetadata(new Metadata(connectorDecoderName.get()));
        }
        loApiDataManagementExtConnector.sendMessage(deviceId, dataMessage);
    }

    @Override
    public void sendCommandResponse(DataDownEventDto dataDownEventDto) {
        externalConnectorCommandResponseWrapper.sendCommandResponse(dataDownEventDto);
    }

    @Override
    public void createDevice(String kerlinkDeviceId, String kerlinkAccountName) {
        loDeviceProvider.addDevice(kerlinkDeviceId, kerlinkAccountName);

        NodeStatus nodeStatus = new NodeStatus();
        nodeStatus.setStatus(Status.ONLINE);
        Capabilities capabilities = new Capabilities();
        capabilities.setCommand(new Command(true));
        nodeStatus.setCapabilities(capabilities);
        loApiDataManagementExtConnector.sendStatus(kerlinkDeviceId, nodeStatus);
    }

    @Override
    public void deleteDevice(String loDeviceId) {
        loDeviceProvider.deleteDevice(loDeviceId);
    }

}
