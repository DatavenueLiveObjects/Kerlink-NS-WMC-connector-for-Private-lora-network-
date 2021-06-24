/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo.kerlink;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties
public class KerlinkPropertiesList {

    private final List<KerlinkProperties> kerlinkList;

    public KerlinkPropertiesList(List<KerlinkProperties> kerlinkList) {
        this.kerlinkList = kerlinkList;
    }

    public List<KerlinkProperties> getKerlinkList() {
        return kerlinkList;
    }
}
