/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo.lo;

import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkApi;
import com.orange.lo.sample.kerlink2lo.kerlink.model.DataDownDto;
import com.orange.lo.sample.kerlink2lo.kerlink.model.EndDeviceDto;
import com.orange.lo.sdk.externalconnector.DataManagementExtConnectorCommandCallback;
import com.orange.lo.sdk.externalconnector.model.CommandRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Optional;

public class MessageListener implements DataManagementExtConnectorCommandCallback {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String PAYLOAD_KEY = "payload";
    private static final String CONTENT_TYPE_KEY = "contentType";
    private static final String DEFAULT_CONTENT_TYPE = "TEXT";
    private static final String F_PORT_KEY = "fPort";
    private static final String DEFAULT_F_PORT = "10";
    private static final Boolean DEFAULT_CONFIRMED = false;

    private final CommandMapper commandMapper;
    private final Map<String, KerlinkApi> kerlinkApiMap;
    private final LoDeviceCache deviceCache;

    public MessageListener(CommandMapper commandMapper,
                           Map<String, KerlinkApi> kerlinkApiMap,
                           LoDeviceCache deviceCache) {
        this.commandMapper = commandMapper;
        this.kerlinkApiMap = kerlinkApiMap;
        this.deviceCache = deviceCache;
    }

    @Override
    public Object onCommandRequest(CommandRequest commandRequest) {
        try {
            LOG.info("Got CommandRequest {}", commandRequest);
            DataDownDto dataDownDto = prepareDataDown(commandRequest);
            String group = deviceCache.getGroup(dataDownDto.getEndDevice().getDevEui());
            Optional<String> commandId = kerlinkApiMap.get(group).sendCommand(dataDownDto);
            if (commandId.isPresent()) {
                commandMapper.put(commandId.get(), commandRequest.getId(), commandRequest.getNodeId());
                LOG.info("Put to commandMapper: kerlinkID = {}, loId = {}, nodeId = {}", commandId, commandRequest.getId(), commandRequest.getNodeId());
            } else {
                LOG.info("Command not sent to Kerlink");
            }
        } catch (Exception e) {
            LOG.error("Error while trying to send command to Kerlink platform", e);
        } finally {
            // Returning null, because non-null response would indicate a finished command
            return null;
        }
    }

    private DataDownDto prepareDataDown(CommandRequest commandRequest) {
        int fPort = Integer.parseInt(commandRequest.getValue().getArg().getOrDefault(F_PORT_KEY, DEFAULT_F_PORT));
        String contentType = commandRequest.getValue().getArg().getOrDefault(CONTENT_TYPE_KEY, DEFAULT_CONTENT_TYPE);
        String payload = commandRequest.getValue().getArg().get(PAYLOAD_KEY);

        if (payload == null) {
            throw new IllegalArgumentException("Payload cannot be null");
        }

        DataDownDto dataDownDto = new DataDownDto();
        dataDownDto.setConfirmed(DEFAULT_CONFIRMED);
        dataDownDto.setContentType(contentType);
        dataDownDto.setfPort(fPort);
        dataDownDto.setPayload(payload);

        EndDeviceDto endDeviceDto = new EndDeviceDto();
        endDeviceDto.setDevEui(commandRequest.getNodeId());

        dataDownDto.setEndDevice(endDeviceDto);
        return dataDownDto;
    }
}