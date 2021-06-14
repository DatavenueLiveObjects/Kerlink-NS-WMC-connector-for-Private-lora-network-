package com.orange.lo.sample.kerlink2lo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.lo.sample.kerlink2lo.kerlink.model.DataDownEventDto;
import com.orange.lo.sample.kerlink2lo.kerlink.model.DataUpDto;
import com.orange.lo.sample.kerlink2lo.lo.ExternalConnectorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;

import java.io.File;
import java.io.IOException;
import java.net.URL;

class Kerlink2LoControllerTest {
    private static final String KERLINK_ACCOUNT_HEADER = "Kerlink-Account";

    private Kerlink2LoController kerlink2LoController;

    @BeforeEach
    private void setUp() {
        ExternalConnectorService externalConnectorService = Mockito.mock(ExternalConnectorService.class);
        kerlink2LoController = new Kerlink2LoController(externalConnectorService);
    }

    @Test
    void dataUp() throws Exception {
        DataUpDto dataUpDto = buildExampleDataUpDto();
        HttpHeaders headers = new HttpHeaders();
        headers.add(KERLINK_ACCOUNT_HEADER, "acc");
        kerlink2LoController.dataUp(dataUpDto, headers).call();
    }

    @Test
    void dataDown() throws Exception {
        // TODO: Example of proper DTO here
        DataDownEventDto dataDownDto = new DataDownEventDto();
        HttpHeaders headers = new HttpHeaders();
        headers.add(KERLINK_ACCOUNT_HEADER, "acc");
        kerlink2LoController.dataDown(dataDownDto, headers).call();
    }

    private DataUpDto buildExampleDataUpDto() throws IOException {
        URL fileUrl = getClass().getResource("/dataUpDtoExample.json");
        File jsonFile = new File(fileUrl.getFile());
        ObjectMapper objectMapper = new ObjectMapper();
        DataUpDto dataUpDto = objectMapper.readValue(jsonFile,DataUpDto.class);
        return dataUpDto;
    }
}
