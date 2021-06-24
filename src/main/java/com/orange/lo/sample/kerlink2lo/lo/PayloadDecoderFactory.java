/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo.lo;

public class PayloadDecoderFactory {

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
