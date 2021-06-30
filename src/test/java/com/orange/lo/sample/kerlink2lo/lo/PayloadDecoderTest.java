/*
 * Copyright (c) Orange. All Rights Reserved.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.kerlink2lo.lo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PayloadDecoderTest {

    @Test
    void decode_identity() {
        String encoded = "encoded \"\' \\ \t string";
        String expected = "encoded \"\' \\ \t string";

        String decoded = PayloadDecoder.IDENTITY.decode(encoded);

        assertEquals(expected, decoded);
    }

    @Test
    void decode_base64() {
        String encoded = "ZW5jb2RlZCAiJyBcIAkgc3RyaW5n";
        String expected = "encoded \"\' \\ \t string";

        String decoded = PayloadDecoder.BASE64.decode(encoded);

        assertEquals(expected, decoded);
    }

    @Test
    void decode_hexa() {
        String encoded = "656e636f646564202227205c200920737472696e67";
        String expected = "encoded \"\' \\ \t string";

        String decoded = PayloadDecoder.HEXA.decode(encoded);

        assertEquals(expected, decoded);
    }
}
