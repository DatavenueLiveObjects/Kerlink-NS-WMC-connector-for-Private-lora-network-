package com.orange.lo.sample.kerlink2lo.lo;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

import org.springframework.stereotype.Component;

/**
 * This class keeps devices ids without LO prefix. In fact it keeps node id.
 */
@Component
public class LoDeviceCache {

    private KeySetView<String, Boolean> cache = ConcurrentHashMap.newKeySet();

    public boolean add(String deviceId) {
        return cache.add(deviceId);
    }
    
    public boolean addAll(Collection<? extends String> c) {
        return cache.addAll(c);
    }
    
    public boolean delete(String deviceId) {
        return cache.remove(deviceId);
    }
    
    public boolean contains(String deviceId) {
        return cache.contains(deviceId);
    }
}
