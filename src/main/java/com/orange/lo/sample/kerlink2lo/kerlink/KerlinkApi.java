/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo.kerlink;

import com.orange.lo.sample.kerlink2lo.kerlink.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class KerlinkApi {
    public static final String DATA_DOWN_PATH = "/application/dataDown";
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String NEXT = "next";
    private static final int COMMAND_OFFSET = (DATA_DOWN_PATH + "/").length();
    private final RestTemplate restTemplate;
    private final KerlinkProperties kerlinkProperties;
    private final String firstHref;
    private HttpEntity<Void> httpEntity;
    private String token;

    public KerlinkApi(KerlinkProperties kerlinkProperties, @Qualifier("kerlinkRestTemplate") RestTemplate restTemplate) {
        this.kerlinkProperties = kerlinkProperties;
        this.restTemplate = restTemplate;
        this.firstHref = "/application/endDevices?fields=devEui,devAddr,name,country,status&sort=%2BdevEui&page=1&pageSize=" + kerlinkProperties.getPageSize();
        login();
    }

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
                if (Objects.isNull(body)) {
                    throw new IllegalArgumentException("Kerlink token is null");
                }
                this.token = "Bearer " + body.getToken();
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
            if (body != null) {
                LOG.trace("And got {} devices", body.getList().size());
                devicesList.addAll(body.getList());
                href = getNextPageHref(body.getLinks());
            }
        }
        return devicesList;
    }

    public Optional<String> sendCommand(DataDownDto dataDownDto) {
        String url = kerlinkProperties.getBaseUrl() + DATA_DOWN_PATH;
        HttpEntity<DataDownDto> dataDownDtoHttpEntity = prepareHttpEntity(token, dataDownDto);
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, dataDownDtoHttpEntity, Void.class);
        URI location = response.getHeaders().getLocation();
        String commandId = location != null ? location.getPath().substring(COMMAND_OFFSET) : null;
        return Optional.ofNullable(commandId);
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
