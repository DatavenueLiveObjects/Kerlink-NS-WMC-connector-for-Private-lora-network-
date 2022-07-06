package com.orange.lo.sample.kerlink2lo;

import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkApi;
import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkProperties;
import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkPropertiesList;
import com.orange.lo.sample.kerlink2lo.kerlink.model.EndDeviceDto;
import com.orange.lo.sample.kerlink2lo.lo.LoApiExternalConnectorService;
import com.orange.lo.sample.kerlink2lo.lo.LoDeviceCache;
import com.orange.lo.sample.kerlink2lo.lo.LoDeviceProvider;
import com.orange.lo.sample.kerlink2lo.lo.LoProperties;
import com.orange.lo.sdk.rest.model.Device;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IotDeviceManagementTest {

    public static final String KER_ACCOUNT = "kerAccount";
    private final String LO_DEVICE_PREFIX = "urn:lo:nsid:x-connector:";
    private static final String GROUP_NAME = KER_ACCOUNT;

    @Mock
    private KerlinkApi kerlinkApi;

    @Mock
    private LoDeviceProvider loDeviceProvider;

    @Mock
    private LoApiExternalConnectorService externalConnectorService;

    private LoDeviceCache loDeviceCache;
    @Autowired
    private LoProperties loProperties;
    private IotDeviceManagement iotDeviceManagement;
    private Map<String, KerlinkApi> kerlinkApiMap;
    private KerlinkPropertiesList kerlinkPropertiesList;

    @BeforeEach
    public void setUp() {
        loDeviceCache = new LoDeviceCache();
        kerlinkApiMap = new HashMap<>();
        kerlinkApiMap.put(KER_ACCOUNT, kerlinkApi);

        KerlinkProperties kerlinkProperties = new KerlinkProperties();
        kerlinkProperties.setKerlinkAccountName(KER_ACCOUNT);
        kerlinkPropertiesList = new KerlinkPropertiesList(Lists.list(kerlinkProperties));
        iotDeviceManagement = new IotDeviceManagement(kerlinkApiMap, loDeviceProvider, externalConnectorService, kerlinkPropertiesList, loDeviceCache);
    }

    @Test
    public void shouldDoNothingWhenDevicesAreEqual() throws InterruptedException {
        // given
        List<EndDeviceDto> kerlinkDevicesList = getKerlinkDevicesList(3);
        List<Device> loDevicesList = getLoDevicesList(3);

        when(kerlinkApi.getEndDevices()).thenReturn(kerlinkDevicesList);
        when(loDeviceProvider.getDevices(GROUP_NAME)).thenReturn(loDevicesList);

        // when
        iotDeviceManagement.synchronizeDevices();

        // then
        verify(externalConnectorService, times(0)).createDevice(any(), eq(GROUP_NAME));
        verify(externalConnectorService, times(0)).deleteDevice(any());

    }

    @Test
    public void shouldCreateNewDevices() throws InterruptedException {
        List<EndDeviceDto> kerlinkDevicesList = getKerlinkDevicesList(6);
        List<Device> loDevicesList = getLoDevicesList(4);

        when(kerlinkApi.getEndDevices()).thenReturn(kerlinkDevicesList);
        when(loDeviceProvider.getDevices(GROUP_NAME)).thenReturn(loDevicesList);

        CountDownLatch countDownLatch = new CountDownLatch(2);

        doAnswer(invocation -> {
            countDownLatch.countDown();
            return null;
        }).when(externalConnectorService).createDevice(any(), eq(GROUP_NAME));

        // when
        iotDeviceManagement.synchronizeDevices();
        countDownLatch.await(10, TimeUnit.SECONDS);

        // then
        verify(externalConnectorService, times(2)).createDevice(any(), eq(GROUP_NAME));
        verify(externalConnectorService, times(0)).deleteDevice(any());
    }

    @Test
    public void shouldDeleteOldDevices() throws InterruptedException {
        List<EndDeviceDto> kerlinkDevicesList = getKerlinkDevicesList(5);
        List<Device> loDevicesList = getLoDevicesList(8);

        when(kerlinkApi.getEndDevices()).thenReturn(kerlinkDevicesList);
        when(loDeviceProvider.getDevices(GROUP_NAME)).thenReturn(loDevicesList);

        CountDownLatch countDownLatch = new CountDownLatch(3);

        doAnswer(invocation -> {
            countDownLatch.countDown();
            return null;
        }).when(externalConnectorService).deleteDevice(any());

        // when
        iotDeviceManagement.synchronizeDevices();
        countDownLatch.await(10, TimeUnit.SECONDS);

        // then
        verify(externalConnectorService, times(0)).createDevice(any(), eq(GROUP_NAME));
        verify(externalConnectorService, times(3)).deleteDevice(any());
    }

    private List<Device> getLoDevicesList(int amount) {
        return IntStream.rangeClosed(1, amount).mapToObj(i -> {
            return new Device().withId(LO_DEVICE_PREFIX + i);
        }).collect(Collectors.toList());
    }

    private List<EndDeviceDto> getKerlinkDevicesList(int amount) {
        return IntStream.rangeClosed(1, amount).mapToObj(i -> {
            EndDeviceDto endDeviceDto = new EndDeviceDto();
            endDeviceDto.setDevEui(String.valueOf(i));
            return endDeviceDto;
        }).collect(Collectors.toList());
    }
}
