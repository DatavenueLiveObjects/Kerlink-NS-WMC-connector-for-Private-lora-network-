package com.orange.lo.sample.kerlink2lo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.lo.sample.kerlink2lo.kerlink.model.DataDownEventDto;
import com.orange.lo.sample.kerlink2lo.kerlink.model.DataUpDto;
import com.orange.lo.sample.kerlink2lo.lo.LoApiExternalConnectorService;
import com.orange.lo.sample.kerlink2lo.utils.Counters;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Kerlink2LoControllerTest {
    private static final String KERLINK_ACCOUNT_HEADER = "Kerlink-Account";

    private Kerlink2LoController kerlink2LoController;
    @Mock
    private Counters counters;
    @Mock
    private LoApiExternalConnectorService externalConnectorService;

    @BeforeEach
    private void setUp() {
        kerlink2LoController = new Kerlink2LoController(externalConnectorService, counters);
    }

    @Test
    void shouldSendMessageAndIncrementCountersWhenDataUpReceivesMessageAndAccountHeader() throws Exception {
        DataUpDto dataUpDto = buildExampleDataUpDto();
        HttpHeaders headers = new HttpHeaders();
        headers.add(KERLINK_ACCOUNT_HEADER, "acc");
        Counter sentAttemptCounter = Mockito.mock(Counter.class);
        when(counters.getMessageSentAttemptCounter()).thenReturn(sentAttemptCounter);
        Counter messageReadCounter = Mockito.mock(Counter.class);
        when(counters.getMessageReadCounter()).thenReturn(messageReadCounter);

        kerlink2LoController.dataUp(dataUpDto, headers).call();

        Mockito.verify(externalConnectorService, times(1)).sendMessage(dataUpDto, "acc");
        Mockito.verify(messageReadCounter, times(1)).increment();
        Mockito.verify(sentAttemptCounter, times(1)).increment();
    }

    @Test
    void shouldNotSendMessageAndIncrementCountersWhenDataUpReceivesMessageAndNoAccountHeader() throws Exception {
        DataUpDto dataUpDto = buildExampleDataUpDto();
        HttpHeaders headers = new HttpHeaders();
        Counter sentAttemptCounter = Mockito.mock(Counter.class);
        when(counters.getMessageSentAttemptCounter()).thenReturn(sentAttemptCounter);
        Counter messageReadCounter = Mockito.mock(Counter.class);
        when(counters.getMessageReadCounter()).thenReturn(messageReadCounter);
        Counter messageSentAttemptFailedCounter = Mockito.mock(Counter.class);
        when(counters.getMessageSentAttemptFailedCounter()).thenReturn(messageSentAttemptFailedCounter);
        Counter messageSentFailedCounter = Mockito.mock(Counter.class);
        when(counters.getMessageSentFailedCounter()).thenReturn(messageSentFailedCounter);

        kerlink2LoController.dataUp(dataUpDto, headers).call();

        Mockito.verify(externalConnectorService, times(0)).sendMessage(any(), any());
        Mockito.verify(messageReadCounter, times(1)).increment();
        Mockito.verify(sentAttemptCounter, times(1)).increment();
        Mockito.verify(messageSentAttemptFailedCounter, times(1)).increment();
        Mockito.verify(messageSentFailedCounter, times(1)).increment();
    }

    @Test
    void dataDown() throws Exception {
        DataDownEventDto dataDownDto = new DataDownEventDto();
        dataDownDto.setStatus("OK");
        HttpHeaders headers = new HttpHeaders();
        headers.add(KERLINK_ACCOUNT_HEADER, "acc");
        kerlink2LoController.dataDown(dataDownDto, headers).call();

        Mockito.verify(externalConnectorService, times(1)).sendCommandResponse(dataDownDto);
    }

    private DataUpDto buildExampleDataUpDto() throws IOException {
        URL fileUrl = getClass().getResource("/dataUpDtoExample.json");
        File jsonFile = new File(fileUrl.getFile());
        ObjectMapper objectMapper = new ObjectMapper();
        DataUpDto dataUpDto = objectMapper.readValue(jsonFile,DataUpDto.class);
        return dataUpDto;
    }
}
