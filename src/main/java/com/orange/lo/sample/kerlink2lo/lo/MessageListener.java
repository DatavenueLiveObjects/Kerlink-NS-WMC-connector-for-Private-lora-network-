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

    private static final String CONTENT_TYPE_TEXT = "TEXT";
    private static final String F_PORT_KEY = "fPort";
    private static final String F_PORT_DEFAULT = "1";

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
        // If this throws, the whole thing seems to stop responding to commands
        // TODO: Exception handling
        LOG.trace("Got CommandRequest {}", commandRequest);
        DataDownDto dataDownDto = prepareDataDown(commandRequest);

        String group = deviceCache.getGroup(dataDownDto.getEndDevice().getDevEui());
        Optional<String> commandId = kerlinkApiMap.get(group)
                .sendCommand(dataDownDto);
        if (commandId.isPresent()) {
            commandMapper.put(commandId.get(), commandRequest.getId(), commandRequest.getNodeId());
            LOG.trace("Put to commandMapper: kerlinkID = {}, loId = {}, nodeId = {}", commandId, commandRequest.getId(), commandRequest.getNodeId());
        }

        // Returning null, because non-null response would indicate a finished command
        return null;
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
