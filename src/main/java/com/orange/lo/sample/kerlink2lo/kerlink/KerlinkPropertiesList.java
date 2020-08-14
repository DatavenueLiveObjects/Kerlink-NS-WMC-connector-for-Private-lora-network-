package com.orange.lo.sample.kerlink2lo.kerlink;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties()
public class KerlinkPropertiesList {

    private List<KerlinkProperties> kerlinkList;

    public List<KerlinkProperties> getKerlinkList() {
        return kerlinkList;
    }

    public void setKerlinkList(List<KerlinkProperties> kerlinkList) {
        this.kerlinkList = kerlinkList;
    }
}