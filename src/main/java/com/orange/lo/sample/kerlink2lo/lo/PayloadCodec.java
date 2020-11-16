package com.orange.lo.sample.kerlink2lo.lo;

import java.util.Base64;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class PayloadCodec {

    private PayloadCodec() {
    }

    public static String encodeToBase64(String raw) {
        return Base64.getEncoder().encodeToString(raw.getBytes());
    }

    public static String decodeFromBase64(String encoded) {
        return new String(Base64.getDecoder().decode(encoded));
    }

    public static String encodeToHex(String raw) {
        return Hex.encodeHexString(raw.getBytes());
    }

    public static String decodeFromHex(String encoded) throws DecoderException {
        return new String(Hex.decodeHex(encoded));
    }
}
