/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo.kerlink;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class KerlinkConfig {

    private final KerlinkPropertiesList kerlinkPropertiesList;

    public KerlinkConfig(KerlinkPropertiesList kerlinkPropertiesList) {
        this.kerlinkPropertiesList = kerlinkPropertiesList;
    }

    @Bean(name = "kerlinkRestTemplate")
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
        defaultUriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        restTemplate.setUriTemplateHandler(defaultUriBuilderFactory);
        return restTemplate;
    }

    @Bean
    public Map<String, KerlinkApi> kerlinkApiMap() {
        return kerlinkPropertiesList
                .getKerlinkList()
                .stream()
                .collect(Collectors.toMap(KerlinkProperties::getKerlinkAccountName,
                        kerlinkProperties -> new KerlinkApi(kerlinkProperties, restTemplate())));
    }
}
