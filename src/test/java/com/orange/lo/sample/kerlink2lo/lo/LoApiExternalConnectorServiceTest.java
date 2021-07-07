package com.orange.lo.sample.kerlink2lo.lo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.lo.sample.kerlink2lo.kerlink.model.DataDownEventDto;
import com.orange.lo.sample.kerlink2lo.kerlink.model.DataUpDto;
import com.orange.lo.sdk.externalconnector.DataManagementExtConnector;
import com.orange.lo.sdk.externalconnector.model.DataMessage;
import com.orange.lo.sdk.externalconnector.model.Value;
import com.orange.lo.sdk.mqtt.exceptions.LoMqttException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.web.client.RestClientException;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoApiExternalConnectorServiceTest {

    public static final String EXPECTED_DECODED_PAYLOAD = "15;25";
    public static final String KERLINK_ACCOUNT_NAME = "acc";
    private static final String KERLINK_DEVICE_ID = "deviceId";
    private DataManagementExtConnector dataManagementExtConnector;
    private LoApiExternalConnectorService loApiExternalConnectorService;
    private ExternalConnectorCommandResponseWrapper externalConnectorCommandResponseWrapper;
    private LoDeviceProvider loDeviceProvider;

    @BeforeEach
    private void setUp() {
        externalConnectorCommandResponseWrapper = Mockito.mock(ExternalConnectorCommandResponseWrapper.class);
        dataManagementExtConnector = Mockito.mock(DataManagementExtConnector.class);
        loDeviceProvider = Mockito.mock(LoDeviceProvider.class);
        LoDeviceCache deviceCache
                = Mockito.mock(LoDeviceCache.class);
        LoProperties loProperties = Mockito.mock(LoProperties.class);
        loApiExternalConnectorService = new LoApiExternalConnectorService(
                externalConnectorCommandResponseWrapper,
                deviceCache,
                loDeviceProvider,
                dataManagementExtConnector,
                loProperties);
    }

    @Test
    void sendMessageWithCorrectDataWorks() throws IOException {
        DataUpDto dataUpDto = buildExampleDataUpDto();
        ArgumentCaptor<DataMessage> dataMessageArgumentCaptor = ArgumentCaptor.forClass(DataMessage.class);

        loApiExternalConnectorService.sendMessage(dataUpDto, KERLINK_ACCOUNT_NAME);

        Mockito.verify(dataManagementExtConnector).sendMessage(Mockito.anyString(), dataMessageArgumentCaptor.capture());
        assertTrue(dataMessageArgumentCaptor.getValue().getValue() instanceof Value);
        assertEquals(EXPECTED_DECODED_PAYLOAD, ((Value) dataMessageArgumentCaptor.getValue().getValue()).getPayload());
    }

    @Test
    void connectCallsApiConnect() {
        loApiExternalConnectorService.connect();

        Mockito.verify(dataManagementExtConnector, times(1)).connect();
    }

    @Test
    void sendCommandResponseCallsWrapperSendCommandResponse() {
        DataDownEventDto dataDownEventDto = Mockito.mock(DataDownEventDto.class);

        loApiExternalConnectorService.sendCommandResponse(dataDownEventDto);

        Mockito.verify(externalConnectorCommandResponseWrapper, atLeastOnce()).sendCommandResponse(any());
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
}
