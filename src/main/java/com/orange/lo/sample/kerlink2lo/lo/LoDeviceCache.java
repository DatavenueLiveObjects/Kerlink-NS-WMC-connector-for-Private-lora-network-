package com.orange.lo.sample.kerlink2lo.lo;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/**
 * This class keeps devices ids without LO prefix. In fact it keeps node id.
 */
@Component
public class LoDeviceCache {

    private Map<String, String> cache = new ConcurrentHashMap<>();

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
