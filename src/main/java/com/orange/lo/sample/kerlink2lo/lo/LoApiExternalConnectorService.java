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
import com.orange.lo.sdk.externalconnector.model.NodeStatus;
import com.orange.lo.sdk.externalconnector.model.NodeStatus.Capabilities;
import com.orange.lo.sdk.externalconnector.model.NodeStatus.Command;
import com.orange.lo.sdk.externalconnector.model.Status;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Service
public class LoApiExternalConnectorService implements ExternalConnectorService {

    private final ExternalConnectorCommandResponseWrapper externalConnectorCommandResponseWrapper;
    private final LoDeviceCache deviceCache;
    private final DataManagementExtConnector loApiDataManagementExtConnector;
    private final LoDeviceProvider loDeviceProvider;
    private final PayloadEncoder payloadEncoder;
	private final LoProperties loProperties;

    public LoApiExternalConnectorService(ExternalConnectorCommandResponseWrapper externalConnectorCommandResponseWrapper, LoDeviceCache deviceCache, LoDeviceProvider loDeviceProvider, PayloadEncoder payloadEncoder, DataManagementExtConnector dataManagementExtConnector, LoProperties loProperties) {
        this.externalConnectorCommandResponseWrapper = externalConnectorCommandResponseWrapper;
        this.deviceCache = deviceCache;
        this.loApiDataManagementExtConnector = dataManagementExtConnector;
        this.loDeviceProvider = loDeviceProvider;
        this.payloadEncoder = payloadEncoder;
		this.loProperties = loProperties;
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
        payloadEncoder.convert(dataUpDto);
        dataMessage.setValue(dataUpDto);

        String messageDecoder = loProperties.getMessageDecoder();
        if (!StringUtils.isEmpty(messageDecoder)) {
            dataMessage.setMetadata(new Metadata(messageDecoder));            
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
