package com.atlaaya.evdrecharge.utils;

import java.util.Locale;

//https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
public final class StringUtils {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();


    static String bytesToHex(final byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String capitalizeFirstChar(String s) {
        Locale locale = Locale.getDefault();
        if (s != null && s.length() > 0) {
            return s.substring(0, 1).toUpperCase(locale) + s.substring(1).toLowerCase();
        }
        return "";
    }

}
