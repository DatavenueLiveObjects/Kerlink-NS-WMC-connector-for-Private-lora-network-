package com.orange.lo.sample.kerlink2lo.lo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class LoDeviceProviderTest {

    LoProperties loProperties;

    @Mock
    RestTemplate restTemplate;

    @Mock
    HttpHeaders httpHeaders;

    @Before
    public void setUp() {
        loProperties = new LoProperties();
        loProperties.setApiUrl("localhost");
    }

    @Test
    public void shouldGetNoDevices() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(LoDevice[].class)))
                .thenReturn(getResponse(0, 0, 0, 0));
        int count = new LoDeviceProvider(loProperties, httpHeaders, restTemplate).getDevices().size();
        Assert.assertEquals(0, count);
    }

    @Test
    public void shouldGetNoDevicesWitLimitZero() {
        int limit = 0;
        int total = 1;
        loProperties.setPageSize(limit);
        when(restTemplate.exchange(eq(getUrl(limit, 0)), eq(HttpMethod.GET),
                any(), eq(LoDevice[].class))).thenReturn(
                getResponse(limit, total, 1, 0));
        int count = new LoDeviceProvider(loProperties, httpHeaders, restTemplate).getDevices().size();
        Assert.assertEquals(limit, count);
    }

    @Test
    public void shouldGetDevicesWithLimitEqualsTotalCount() {
        int limit = 2;
        loProperties.setPageSize(limit);
        when(restTemplate.exchange(eq(getUrl(limit, 0)), eq(HttpMethod.GET),
                any(), eq(LoDevice[].class))).thenReturn(
                getResponse(limit, limit, 1, 0));
        int count = new LoDeviceProvider(loProperties, httpHeaders, restTemplate).getDevices().size();
        Assert.assertEquals(limit, count);
    }

    @Test
    public void shouldGetDevicesWithLimitGreaterThanTotalCount() {
        int limit = 3;
        int total = 2;
        loProperties.setPageSize(limit);
        when(restTemplate.exchange(eq(getUrl(limit, 0)), eq(HttpMethod.GET),
                any(), eq(LoDevice[].class))).thenReturn(
                getResponse(total, total, 1, 0));
        int count = new LoDeviceProvider(loProperties, httpHeaders, restTemplate).getDevices().size();
        Assert.assertEquals(total, count);
    }

    @Test
    public void shouldGetDevicesWithLimitBeingAliquotOfTotalCount() {
        int limit = 2;
        int total = 4;
        loProperties.setPageSize(limit);
        when(restTemplate.exchange(eq(getUrl(limit, 0)), eq(HttpMethod.GET),
                any(), eq(LoDevice[].class))).thenReturn(
                        getResponse(limit, total, 1, 0));
        when(restTemplate.exchange(eq(getUrl(limit, 2)), eq(HttpMethod.GET),
                any(), eq(LoDevice[].class))).thenReturn(
                        getResponse(limit, total, 1, 0));
        int count = new LoDeviceProvider(loProperties, httpHeaders, restTemplate).getDevices().size();
        Assert.assertEquals(total, count);
    }

    @Test
    public void shouldGetDevicesWithLimitNotBeingAliquotOfTotalCount() {
        int limit = 2;
        int total = 3;
        loProperties.setPageSize(limit);
        when(restTemplate.exchange(eq(getUrl(limit, 0)), eq(HttpMethod.GET),
                any(), eq(LoDevice[].class))).thenReturn(
                getResponse(limit, total, 1, 0));
        when(restTemplate.exchange(eq(getUrl(limit, 2)), eq(HttpMethod.GET),
                any(), eq(LoDevice[].class))).thenReturn(
                getResponse(total - limit, total, 1, 0));
        int count = new LoDeviceProvider(loProperties, httpHeaders, restTemplate).getDevices().size();
        Assert.assertEquals(total, count);
    }

    @Test
    public void shouldGetDevicesOfQuantityGreaterThanTotalCount() {
        int limit = 3;
        int total1 = 4;
        int total2 = 2;
        loProperties.setPageSize(limit);
        when(restTemplate.exchange(eq(getUrl(limit, 0)), eq(HttpMethod.GET),
                any(), eq(LoDevice[].class))).thenReturn(
                getResponse(limit, total1, 1, 0));
        when(restTemplate.exchange(eq(getUrl(limit, 3)), eq(HttpMethod.GET),
                any(), eq(LoDevice[].class))).thenReturn(
                getResponse(0, total2, 1, 0));
        int count = new LoDeviceProvider(loProperties, httpHeaders, restTemplate).getDevices().size();
        Assert.assertEquals(limit, count);
    }

    @Test
    public void shouldGetDevicesAfterSleep() {
        int limit = 2;
        int total = 3;
        loProperties.setPageSize(limit);
        when(restTemplate.exchange(eq(getUrl(limit, 0)), eq(HttpMethod.GET),
                any(), eq(LoDevice[].class))).thenReturn(
                getResponse(limit, total, 0, System.currentTimeMillis() + 10000));
        when(restTemplate.exchange(eq(getUrl(limit, 2)), eq(HttpMethod.GET),
                any(), eq(LoDevice[].class))).thenReturn(
                getResponse(limit, total, 1, 0));
        long start = System.currentTimeMillis();
        new LoDeviceProvider(loProperties, httpHeaders, restTemplate).getDevices();
        long stop = System.currentTimeMillis();
        Assert.assertTrue(stop - start >= 10000);
    }

    @Test
    public void shouldAddDevice() {
        String deviceId = "test";
        new LoDeviceProvider(loProperties, httpHeaders, restTemplate).addDevice(deviceId);
        verify(restTemplate, times(1))
                .exchange(eq(loProperties.getApiUrl() + "/v1/deviceMgt/devices"), eq(HttpMethod.POST),
                        any(), eq(Void.class));
    }

    @Test
    public void shouldDeleteDevice() {
        String deviceId = "test";
        new LoDeviceProvider(loProperties, httpHeaders, restTemplate).deleteDevice(deviceId);
        verify(restTemplate, times(1))
                .exchange(eq(loProperties.getApiUrl() + "/v1/deviceMgt/devices/" + deviceId), eq(HttpMethod.DELETE),
                        any(), eq(Void.class));
    }

    private String getUrl(int limit, int offset) {
        return "localhost/v1/deviceMgt/devices?limit=" + limit +
                "&offset=" + offset + "&groupId=null&fields=id,name,group";
    }

    private ResponseEntity<LoDevice[]> getResponse(int devicesCount, int xTotalCount, int xRateLimitRemaining, long xRateLimitReset) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("X-Total-Count", String.valueOf(xTotalCount));
        headers.add("X-Ratelimit-Remaining", String.valueOf(xRateLimitRemaining));
        headers.add("X-Ratelimit-Reset", String.valueOf(xRateLimitReset));
        return new ResponseEntity<>(returnDevices(devicesCount), headers, HttpStatus.OK);
    }

    private LoDevice[] returnDevices(int count) {
        LoDevice[] devices = new LoDevice[count];
        for (int i = 0;  i < count; i++)
            devices[i] = new LoDevice();
        return devices;
    }
}
