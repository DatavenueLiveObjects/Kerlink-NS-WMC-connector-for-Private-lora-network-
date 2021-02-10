package com.orange.lo.sample.kerlink2lo.utils;

public class StringUtils {

    private StringUtils() {
    }

    public static String sanitize(String message) {
        return message.replaceAll("\\r\\n|\\r|\\n", " ");
    }
}
