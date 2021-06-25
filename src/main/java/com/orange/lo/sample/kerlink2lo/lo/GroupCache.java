/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo.lo;

import com.orange.lo.sdk.rest.model.Group;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GroupCache {
    private final Map<String, Group> backingMap = new ConcurrentHashMap<>();

    public void put(String accountName, Group group) {
        backingMap.put(accountName, group);
    }

    public Group get(String accountName) {
        return backingMap.get(accountName);
    }

    public void putAll(Map<String, Group> groups) {
        backingMap.putAll(groups);
    }
}
