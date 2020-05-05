package com.orange.lo.sample.kerlink2lo.lo;

import java.util.Base64;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class PayloadCodec {

    public static String encodeToBase64(String raw) {
        String encoded = Base64.getEncoder().encodeToString(raw.getBytes());
        return encoded;
    }

    public static String decodeFromBase64(String encoded) {
        String decoded = new String(Base64.getDecoder().decode(encoded));
        return decoded;
    }

    public static String encodeToHex(String raw) {
        String encoded = Hex.encodeHexString(raw.getBytes());
        return encoded;
    }

    public static String decodeFromHex(String encoded) throws DecoderException {
        String decoded = new String(Hex.decodeHex(encoded));
        return decoded;
    }
}
