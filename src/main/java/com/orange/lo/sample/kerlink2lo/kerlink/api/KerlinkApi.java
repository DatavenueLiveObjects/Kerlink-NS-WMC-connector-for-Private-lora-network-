/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlink2lo.kerlink.api;

import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkProperties;
import com.orange.lo.sample.kerlink2lo.kerlink.api.model.DataDownDto;
import com.orange.lo.sample.kerlink2lo.kerlink.api.model.EndDeviceDto;
import com.orange.lo.sample.kerlink2lo.kerlink.api.model.JwtDto;
import com.orange.lo.sample.kerlink2lo.kerlink.api.model.LinkDto;
import com.orange.lo.sample.kerlink2lo.kerlink.api.model.PaginatedDto;
import com.orange.lo.sample.kerlink2lo.kerlink.api.model.UserDto;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

@EnableScheduling
public class KerlinkApi {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String NEXT = "next";
    private RestTemplate restTemplate;
    private KerlinkProperties kerlinkProperties;
    private HttpEntity<Void> httpEntity;
    private String firstHref;
    private String token;

    @Autowired
    public KerlinkApi(KerlinkProperties kerlinkProperties, @Qualifier("kerlinkRestTemplate") RestTemplate restTemplate) {
        this.kerlinkProperties = kerlinkProperties;
        this.restTemplate = restTemplate;
        this.firstHref = "/application/endDevices?fields=devEui,devAddr,name,country,status&sort=%2BdevEui&page=1&pageSize=" + kerlinkProperties.getPageSize();
        login();
    }

    @Scheduled(fixedRateString = "${kerlink.login-interval}")
    public void login() {
        LOG.info("Trying to login and get bearer token");
        UserDto userDto = new UserDto();
        userDto.setLogin(kerlinkProperties.getLogin());
        userDto.setPassword(kerlinkProperties.getPassword());
        String url = kerlinkProperties.getBaseUrl() + "/application/login";
        try {
            ResponseEntity<JwtDto> responseEntity = restTemplate.postForEntity(url, userDto, JwtDto.class);
            if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
                JwtDto body = responseEntity.getBody();
                this.token = "Bearer " + (Objects.nonNull(body) ? body.getToken() : "");
                this.httpEntity = prepareHttpEntity(this.token);
                LOG.debug("Kerlink Token: {}", this.token);
            } else {
                LOG.error("Error while trying to login to Kerlink platform, returned status code is {}", responseEntity.getStatusCodeValue());
                System.exit(1);
            }
        } catch (Exception e) {
            LOG.error("Error while trying to login to Kerlink platform, ", e);
            System.exit(1);
        }
    }

    public List<EndDeviceDto> getEndDevices() {
        ParameterizedTypeReference<PaginatedDto<EndDeviceDto>> returnType = new ParameterizedTypeReference<PaginatedDto<EndDeviceDto>>() {
        };
        List<EndDeviceDto> devicesList = new ArrayList<>();
        Optional<String> href = Optional.of(firstHref);
        while (href.isPresent()) {
            String url = kerlinkProperties.getBaseUrl() + href.get();
            LOG.trace("Calling kerlink url {}", url);
            ResponseEntity<PaginatedDto<EndDeviceDto>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, returnType);
            PaginatedDto<EndDeviceDto> body = responseEntity.getBody();
            LOG.trace("And got {} devices", body.getList().size());
            devicesList.addAll(body.getList());
            href = getNextPageHref(body.getLinks());
        }
        return devicesList;
    }

    public Optional<String> sendCommand(DataDownDto dataDownDto) {
        String url = kerlinkProperties.getBaseUrl() + "/application/dataDown";
        HttpEntity<DataDownDto> dataDownDtoHttpEntity = prepareHttpEntity(token, dataDownDto);
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, dataDownDtoHttpEntity, Void.class);
        String commandId = response.getHeaders().getLocation().getPath().substring(22);
        return Optional.of(commandId);
    }

    private HttpEntity<Void> prepareHttpEntity(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json,application/vnd.kerlink.iot-v1+json");
        headers.set("Authorization", token);
        return new HttpEntity<>(headers);
    }

    private <T> HttpEntity<T> prepareHttpEntity(String token, T t) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", token);
        return new HttpEntity<>(t, headers);
    }

    private Optional<String> getNextPageHref(List<LinkDto> links) {
        return links.stream().filter(l -> NEXT.equals(l.getRel())).findFirst().map(LinkDto::getHref);
    }
}