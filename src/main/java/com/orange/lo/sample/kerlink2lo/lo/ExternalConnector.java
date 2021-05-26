/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.kerlink2lo.lo;

import com.orange.lo.sample.kerlink2lo.kerlink.model.DataDownEventDto;
import com.orange.lo.sample.kerlink2lo.lo.CommandMapper.LoCommand;
import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.externalconnector.model.CommandResponse;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

@Component
public class ExternalConnector {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private CommandMapper commandMapper;
    private LOApiClient loApiClient;

    public ExternalConnector(CommandMapper commandMapper, LOApiClient loApiClient) {
        this.commandMapper = commandMapper;
        this.loApiClient = loApiClient;
    }

    public void sendCommandResponse(DataDownEventDto dataDownEventDto) {
        Optional<LoCommand> loCommand = commandMapper.get(dataDownEventDto.getDataDownId());

        if (loCommand.isPresent()) {
            LOG.debug("Sending command response for device {}", loCommand.get().getNodeId());
            CommandResponse commandResponse = new CommandResponse(loCommand.get().getId(), loCommand.get().getNodeId());
            loApiClient.getDataManagementExtConnector().sendCommandResponse(commandResponse);
        } else {
            if (LOG.isDebugEnabled()) {
                String cleanDtoString = StringEscapeUtils.escapeJava(dataDownEventDto.toString());
                LOG.debug("Received unknown command status from Kerlink: {}", cleanDtoString);
            }
        }
    }

}
