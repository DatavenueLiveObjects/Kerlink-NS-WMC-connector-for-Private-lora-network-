/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo.lo;

import com.orange.lo.sample.kerlink2lo.kerlink.model.DataDownEventDto;
import com.orange.lo.sample.kerlink2lo.kerlink.model.DataUpDto;
import com.orange.lo.sample.kerlink2lo.lo.CommandMapper.LoCommand;
import com.orange.lo.sample.kerlink2lo.utils.Counters;
import com.orange.lo.sdk.externalconnector.DataManagementExtConnector;
import com.orange.lo.sdk.externalconnector.model.*;
import com.orange.lo.sdk.externalconnector.model.NodeStatus.Capabilities;
import com.orange.lo.sdk.externalconnector.model.NodeStatus.Command;
import io.micrometer.core.instrument.Counter;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.Policy;
import net.jodah.failsafe.RetryPolicy;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.Optional;

@Service
public class LoApiExternalConnectorService {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final LoDeviceCache deviceCache;
    private final LoDeviceProvider loDeviceProvider;
    private final LoProperties loProperties;
    private final Policy<Void> statusRetryPolicy;
    private final CommandMapper commandMapper;
    private final DataManagementExtConnector dataManagementExtConnector;
    private final Counters counters;

    public LoApiExternalConnectorService(LoDeviceCache deviceCache,
                                         LoDeviceProvider loDeviceProvider,
                                         DataManagementExtConnector dataManagementExtConnector,
                                         LoProperties loProperties,
                                         CommandMapper commandMapper,
                                         Counters counters) {
        this.deviceCache = deviceCache;
        this.loDeviceProvider = loDeviceProvider;
        this.dataManagementExtConnector = dataManagementExtConnector;
        this.loProperties = loProperties;
        this.commandMapper = commandMapper;
        this.statusRetryPolicy = new RetryPolicy<>();
        this.counters = counters;
    }

    @PostConstruct
    public void connect() {
        dataManagementExtConnector.connect();
    }

    public void sendMessage(DataUpDto dataUpDto, String kerlinkAccountName) {
        String deviceId = dataUpDto.getEndDevice().getDevEui();
        if (!deviceCache.contains(deviceId)) {
            createDevice(deviceId, kerlinkAccountName);
        }
        DataMessage dataMessage = new DataMessage();
        Value value = new Value(dataUpDto.getPayload());
        dataMessage.setValue(value);

        String connectorDecoder = loProperties.getMessageDecoder();
        if(connectorDecoder != null && !connectorDecoder.isEmpty()) {
            dataMessage.setMetadata(new Metadata(connectorDecoder));
        }

        LOG.debug("Sending message to device {} on account {}", deviceId, kerlinkAccountName);
        try {
            dataManagementExtConnector.sendMessage(deviceId, dataMessage);
            incrementSuccessCounters();
        } catch (Exception e) {
            incrementFailureCounters();
            throw e;
        }
    }

    private void incrementFailureCounters() {
        Counter messageSentAttemptFailedCounter = counters.getMessageSentAttemptFailedCounter();
        messageSentAttemptFailedCounter.increment();
        Counter messageSentFailedCounter = counters.getMessageSentFailedCounter();
        messageSentFailedCounter.increment();
    }

    private void incrementSuccessCounters() {
        Counter messageSentCounter = counters.getMessageSentCounter();
        messageSentCounter.increment();
    }

    public void sendCommandResponse(DataDownEventDto dataDownEventDto) {
        Optional<LoCommand> optionalLoCommand = commandMapper.get(dataDownEventDto.getDataDownId());

        if (optionalLoCommand.isPresent()) {
            LoCommand loCommand = optionalLoCommand.get();
            LOG.trace("Sending command response for device {}", loCommand.getNodeId());
            CommandResponse commandResponse = new CommandResponse(loCommand.getId(), loCommand.getNodeId());
            dataManagementExtConnector.sendCommandResponse(commandResponse);
        } else if (LOG.isDebugEnabled()) {
            String cleanDtoString = StringEscapeUtils.escapeJava(dataDownEventDto.toString());
            LOG.debug("Received unknown command status from Kerlink: {}", cleanDtoString);
        }
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
                        dataManagementExtConnector.sendStatus(kerlinkDeviceId, nodeStatus)
                );
    }

    public void deleteDevice(String loDeviceId) {
        loDeviceProvider.deleteDevice(loDeviceId);
    }

}
