package com.orange.lo.sample.kerlink2lo.lo;

import com.orange.lo.sample.kerlink2lo.kerlink.model.DataDownEventDto;
import com.orange.lo.sample.kerlink2lo.kerlink.model.DataUpDto;

public interface ExternalConnectorService {
    void sendMessage(DataUpDto dataUpDto, String kerlinkAccountName);

    void sendCommandResponse(DataDownEventDto dataDownEventDto);

    void createDevice(String kerlinkDeviceId, String kerlinkAccountName);

    void deleteDevice(String loDeviceId);
}
