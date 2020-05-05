package com.orange.lo.sample.kerlink2lo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.orange.lo.sample.kerlink2lo.kerlink.api.KerlinkApi;
import com.orange.lo.sample.kerlink2lo.kerlink.api.model.EndDeviceDto;
import com.orange.lo.sample.kerlink2lo.lo.ExternalConnectorService;
import com.orange.lo.sample.kerlink2lo.lo.LoDevice;
import com.orange.lo.sample.kerlink2lo.lo.LoDeviceCache;
import com.orange.lo.sample.kerlink2lo.lo.LoDeviceProvider;
import com.orange.lo.sample.kerlink2lo.lo.LoProperties;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class IotDeviceManagementTest {

    private final String LO_DEVICE_PREFIX = "urn:lo:nsid:x-connector:";
    
    @Mock
    private KerlinkApi kerlinkApi;
    
    @Mock
    private LoDeviceProvider loDeviceProvider;
    
    @Mock
    private ExternalConnectorService externalConnectorService;
    
    private LoDeviceCache loDeviceCache;
    private LoProperties loProperties;
    private IotDeviceManagement iotDeviceManagement;
    
    @Before
    public void setUp() {
        loDeviceCache = new LoDeviceCache();
        loProperties = new LoProperties();
        iotDeviceManagement = new IotDeviceManagement(kerlinkApi, loDeviceProvider, externalConnectorService, loProperties, loDeviceCache);
    }
    
    @Test
    public void shouldDoNothingWhenDevicesAreEqual() throws InterruptedException {
        // given
        loProperties.setDevicePrefix(LO_DEVICE_PREFIX);
        List<EndDeviceDto> kerlinkDevicesList = getKerlinkDevicesList(3);
        List<LoDevice> loDevicesList = getLoDevicecsList(3);
        
        when(kerlinkApi.getEndDevices()).thenReturn(kerlinkDevicesList);
        when(loDeviceProvider.getDevices()).thenReturn(loDevicesList);
        
        // when
        iotDeviceManagement.synchronizeDevices();        
        
        // then
        verify(externalConnectorService, times(0)).createDevice(any());
        verify(externalConnectorService, times(0)).deleteDevice(any());

    }
    
    @Test
    public void shouldCreateNewDevices() throws InterruptedException {
        // given
        CountDownLatch countDownLatch = new CountDownLatch(2);

        loProperties.setDevicePrefix(LO_DEVICE_PREFIX);
        loProperties.setSynchronizationThreadPoolSize(10); 
        
        List<EndDeviceDto> kerlinkDevicesList = getKerlinkDevicesList(6);
        List<LoDevice> loDevicesList = getLoDevicecsList(4);
        
        when(kerlinkApi.getEndDevices()).thenReturn(kerlinkDevicesList);
        when(loDeviceProvider.getDevices()).thenReturn(loDevicesList);
        
        doAnswer(invocation -> {
            countDownLatch.countDown();
            return null;
        }).when(externalConnectorService).createDevice(any());
        
        // when
        iotDeviceManagement.synchronizeDevices();
        countDownLatch.await(); 
        
        // then
        verify(externalConnectorService, times(2)).createDevice(any());
        verify(externalConnectorService, times(0)).deleteDevice(any());
    }
    
    @Test
    public void shouldDeleteOldDevices() throws InterruptedException {
        // given
        CountDownLatch countDownLatch = new CountDownLatch(3);
        
        loProperties.setDevicePrefix(LO_DEVICE_PREFIX);
        loProperties.setSynchronizationThreadPoolSize(10); 
        
        
        List<EndDeviceDto> kerlinkDevicesList = getKerlinkDevicesList(5);
        List<LoDevice> loDevicesList = getLoDevicecsList(8);
        
        when(kerlinkApi.getEndDevices()).thenReturn(kerlinkDevicesList);
        when(loDeviceProvider.getDevices()).thenReturn(loDevicesList);

        doAnswer(invocation -> {
            countDownLatch.countDown();
            return null;
        }).when(externalConnectorService).deleteDevice(any());
        
        // when
        iotDeviceManagement.synchronizeDevices();        
        countDownLatch.await();
        
        // then
        verify(externalConnectorService, times(0)).createDevice(any());
        verify(externalConnectorService, times(3)).deleteDevice(any());
    }

    private List<LoDevice> getLoDevicecsList(int amount) {
        return IntStream.rangeClosed(1, amount).mapToObj(i -> {
            LoDevice loDevice = new LoDevice();
            loDevice.setId(LO_DEVICE_PREFIX + i);
            return loDevice;
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