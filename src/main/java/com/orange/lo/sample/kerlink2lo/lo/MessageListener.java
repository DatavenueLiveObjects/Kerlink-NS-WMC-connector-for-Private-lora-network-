package com.orange.lo.sample.kerlink2lo.lo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.lo.sample.kerlink2lo.kerlink.KerlinkApi;
import com.orange.lo.sample.kerlink2lo.kerlink.model.DataDownDto;
import com.orange.lo.sample.kerlink2lo.kerlink.model.EndDeviceDto;
import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.externalconnector.model.CommandRequest;
import com.orange.lo.sdk.fifomqtt.DataManagementFifoCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Optional;

public class MessageListener implements DataManagementFifoCallback {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String CONTENT_TYPE_TEXT = "TEXT";
    private static final String F_PORT_KEY = "fPort";
    private static final String F_PORT_DEFAULT = "1";

    private final CommandMapper commandMapper;
    private final Map<String, KerlinkApi> kerlinkApiMap;
    private final LoDeviceCache deviceCache;
    private final ObjectMapper objectMapper;

    public MessageListener(CommandMapper commandMapper, Map<String, KerlinkApi> kerlinkApiMap, LoDeviceCache deviceCache, ObjectMapper objectMapper) {
        this.commandMapper = commandMapper;
        this.kerlinkApiMap = kerlinkApiMap;
        this.deviceCache = deviceCache;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(String message) {
        LOG.trace("New Command received  command: {}", message);
        Optional<CommandRequest> commandRequestOptional = parseCommandRequest(message);
        if (!commandRequestOptional.isPresent()) {
            LOG.error("Message couldn't be deserialized to CommandRequest: {}", message);
            return;
        }

        CommandRequest commandRequest = commandRequestOptional.get();
        DataDownDto dataDownDto = prepareDataDown(commandRequest);

        Optional<String> commandId = kerlinkApiMap.get(deviceCache.getGroup(dataDownDto.getEndDevice().getDevEui())).sendCommand(dataDownDto);
        if (commandId.isPresent()) {
            commandMapper.put(commandId.get(), commandRequest.getId(), commandRequest.getNodeId());
            LOG.trace("Put to commandMapper: kerlinkID = {}, loId = {}, nodeId = {}", commandId, commandRequest.getId(), commandRequest.getNodeId());
        }
    }

    private Optional<CommandRequest> parseCommandRequest(String message) {
        try {
            return Optional.of(objectMapper.readValue(message, CommandRequest.class));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    private DataDownDto prepareDataDown(CommandRequest commandRequest) {
        DataDownDto dataDownDto = new DataDownDto();
        dataDownDto.setConfirmed(false);
        dataDownDto.setContentType(CONTENT_TYPE_TEXT);

        String fPort = commandRequest.getValue().getArg().getOrDefault(F_PORT_KEY, F_PORT_DEFAULT);
        dataDownDto.setfPort(Integer.parseInt(fPort));

        EndDeviceDto endDeviceDto = new EndDeviceDto();
        endDeviceDto.setDevEui(commandRequest.getNodeId());

        dataDownDto.setEndDevice(endDeviceDto);
        dataDownDto.setPayload(commandRequest.getValue().getReq());
        return dataDownDto;
    }
}

