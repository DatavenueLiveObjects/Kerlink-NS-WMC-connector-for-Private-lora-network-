package com.orange.lo.sample.kerlink2lo.lo;

import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkProperties;
import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkPropertiesList;
import com.orange.lo.sdk.rest.RestTemplateFactory;
import com.orange.lo.sdk.rest.devicemanagement.DeviceManagement;
import com.orange.lo.sdk.rest.devicemanagement.GetGroupsFilter;
import com.orange.lo.sdk.rest.devicemanagement.Groups;
import com.orange.lo.sdk.rest.model.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class LoDeviceProviderTest {

    private DeviceManagement deviceManagement;
    private LoDeviceProvider loDeviceProvider;

    @BeforeEach
    void setUp() {
        LoProperties loProperties = LoPropertiesTestData.loPropertiesTestData();
        KerlinkProperties kerlinkProperties = new KerlinkProperties();
        List<KerlinkProperties> kerlinkList = Collections.singletonList(kerlinkProperties);
        KerlinkPropertiesList kerlinkPropertiesList = new KerlinkPropertiesList(kerlinkList);
        deviceManagement = Mockito.mock(DeviceManagement.class);
        GroupCache groupCache = new GroupCache();
        LoDeviceCache deviceCache = Mockito.mock(LoDeviceCache.class);
        loDeviceProvider = new LoDeviceProvider(loProperties, deviceManagement, deviceCache, groupCache, kerlinkPropertiesList);
    }

    @Test
    void groupsAreRetrievedCorrectly() {
        RestTemplateFactory restTemplateFactory = Mockito.mock(RestTemplateFactory.class);
        when(restTemplateFactory.getRestTemplate()).thenReturn(new RestTemplate());
        Groups groups = Mockito.mock(Groups.class);
        Group oneGroup = new Group();
        List<Group> groupList = Collections.singletonList(oneGroup);
        when(groups.getGroups(Mockito.any(GetGroupsFilter.class)))
                .thenReturn(groupList);
        when(deviceManagement.getGroups()).thenReturn(groups);

        Map<String, Group> retrievedGroups = loDeviceProvider.retrieveGroups();

        assertEquals(retrievedGroups.values().stream().findAny().get(), oneGroup);
    }
}
