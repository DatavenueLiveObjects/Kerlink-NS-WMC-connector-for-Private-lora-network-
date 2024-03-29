/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo.kerlink;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class KerlinkLoginCaller {

    private final List<KerlinkApi> kerlinkApiList;

    public KerlinkLoginCaller(Map<String, KerlinkApi> kerlinkApiMap) {
        this.kerlinkApiList = new ArrayList<>(kerlinkApiMap.values());
    }

    @Scheduled(fixedRateString = "${kerlink-global.login-interval}")
    public void loginAll() {
        kerlinkApiList.forEach(KerlinkApi::login);
    }
}
