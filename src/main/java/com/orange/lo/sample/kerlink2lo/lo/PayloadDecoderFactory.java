package com.orange.lo.sample.kerlink2lo.lo;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class PayloadDecoderFactory {
    @Bean
    public PayloadDecoder payloadDecoder(LoProperties properties) {
        String decoderName = properties.getMessageDecoder();
        return payloadDecoder(decoderName);
    }

    public static PayloadDecoder payloadDecoder(String decoderName) {
        String notNullDecoderName = decoderName != null ? decoderName : "";
        switch (notNullDecoderName) {
            case "BASE64":
                return PayloadDecoder.BASE64;
            case "HEXA":
                return PayloadDecoder.HEXA;
            default:
                return PayloadDecoder.IDENTITY;
        }
    }
}
