package com.orange.lo.sample.kerlink2lo.kerlink;

import com.orange.lo.sample.kerlink2lo.kerlink.model.*;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KerlinkApiTest {

    public static final String TOKEN = "abcdef";
    @Mock
    RestTemplate restTemplate;

    private KerlinkApi kerlinkApi;

    @BeforeEach
    public void setUp() {

        ResponseEntity<JwtDto> loginResponse = getLoginResponse(TOKEN);
        when(restTemplate.postForEntity(eq("localhost/application/login"), any(), eq(JwtDto.class)))
                .thenReturn(loginResponse);

        KerlinkProperties kerlinkProperties = new KerlinkProperties();
        kerlinkProperties.setBaseUrl("localhost");
        kerlinkProperties.setPageSize(10);

        kerlinkApi = new KerlinkApi(kerlinkProperties, restTemplate);
    }

    @Test
    public void shouldGetAllDevices() {
        // given
        int pageSize = 10;
        int deviceAmount = 5;
        String url = "localhost/application/endDevices?fields=devEui,devAddr,name,country,status&sort=%2BdevEui&page=1&pageSize=10";
        ParameterizedTypeReference<PaginatedDto<EndDeviceDto>> returnType = new ParameterizedTypeReference<PaginatedDto<EndDeviceDto>>() {
        };

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(), eq(returnType))).thenReturn(getDevicesResponse(deviceAmount, pageSize));

        // when
        List<EndDeviceDto> endDevices = kerlinkApi.getEndDevices();

        // then
        Assertions.assertEquals(deviceAmount, endDevices.size());
    }

    @Test
    public void shouldGetAllDevicesIn2Calls() {
        // given
        int pageSize = 10;
        int firstDeviceAmount = 10;
        int secondDeviceAmount = 5;
        int nextPage = 2;

        String firstUrl = "localhost/application/endDevices?fields=devEui,devAddr,name,country,status&sort=%2BdevEui&page=1&pageSize=10";
        String secondUrl = "localhost/application/endDevices?fields=devEui,devAddr,name,country,status&sort=%2BdevEui&page=2&pageSize=10";
        ParameterizedTypeReference<PaginatedDto<EndDeviceDto>> returnType = new ParameterizedTypeReference<PaginatedDto<EndDeviceDto>>() {
        };

        when(restTemplate.exchange(eq(firstUrl), eq(HttpMethod.GET), any(), eq(returnType))).thenReturn(getDevicesResponse(firstDeviceAmount, pageSize, nextPage));
        when(restTemplate.exchange(eq(secondUrl), eq(HttpMethod.GET), any(), eq(returnType))).thenReturn(getDevicesResponse(secondDeviceAmount, pageSize));

        // when
        List<EndDeviceDto> endDevices = kerlinkApi.getEndDevices();

        // then
        Assertions.assertEquals(15, endDevices.size());
    }

    @Test
    public void shouldLoginAndSendCommand() throws RestClientException, URISyntaxException {
        // given
        String commandId = "123456";

        DataDownDto dataDownDto = new DataDownDto();
        dataDownDto.setPayload("command");

        when(restTemplate.exchange(eq("localhost/application/dataDown"), eq(HttpMethod.POST), eq(getSendCommandHttpEntity(TOKEN, dataDownDto)), eq(Void.class))).thenReturn(getCommandResponse(commandId));

        // when
        kerlinkApi.login();
        Optional<String> command = kerlinkApi.sendCommand(dataDownDto);

        // then
        Assertions.assertEquals(commandId, command.get());
    }

    private HttpEntity<DataDownDto> getSendCommandHttpEntity(String token, DataDownDto dataDownDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<DataDownDto> httpEntity = new HttpEntity<>(dataDownDto, headers);
        return httpEntity;
    }

    private ResponseEntity<JwtDto> getLoginResponse(String token) {
        JwtDto jwtDto = new JwtDto();
        jwtDto.setToken(token);
        ResponseEntity<JwtDto> responseEntity = new ResponseEntity<JwtDto>(jwtDto, HttpStatus.CREATED);
        return responseEntity;
    }

    private ResponseEntity<Void> getCommandResponse(String commandId) throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI("/application/dataDown/" + commandId));

        ResponseEntity<Void> response = new ResponseEntity<Void>(headers, HttpStatus.CREATED);
        return response;
    }

    private ResponseEntity<PaginatedDto<EndDeviceDto>> getDevicesResponse(int amount, int pageSize) {
        return getDevicesResponse(amount, pageSize, 0);
    }

    private ResponseEntity<PaginatedDto<EndDeviceDto>> getDevicesResponse(int amount, int pageSize, int nextPage) {
        LinkDto linkDto = new LinkDto();
        if (nextPage > 0) {
            linkDto.setRel("next");
            linkDto.setHref("/application/endDevices?fields=devEui,devAddr,name,country,status&sort=%2BdevEui&page=" + nextPage + "&pageSize=" + pageSize);
        }
        List<LinkDto> links = Lists.list(linkDto);

        List<EndDeviceDto> list = IntStream.rangeClosed(1, amount).mapToObj(i -> new EndDeviceDto()).collect(Collectors.toList());

        PaginatedDto<EndDeviceDto> paginatedDto = new PaginatedDto<EndDeviceDto>();
        paginatedDto.setList(list);
        paginatedDto.setLinks(links);

        return new ResponseEntity<>(paginatedDto, HttpStatus.OK);
    }
}
