package com.orange.lo.sample.kerlink2lo.lo;

import com.orange.lo.sdk.LOApiClient;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class LoApiClientWrapper {

    private final LOApiClient loApiClient;

    public LoApiClientWrapper(LOApiClient loApiClient) {
        this.loApiClient = loApiClient;
    }

    @PostConstruct
    public void Connect() {
        loApiClient.getDataManagementFifo().connectAndSubscribe();
    }
}
