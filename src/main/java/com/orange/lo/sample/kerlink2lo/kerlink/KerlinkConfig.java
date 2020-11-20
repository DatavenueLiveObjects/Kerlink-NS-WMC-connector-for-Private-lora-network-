/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlink2lo.kerlink;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class KerlinkConfig {

    private KerlinkPropertiesList kerlinkPropertiesList;

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
        Map<String, KerlinkApi> map = new HashMap<>();
        kerlinkPropertiesList.getKerlinkList().forEach(kerlinkProperties -> map.put(
                kerlinkProperties.getKerlinkAccountName(),
                new KerlinkApi(kerlinkProperties, restTemplate())
        ));
        return map;
    }
}