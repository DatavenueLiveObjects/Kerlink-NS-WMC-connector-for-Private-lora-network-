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

public class LoExtConnectorCommandCallback implements DataManagementExtConnectorCommandCallback {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private Map<String, KerlinkApi> kerlinkApiMap;
    private CommandMapper commandMapper;
    private LoDeviceCache deviceCache;

    private static final String COMMAND_RESPONSE_TOPIC = "connector/v1/responses/command";
    private static final String COMMAND_REQUEST_TOPIC = "connector/v1/requests/command";

    private static final String F_PORT_KEY = "fPort";
    private static final String F_PORT_DEFAULT = "1";

    public LoExtConnectorCommandCallback(Map<String, KerlinkApi> kerlinkApiMap, CommandMapper commandMapper, LoDeviceCache deviceCache) {
        this.kerlinkApiMap = kerlinkApiMap;
        this.commandMapper = commandMapper;
        this.deviceCache = deviceCache;
    }

    @Override
    public Object onCommandRequest(CommandRequest commandRequest) {
        LOG.trace("New Command received - {}", commandRequest);

        DataDownDto dataDownDto = prepareDataDown(commandRequest);

        Optional<String> commandId = kerlinkApiMap.get(deviceCache.getGroup(dataDownDto.getEndDevice().getDevEui())).sendCommand(dataDownDto);
        if (commandId.isPresent()) {
            commandMapper.put(commandId.get(), commandRequest.getId(), commandRequest.getNodeId());
            LOG.trace("Put to commandMapper: kerlinkID = {}, loId = {}, nodeId = {}", commandId, commandRequest.getId(), commandRequest.getNodeId());
        }

        return null;
    }

    private static final String CONTENT_TYPE_TEXT = "TEXT";


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
