/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlink2lo.lo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.lo.sample.kerlink2lo.exceptions.LoMqttException;
import com.orange.lo.sample.kerlink2lo.exceptions.ParseException;
import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkApi;
import com.orange.lo.sample.kerlink2lo.kerlink.model.DataDownDto;
import com.orange.lo.sample.kerlink2lo.kerlink.model.DataDownEventDto;
import com.orange.lo.sample.kerlink2lo.kerlink.model.EndDeviceDto;
import com.orange.lo.sample.kerlink2lo.lo.CommandMapper.LoCommand;
import com.orange.lo.sample.kerlink2lo.lo.model.CommandRequest;
import com.orange.lo.sample.kerlink2lo.lo.model.CommandResponse;
import com.orange.lo.sdk.LOApiClient;
import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Optional;

@Component
public class ExternalConnector {

    private static final String COMMAND_RESPONSE_TOPIC = "connector/v1/responses/command";

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private IMqttClient loMqttClient;
    private ObjectMapper objectMapper;
    private LoProperties loProperties;
    private CommandMapper commandMapper;

    public ExternalConnector(IMqttClient loMqttClient, ObjectMapper objectMapper, CommandMapper commandMapper, LoProperties loProperties) {
        this.loMqttClient = loMqttClient;
        this.objectMapper = objectMapper;
        this.commandMapper = commandMapper;
        this.loProperties = loProperties;
    }

    public void sendCommandResponse(DataDownEventDto dataDownEventDto) {
        Optional<LoCommand> loCommand = commandMapper.get(dataDownEventDto.getDataDownId());

        if (loCommand.isPresent()) {
            LOG.debug("Sending command response for device {}", loCommand.get().getNodeId());
            CommandResponse commandResponse = new CommandResponse(loCommand.get().getId(), loCommand.get().getNodeId());

            MqttMessage msg = prepareMqttMessage(commandResponse);
            publish(COMMAND_RESPONSE_TOPIC, msg);
        } else {
            if (LOG.isDebugEnabled()) {
                String cleanDtoString = StringEscapeUtils.escapeJava(dataDownEventDto.toString());
                LOG.debug("Receive unknown command status from Kerlink: {}", cleanDtoString);
            }
        }
    }

    private MqttMessage prepareMqttMessage(Object dataMessage) {
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

}
