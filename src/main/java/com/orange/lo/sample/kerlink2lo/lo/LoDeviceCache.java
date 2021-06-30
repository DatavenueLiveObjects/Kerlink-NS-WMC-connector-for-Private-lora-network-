/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo.lo;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class keeps devices ids without LO prefix. In fact it keeps node id.
 */
@Component
public class LoDeviceCache {

    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public void add(String deviceId, String groupName) {
        cache.put(deviceId, groupName);
    }

    public void addAll(Collection<? extends String> c, String groupName) {
        c.forEach(d -> cache.put(d, groupName));
    }

    public void delete(String deviceId) {
        cache.remove(deviceId);
    }

    public boolean contains(String deviceId) {
        return cache.containsKey(deviceId);
    }

    public String getGroup(String deviceId) {
        return cache.get(deviceId);
    }
}
