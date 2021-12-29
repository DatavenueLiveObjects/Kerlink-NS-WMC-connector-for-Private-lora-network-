package com.orange.lo.sample.kerlink2lo.lo;

import java.util.Base64;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import com.orange.lo.sample.kerlink2lo.exceptions.EncodingTypeException;
import com.orange.lo.sample.kerlink2lo.kerlink.model.DataUpDto;

@Service
public class PayloadEncoder {

	public void convert(DataUpDto dataUpDto) {
        switch (dataUpDto.getEncodingType()) {
            case "BASE64":
            	byte[] decoded = Base64.getDecoder().decode(dataUpDto.getPayload());
            	dataUpDto.setPayload(new String(decoded));
            	break;
            case "HEXA":
            	try {
                    byte[] decodeHex = Hex.decodeHex(dataUpDto.getPayload());
                    dataUpDto.setPayload(new String(decodeHex));
                } catch (DecoderException e) {
                    throw new EncodingTypeException(e);
                }
            	break;
            default:
            	break;
        }
	}
}
