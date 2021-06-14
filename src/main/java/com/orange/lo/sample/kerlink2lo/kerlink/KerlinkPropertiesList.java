package com.orange.lo.sample.kerlink2lo.kerlink;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties()
public class KerlinkPropertiesList {

    private final List<KerlinkProperties> kerlinkList;

    public KerlinkPropertiesList(List<KerlinkProperties> kerlinkList) {
        this.kerlinkList = kerlinkList;
    }

    public List<KerlinkProperties> getKerlinkList() {
        return kerlinkList;
    }
}
