package com.orange.lo.sample.kerlink2lo.lo;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import com.orange.lo.sample.kerlink2lo.kerlink.model.DataDownEventDto;
import com.orange.lo.sdk.LOApiClientParameters;
import com.orange.lo.sdk.externalconnector.DataManagementExtConnector;
import com.orange.lo.sdk.mqtt.MqttClientFactory;
import com.orange.lo.sdk.mqtt.MqttClientFactoryImpl;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class ExternalConnectorCommandResponseWrapperTest {

    public static final int PORT = 8081;
    private static final String TEST_DATA_DOWN_ID = "data_down_id";
    private static final String TEST_NODE_ID = "node_id";
    private static final String TEST_LO_ID = "lo_id";
    private ExternalConnectorCommandResponseWrapper externalConnectorCommandResponseWrapper;
    private IMqttClient mqttClient;

    @BeforeEach
    private void setUp() {
        CommandMapper commandMapper = new CommandMapper();
        commandMapper.put(TEST_DATA_DOWN_ID, TEST_LO_ID, TEST_NODE_ID);
        mqttClient = Mockito.mock(IMqttClient.class);
        MqttClientFactory mqttClientFactory = () -> mqttClient;
        LOApiClientParameters loApiClientParameters = LOApiClientParameters.builder()
                .apiKey("dummy")
                .hostname("localhost")
                .build();
        DataManagementExtConnector dataManagementExtConnector = new DataManagementExtConnector(loApiClientParameters, mqttClientFactory);
        externalConnectorCommandResponseWrapper = new ExternalConnectorCommandResponseWrapper(commandMapper, dataManagementExtConnector);
    }

    @Test
    void sendCommandResponse() throws MqttException {
        doNothing().when(mqttClient).publish(anyString(), ArgumentMatchers.any(MqttMessage.class));

        DataDownEventDto dataDownEventDto = new DataDownEventDto();
        dataDownEventDto.setDataDownId(TEST_DATA_DOWN_ID);
        externalConnectorCommandResponseWrapper.sendCommandResponse(dataDownEventDto);
    }
}