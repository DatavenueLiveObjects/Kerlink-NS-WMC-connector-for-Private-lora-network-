package com.orange.lo.sample.kerlink2lo.lo;

import com.orange.lo.sdk.rest.model.Group;

import java.util.HashMap;
import java.util.Map;

public class GroupCache {
    private final Map<String, Group> backingMap = new HashMap<>();

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
