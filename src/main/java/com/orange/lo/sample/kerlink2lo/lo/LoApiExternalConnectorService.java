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
import com.orange.lo.sdk.externalconnector.model.*;
import com.orange.lo.sdk.externalconnector.model.NodeStatus.Capabilities;
import com.orange.lo.sdk.externalconnector.model.NodeStatus.Command;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.Policy;
import net.jodah.failsafe.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;

@Service
public class LoApiExternalConnectorService {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ExternalConnectorCommandResponseWrapper externalConnectorCommandResponseWrapper;
    private final LoDeviceCache deviceCache;
    private final DataManagementExtConnector loApiDataManagementExtConnector;
    private final LoDeviceProvider loDeviceProvider;
    private final LoProperties loProperties;
    private Policy<Void> statusRetryPolicy;

    public LoApiExternalConnectorService(ExternalConnectorCommandResponseWrapper externalConnectorCommandResponseWrapper,
                                         LoDeviceCache deviceCache,
                                         LoDeviceProvider loDeviceProvider,
                                         DataManagementExtConnector dataManagementExtConnector,
                                         LoProperties loProperties) {
        this.externalConnectorCommandResponseWrapper = externalConnectorCommandResponseWrapper;
        this.deviceCache = deviceCache;
        this.loApiDataManagementExtConnector = dataManagementExtConnector;
        this.loDeviceProvider = loDeviceProvider;
        this.loProperties = loProperties;
        this.statusRetryPolicy = new RetryPolicy<>();
    }

    @PostConstruct
    public void connect() {
        loApiDataManagementExtConnector.connect();
    }

    public void sendMessage(DataUpDto dataUpDto, String kerlinkAccountName) {
        String deviceId = dataUpDto.getEndDevice().getDevEui();
        if (!deviceCache.contains(deviceId)) {
            createDevice(deviceId, kerlinkAccountName);
        }
        DataMessage dataMessage = new DataMessage();
        PayloadDecoder payloadDecoder = PayloadDecoderFactory.payloadDecoder(dataUpDto.getEncodingType());
        String decodedPayload = payloadDecoder.decode(dataUpDto.getPayload());
        Value value = new Value(decodedPayload);
        dataMessage.setValue(value);

        String connectorDecoder = loProperties.getMessageDecoder();
        if(connectorDecoder != null && !connectorDecoder.isEmpty()) {
            dataMessage.setMetadata(new Metadata(connectorDecoder));
        }

        LOG.debug("Sending message to device {} on account {}", deviceId, kerlinkAccountName);
        loApiDataManagementExtConnector.sendMessage(deviceId, dataMessage);
    }

    public void sendCommandResponse(DataDownEventDto dataDownEventDto) {
        externalConnectorCommandResponseWrapper.sendCommandResponse(dataDownEventDto);
    }

    public void createDevice(String kerlinkDeviceId, String kerlinkAccountName) {
        loDeviceProvider.addDevice(kerlinkDeviceId, kerlinkAccountName);

        NodeStatus nodeStatus = new NodeStatus();
        nodeStatus.setStatus(Status.ONLINE);
        Capabilities capabilities = new Capabilities();
        capabilities.setCommand(new Command(true));
        nodeStatus.setCapabilities(capabilities);
        Failsafe.with(statusRetryPolicy)
                .run(() ->
                        loApiDataManagementExtConnector.sendStatus(kerlinkDeviceId, nodeStatus)
                );
    }

    public void deleteDevice(String loDeviceId) {
        loDeviceProvider.deleteDevice(loDeviceId);
    }

}
