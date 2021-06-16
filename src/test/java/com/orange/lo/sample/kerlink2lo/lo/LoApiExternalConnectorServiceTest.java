package com.orange.lo.sample.kerlink2lo.lo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.lo.sample.kerlink2lo.kerlink.model.DataUpDto;
import com.orange.lo.sdk.externalconnector.DataManagementExtConnector;
import com.orange.lo.sdk.externalconnector.model.DataMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class LoApiExternalConnectorServiceTest {

    public static final String EXPECTED_DECODED_PAYLOAD = "15;25";
    public static final String KERLINK_ACCOUNT_NAME = "";

    @Test
    void sendMessage() throws IOException {
        ExternalConnectorCommandResponseWrapper externalConnectorCommandResponseWrapper =
                Mockito.mock(ExternalConnectorCommandResponseWrapper.class);
        DataManagementExtConnector dataManagementExtConnector
                = Mockito.mock(DataManagementExtConnector.class);
        PayloadDecoder connectorDecoder
                = Mockito.mock(PayloadDecoder.class);
        LoDeviceProvider loDeviceProvider
                = Mockito.mock(LoDeviceProvider.class);
        LoDeviceCache deviceCache
                = Mockito.mock(LoDeviceCache.class);
        LoApiExternalConnectorService loApiExternalConnectorService =
                new LoApiExternalConnectorService(
                        externalConnectorCommandResponseWrapper,
                        deviceCache,
                        loDeviceProvider,
                        connectorDecoder,
                        dataManagementExtConnector
                );
        DataUpDto dataUpDto = buildExampleDataUpDto();
        ArgumentCaptor<DataMessage> dataMessageArgumentCaptor = ArgumentCaptor.forClass(DataMessage.class);

        loApiExternalConnectorService.sendMessage(dataUpDto, KERLINK_ACCOUNT_NAME);

        Mockito.verify(dataManagementExtConnector).sendMessage(Mockito.anyString(), dataMessageArgumentCaptor.capture());
        assertEquals(EXPECTED_DECODED_PAYLOAD, dataMessageArgumentCaptor.getValue().getValue());
    }

    private DataUpDto buildExampleDataUpDto() throws IOException {
        URL fileUrl = getClass().getResource("/dataUpDtoExample.json");
        File jsonFile = new File(fileUrl.getFile());
        ObjectMapper objectMapper = new ObjectMapper();
        DataUpDto dataUpDto = objectMapper.readValue(jsonFile,DataUpDto.class);
        return dataUpDto;
    }
}
