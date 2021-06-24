/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo.lo;

import com.orange.lo.sdk.LOApiClient;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class LoApiClientWrapper {

    private final LOApiClient loApiClient;

    public LoApiClientWrapper(LOApiClient loApiClient) {
        this.loApiClient = loApiClient;
    }

    @PostConstruct
    public void connect() {
        loApiClient.getDataManagementFifo().connectAndSubscribe();
    }
}
