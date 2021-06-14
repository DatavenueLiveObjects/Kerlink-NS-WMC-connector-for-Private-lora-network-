/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo.lo;

import com.orange.lo.sample.kerlink2lo.kerlink.model.DataDownEventDto;
import com.orange.lo.sample.kerlink2lo.lo.CommandMapper.LoCommand;
import com.orange.lo.sdk.externalconnector.DataManagementExtConnector;
import com.orange.lo.sdk.externalconnector.model.CommandResponse;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

@Component
public class ExternalConnectorCommandResponseWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final CommandMapper commandMapper;
    private final DataManagementExtConnector dataManagementExtConnector;

    public ExternalConnectorCommandResponseWrapper(CommandMapper commandMapper, DataManagementExtConnector dataManagementExtConnector) {
        this.commandMapper = commandMapper;
        this.dataManagementExtConnector = dataManagementExtConnector;
    }

    public void sendCommandResponse(DataDownEventDto dataDownEventDto) {
        Optional<LoCommand> optionalLoCommand = commandMapper.get(dataDownEventDto.getDataDownId());

        if (optionalLoCommand.isPresent()) {
            LoCommand loCommand = optionalLoCommand.get();
            LOG.trace("Sending command response for device {}", loCommand.getNodeId());
            CommandResponse commandResponse = new CommandResponse(loCommand.getId(), loCommand.getNodeId());
            dataManagementExtConnector.sendCommandResponse(commandResponse);
        } else if (LOG.isDebugEnabled()) {
            String cleanDtoString = StringEscapeUtils.escapeJava(dataDownEventDto.toString());
            LOG.debug("Received unknown command status from Kerlink: {}", cleanDtoString);
        }
    }

}
