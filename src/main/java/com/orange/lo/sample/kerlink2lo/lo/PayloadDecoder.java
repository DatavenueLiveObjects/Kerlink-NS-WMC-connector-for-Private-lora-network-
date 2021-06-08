package com.orange.lo.sample.kerlink2lo.lo;

import com.orange.lo.sample.kerlink2lo.exceptions.EncodingTypeException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.util.Base64;
import java.util.Optional;

public enum PayloadDecoder {
    IDENTITY {
        @Override
        public String decode(String payload) {
            return payload;
        }
        @Override
        public Optional<String> metadataName() {
            return Optional.empty();
        }
    },
    BASE64 {
        @Override
        public String decode(String payload) {
            byte[] decoded = Base64.getDecoder().decode(payload);
            return new String(decoded);
        }
    },
    HEXA {
        @Override
        public String decode(String payload) {
            try {
                byte[] decodeHex = Hex.decodeHex(payload);
                return new String(decodeHex);
            } catch (DecoderException e) {
                throw new EncodingTypeException(e);
            }
        }
    };

    public abstract String decode(String payload);
    public Optional<String> metadataName() {
        return Optional.of(name());
    }
}
