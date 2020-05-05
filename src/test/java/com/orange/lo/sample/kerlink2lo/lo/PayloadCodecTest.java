package com.orange.lo.sample.kerlink2lo.lo;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;


public class PayloadCodecTest {

    public String getRandomString() {
        int leftLimit = 97;
        int rightLimit = 122;
        int targetStringLength = 10;
        Random random = new Random();
        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @Test
    public void shouldEncodeToBase64() {
        // given
        String input = "test input";
        String expected = "dGVzdCBpbnB1dA==";
        // when
        String coded = PayloadCodec.encodeToBase64(input);
        // then
        assertEquals(expected, coded);
    }

    @Test
    public void shouldDecodeFromBase64() {
        // given
        String input = "dGVzdCBpbnB1dA==";
        String expected = "test input";
        // when
        String decoded = PayloadCodec.decodeFromBase64(input);
        // then
        assertEquals(expected, decoded);
    }

    @Test
    public void shouldEncodeDecodeBase64() {
        // given
        String input = getRandomString();
        // when
        String coded = PayloadCodec.encodeToBase64(input);
        String decoded = PayloadCodec.decodeFromBase64(coded);
        // then
        assertEquals(input, decoded);
    }

    @Test
    public void shouldEncodeToHex() {
        // given
        String input = "test input";
        String expected = "7465737420696e707574";
        // when
        String coded = PayloadCodec.encodeToHex(input);
        // then
        assertEquals(expected, coded);
    }

    @Test public void shouldDecodeFromHex() throws DecoderException {
        // given
        String input = "7465737420696e707574";
        String expected = "test input";
        // when
        String decoded = PayloadCodec.decodeFromHex(input);
        assertEquals(expected, decoded);
    }

    @Test
    public void shouldEncodeDecodeHex() throws DecoderException {
        // given
        String input = getRandomString();
        // when
        String coded = PayloadCodec.encodeToHex(input);
        String decoded = PayloadCodec.decodeFromHex(coded);
        // then
        assertEquals(input, decoded);
    }
}