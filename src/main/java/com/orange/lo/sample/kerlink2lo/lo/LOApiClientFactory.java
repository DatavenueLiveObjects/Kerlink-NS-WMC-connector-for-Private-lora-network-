package com.orange.lo.sample.kerlink2lo.lo;

import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.LOApiClientParameters;
import com.orange.lo.sdk.LOApiClientParameters.LOApiClientParametersBuilder;
import com.orange.lo.sdk.externalconnector.DataManagementExtConnectorCommandCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class LOApiClientFactory {

    @Bean
    public LOApiClientParameters LOApiClientParameters(LOApiClientParametersBuilder builder, DataManagementExtConnectorCommandCallback callback) {
        return builder.dataManagementExtConnectorCommandCallback(callback)
                .build();
    }

    @Bean
    public LOApiClient LOApiClient(LOApiClientParameters parameters) {
        return new LOApiClient(parameters);
    }
}
