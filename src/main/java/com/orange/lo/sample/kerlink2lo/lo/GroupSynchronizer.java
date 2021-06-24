/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo.lo;

import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkProperties;
import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkPropertiesList;
import com.orange.lo.sdk.rest.devicemanagement.DeviceManagement;
import com.orange.lo.sdk.rest.devicemanagement.GetGroupsFilter;
import com.orange.lo.sdk.rest.model.Group;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupSynchronizer {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final Set<String> kerlinkAccountNames;
    private final DeviceManagement deviceManagement;
    private final GroupCache groupCache;
    private final Integer pageSize;
    private final RetryPolicy<Group> groupRetryPolicy;
    private final RetryPolicy<List<Group>> groupListRetryPolicy;

    public GroupSynchronizer(LoProperties loProperties,
                             KerlinkPropertiesList kerlinkPropertiesList,
                             DeviceManagement deviceManagement,
                             GroupCache groupCache) {
        this.kerlinkAccountNames = kerlinkPropertiesList.getKerlinkList()
                .stream()
                .map(KerlinkProperties::getKerlinkAccountName)
                .collect(Collectors.toSet());
        this.deviceManagement = deviceManagement;

        pageSize = loProperties.getPageSize();
        this.groupCache = groupCache;
        groupRetryPolicy = new RetryPolicy<>();
        groupListRetryPolicy = new RetryPolicy<>();
    }

    @PostConstruct
    public void postConstruct() {
        LOG.info("Managing group of devices");
        try {
            synchronizeGroups();
        } catch (HttpClientErrorException e) {
            LOG.error("Cannot create group \n {}", e.getResponseBodyAsString());
            System.exit(1);
        } catch (Exception e) {
            LOG.error("Unexpected error while managing group {}", e.getMessage());
            System.exit(1);
        }
    }

    private void synchronizeGroups() {
        LOG.debug("Trying to get existing groups");

        Map<String, Group> allGroups = retrieveGroups();
        this.groupCache.putAll(allGroups);

        this.kerlinkAccountNames.forEach(accountName -> {
            if (!allGroups.containsKey(accountName)) {
                LOG.debug("Group {} not found, trying to create new group", accountName);
                Group group = Failsafe.with(this.groupRetryPolicy)
                        .get(() -> this.deviceManagement
                                .getGroups()
                                .createGroup(accountName)
                        );
                this.groupCache.put(accountName, group);
                LOG.debug("Group {} created", accountName);
            }
        });
        LOG.debug("kerlinkAccountNames: {}", this.kerlinkAccountNames);
        LOG.debug("existing groups: {}", this.groupCache);
    }

    public Map<String, Group> retrieveGroups() {
        Map<String, Group> allGroups = new HashMap<>();
        for (int offset = 0; ; offset++) {
            GetGroupsFilter getGroupsFilter = new GetGroupsFilter()
                    .withLimit(this.pageSize)
                    .withOffset(offset * this.pageSize);
            List<Group> groupSubset = Failsafe.with(this.groupListRetryPolicy)
                    .get(() -> this.deviceManagement.getGroups().getGroups(getGroupsFilter));
            groupSubset.forEach(g -> allGroups.put(g.getPathNode(), g));

            if (groupSubset.size() < this.pageSize) {
                break;
            }
        }

        return allGroups;
    }

}