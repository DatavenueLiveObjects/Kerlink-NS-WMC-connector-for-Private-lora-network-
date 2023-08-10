package com.orange.lo.sample.kerlink2lo.lo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.lo.sample.kerlink2lo.kerlink.model.DataDownEventDto;
import com.orange.lo.sample.kerlink2lo.kerlink.model.DataUpDto;
import com.orange.lo.sample.kerlink2lo.lo.CommandMapper.LoCommand;
import com.orange.lo.sample.kerlink2lo.utils.Counters;
import com.orange.lo.sdk.externalconnector.DataManagementExtConnector;
import com.orange.lo.sdk.externalconnector.model.DataMessage;
import com.orange.lo.sdk.externalconnector.model.Value;
import com.orange.lo.sdk.mqtt.exceptions.LoMqttException;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoApiExternalConnectorServiceTest {

    public static final String EXPECTED_PAYLOAD = "MTU7MjU=";
    public static final String KERLINK_ACCOUNT_NAME = "acc";
    private static final String KERLINK_DEVICE_ID = "deviceId";
    private static final String NODE_ID = "node";
    private static final String COMMAND_ID = "command";
    @Mock
    private DataManagementExtConnector dataManagementExtConnector;
    private LoApiExternalConnectorService loApiExternalConnectorService;
    @Mock
    private LoDeviceProvider loDeviceProvider;
    @Mock
    private CommandMapper commandMapper;
    @Mock
    private Counters counters;
    @Mock
    private Counter messageSentCounter = Mockito.mock(Counter.class);



    @BeforeEach
    private void setUp() {
        LoDeviceCache deviceCache
                = Mockito.mock(LoDeviceCache.class);
        LoProperties loProperties = Mockito.mock(LoProperties.class);
        loApiExternalConnectorService = new LoApiExternalConnectorService(
                deviceCache,
                loDeviceProvider,
                dataManagementExtConnector,
                loProperties,
                commandMapper,
                counters);
    }

    @Test
    void sendMessageWithCorrectDataWorks() throws IOException {
        DataUpDto dataUpDto = buildExampleDataUpDto();
        ArgumentCaptor<DataMessage> dataMessageArgumentCaptor = ArgumentCaptor.forClass(DataMessage.class);
        when(counters.getMessageSentCounter()).thenReturn(messageSentCounter);

        loApiExternalConnectorService.sendMessage(dataUpDto, KERLINK_ACCOUNT_NAME);

        Mockito.verify(dataManagementExtConnector).sendMessage(Mockito.anyString(), dataMessageArgumentCaptor.capture());
        assertTrue(dataMessageArgumentCaptor.getValue().getValue() instanceof DataUpDto);
        assertEquals(EXPECTED_PAYLOAD, ((DataUpDto) dataMessageArgumentCaptor.getValue().getValue()).getPayload());
        Mockito.verify(messageSentCounter, times(1)).increment();
    }

    @Test
    void connectCallsApiConnect() {
        loApiExternalConnectorService.connect();

        Mockito.verify(dataManagementExtConnector, times(1)).connect();
    }

    @Test
    void sendCommandResponseCallsApiSendCommandResponse() throws IOException {
        DataDownEventDto dataDownEventDto = buildExampleDataDownEventDto();
        LoCommand loCommand = commandMapper.new LoCommand(COMMAND_ID, NODE_ID);
        Optional<LoCommand> optionalLoCommand = Optional.of(loCommand);
        doReturn(optionalLoCommand).when(commandMapper).get(anyString());

        loApiExternalConnectorService.sendCommandResponse(dataDownEventDto);

        Mockito.verify(dataManagementExtConnector, times(1)).sendCommandResponse(any());
    }

    @Test
    void createDeviceCallsAddDevice() {
        loApiExternalConnectorService.createDevice(KERLINK_DEVICE_ID, KERLINK_ACCOUNT_NAME);

        verify(loDeviceProvider, times(1)).addDevice(anyString(), anyString());
    }

    @Test
    void createDeviceSendsStatusUpdate() {
        loApiExternalConnectorService.createDevice(KERLINK_DEVICE_ID, KERLINK_ACCOUNT_NAME);

        verify(dataManagementExtConnector, times(1)).sendStatus(anyString(), any());
    }

    @Test
    void createDeviceResendsStatusUpdateOnFailure() {
        doThrow(LoMqttException.class).doNothing().when(dataManagementExtConnector).sendStatus(anyString(), any());

        loApiExternalConnectorService.createDevice(KERLINK_DEVICE_ID, KERLINK_ACCOUNT_NAME);

        verify(dataManagementExtConnector, times(2)).sendStatus(anyString(), any());
    }

    @Test
    void createDeviceThrowsBeforeSendStatusOnFailure() {
        doThrow(RestClientException.class).when(loDeviceProvider).addDevice(anyString(), anyString());

        assertThrows(Exception.class, () ->
                loApiExternalConnectorService.createDevice(KERLINK_DEVICE_ID, KERLINK_ACCOUNT_NAME));

        verify(dataManagementExtConnector, times(0)).sendStatus(anyString(), any());

    }

    @Test
    void deleteDeviceCallsDeleteDevice() {
        loApiExternalConnectorService.deleteDevice(KERLINK_DEVICE_ID);

        verify(loDeviceProvider, times(1)).deleteDevice(anyString());
    }

    private DataUpDto buildExampleDataUpDto() throws IOException {
        URL fileUrl = getClass().getResource("/dataUpDtoExample.json");
        assert fileUrl != null;
        File jsonFile = new File(fileUrl.getFile());
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonFile, DataUpDto.class);
    }

    private DataDownEventDto buildExampleDataDownEventDto() throws IOException {
        URL fileUrl = getClass().getResource("/dataDownEventDtoExample.json");
        assert fileUrl != null;
        File jsonFile = new File(fileUrl.getFile());
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonFile, DataDownEventDto.class);
    }
}
