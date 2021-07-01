/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo.lo;

import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkApi;
import com.orange.lo.sdk.externalconnector.model.AcknowledgementMode;
import com.orange.lo.sdk.externalconnector.model.CommandRequest;
import com.orange.lo.sdk.externalconnector.model.CommandRequestValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MessageListenerTest {
    private static final String REQUEST_NODE_ID = "node";
    private static final String REQUEST_ID = "request_id";
    private static final String GROUP_ID = "group";
    private static final String COMMAND_ID = "commandId";
    public static final String COMMAND_REQUEST_VALUE_REQUEST = "do_stuff";
    private MessageListener messageListener;
    private CommandMapper commandMapper;
    private KerlinkApi kerlinkApi;

    @BeforeEach
    public void setUp() {
        commandMapper = new CommandMapper();
        kerlinkApi = Mockito.mock(KerlinkApi.class);
        Map<String, KerlinkApi> kerlinkApiMap = new HashMap<>();
        kerlinkApiMap.put(GROUP_ID, kerlinkApi);
        LoDeviceCache deviceCache = Mockito.mock(LoDeviceCache.class);
        when(deviceCache.getGroup(anyString())).thenReturn(GROUP_ID);
        messageListener = new MessageListener(commandMapper, kerlinkApiMap, deviceCache);

    }

    @Test
    void onCommandRequest_calls_send_command() {
        CommandRequest commandRequest = prepareCommandRequest();

        messageListener.onCommandRequest(commandRequest);

        verify(kerlinkApi, times(1)).sendCommand(any());
    }

    @Test
    void onCommandRequest_returns_null() {
        CommandRequest commandRequest = prepareCommandRequest();

        Object returnVal = messageListener.onCommandRequest(commandRequest);

        Assertions.assertNull(returnVal);
    }

    @Test
    void onCommandRequest_adds_command_id_to_mapper() {
        when(kerlinkApi.sendCommand(any())).thenReturn(Optional.of(COMMAND_ID));
        CommandRequest commandRequest = prepareCommandRequest();

        messageListener.onCommandRequest(commandRequest);

        Assertions.assertTrue(commandMapper.get(COMMAND_ID).isPresent());
    }

    private CommandRequest prepareCommandRequest() {
        CommandRequest commandRequest = new CommandRequest();
        commandRequest.setAckMode(AcknowledgementMode.APPLICATIVE);
        commandRequest.setNodeId(REQUEST_NODE_ID);
        commandRequest.setId(REQUEST_ID);

        CommandRequestValue commandRequestValue = new CommandRequestValue();
        commandRequestValue.setReq(COMMAND_REQUEST_VALUE_REQUEST);
        commandRequestValue.setArg(Collections.emptyMap());
        commandRequest.setValue(commandRequestValue);
        return commandRequest;
    }
}
