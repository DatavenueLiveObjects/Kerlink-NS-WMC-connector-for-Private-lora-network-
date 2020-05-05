/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlink2lo.lo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.lo.sample.kerlink2lo.CommandMapper;
import com.orange.lo.sample.kerlink2lo.CommandMapper.LoCommand;
import com.orange.lo.sample.kerlink2lo.exceptions.EncodingTypeException;
import com.orange.lo.sample.kerlink2lo.exceptions.LoMqttException;
import com.orange.lo.sample.kerlink2lo.exceptions.ParseException;
import com.orange.lo.sample.kerlink2lo.kerlink.api.KerlinkApi;
import com.orange.lo.sample.kerlink2lo.kerlink.api.model.DataDownDto;
import com.orange.lo.sample.kerlink2lo.kerlink.api.model.DataDownEventDto;
import com.orange.lo.sample.kerlink2lo.kerlink.api.model.DataUpDto;
import com.orange.lo.sample.kerlink2lo.kerlink.api.model.EndDeviceDto;
import com.orange.lo.sample.kerlink2lo.lo.model.CommandRequest;
import com.orange.lo.sample.kerlink2lo.lo.model.CommandResponse;
import com.orange.lo.sample.kerlink2lo.lo.model.DataMessage;
import com.orange.lo.sample.kerlink2lo.lo.model.Metadata;
import com.orange.lo.sample.kerlink2lo.lo.model.NodeStatus;
import com.orange.lo.sample.kerlink2lo.lo.model.NodeStatus.Capabilities;

import java.lang.invoke.MethodHandles;
import java.util.Base64;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class ExternalConnector {

    private static final String DATA_TOPIC_TEMPLATE = "connector/v1/nodes/%s/data";
    private static final String STATUS_TOPIC_TEMPLATE = "connector/v1/nodes/%s/status";
    private static final String COMMAND_RESPONSE_TOPIC = "connector/v1/responses/command";
    private static final String COMMAND_REQUEST_TOPIC = "connector/v1/requests/command";
    
    private static final String F_PORT_KEY = "fPort";
    private static final String F_PORT_DEFAULT = "1";
    
    
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private IMqttClient loMqttClient;
    private ObjectMapper objectMapper;
    private LoProperties loProperties;
    private KerlinkApi kerlinkApi;
    private CommandMapper commandMapper;

    public ExternalConnector(IMqttClient loMqttClient, KerlinkApi kerlinkApi, ObjectMapper objectMapper, CommandMapper commandMapper, LoProperties loProperties) {
        this.loMqttClient = loMqttClient;
        this.kerlinkApi = kerlinkApi;
        this.objectMapper = objectMapper;
        this.commandMapper = commandMapper;
        this.loProperties = loProperties;
    }
    
    @PostConstruct
    public void receiveCommands() {
        try {
            LOG.info("Run commands subscriber");
            loMqttClient.subscribe(COMMAND_REQUEST_TOPIC, loProperties.getMessageQos(), new MessageListener());
        } catch (MqttException e) {
            throw new LoMqttException(e);
        }
    }

    public void sendMessage(DataUpDto dataUpDto) {
        LOG.debug("Sending data message from device {}", dataUpDto.getEndDevice().getDevEui());

        DataMessage dataMessage = new DataMessage();
        convertPayload(dataUpDto);
        
        dataMessage.setValue(dataUpDto);
        
        String messageDecoder = loProperties.getMessageDecoder();
        if (messageDecoder != null && !messageDecoder.isEmpty()) {
            dataMessage.setMetadata(new Metadata(messageDecoder));            
        }
        
        MqttMessage msg = prepareMqttMessgae(dataMessage);
        String topic = String.format(DATA_TOPIC_TEMPLATE, dataUpDto.getEndDevice().getDevEui());

        publish(topic, msg);
    }

    public void sendStatus(String devEui) {
        LOG.debug("Sending status message for device {}", devEui);

        NodeStatus nodeStatus = new NodeStatus();
        nodeStatus.setStatus("ONLINE");
        nodeStatus.setCapabilities(new Capabilities(true));

        MqttMessage msg = prepareMqttMessgae(nodeStatus);
        String topic = String.format(STATUS_TOPIC_TEMPLATE, devEui);

        publish(topic, msg);
    }

    
    
    public void sendCommandResponse(DataDownEventDto dataDownEventDto) {
        Optional<LoCommand> loCommand = commandMapper.get(dataDownEventDto.getDataDownId());
        
        if (loCommand.isPresent()) {
            LOG.debug("Sending command response for device {}", loCommand.get().getNodeId());
            CommandResponse commandResponse = new CommandResponse(loCommand.get().getId(), loCommand.get().getNodeId());
            
            MqttMessage msg = prepareMqttMessgae(commandResponse);
            publish(COMMAND_RESPONSE_TOPIC, msg);
        } else {
            LOG.debug("Receive unknow command status from Kerlink: {}", dataDownEventDto);
        }
    }

    private MqttMessage prepareMqttMessgae(Object dataMessage) {
        try {
            String payload = objectMapper.writeValueAsString(dataMessage);
            MqttMessage msg = new MqttMessage();
            msg.setQos(loProperties.getMessageQos());
            msg.setPayload(payload.getBytes());
            return msg;
        } catch (JsonProcessingException e) {
            throw new ParseException(e);
        }
    }

    private void publish(String topic, MqttMessage msg) {
        try {
            loMqttClient.publish(topic, msg);
        } catch (MqttException e) {
            throw new LoMqttException(e);
        }
    }
    
    private void convertPayload(DataUpDto dataUpDto) {
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
    
    protected class MessageListener implements IMqttMessageListener {

        private static final String CONTENT_TYPE_TEXT = "TEXT";

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            LOG.trace("New Command received - topic: {}, command: {}", topic, message);
            
            CommandRequest commandRequest = objectMapper.readValue(message.getPayload(), CommandRequest.class);
            
            DataDownDto dataDownDto = prepareDataDown(commandRequest);
            
            Optional<String> commandId = kerlinkApi.sendCommand(dataDownDto);
            if (commandId.isPresent()) {
                commandMapper.put(commandId.get(), commandRequest.getId(), commandRequest.getNodeId());
                LOG.trace("Put to commandMapper: kerlinkID = {}, loId = {}, nodeId = {}", commandId, commandRequest.getId(), commandRequest.getNodeId());                
            }
        }

        private DataDownDto prepareDataDown(CommandRequest commandRequest) {
            DataDownDto dataDownDto = new DataDownDto();
            dataDownDto.setConfirmed(false);
            dataDownDto.setContentType(CONTENT_TYPE_TEXT);
            
            String fPort = commandRequest.getValue().getArg().getOrDefault(F_PORT_KEY, F_PORT_DEFAULT);
            dataDownDto.setfPort(Integer.parseInt(fPort));
            
            EndDeviceDto endDeviceDto = new EndDeviceDto();
            endDeviceDto.setDevEui(commandRequest.getNodeId());
            
            dataDownDto.setEndDevice(endDeviceDto);
            dataDownDto.setPayload(commandRequest.getValue().getReq());
            return dataDownDto;
        }
    }
}
