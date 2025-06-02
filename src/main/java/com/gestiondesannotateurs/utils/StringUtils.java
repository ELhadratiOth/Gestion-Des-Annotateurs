package com.gestiondesannotateurs.utils;

public class StringUtils {
    public static String safeSubstring(String input, int index) {
        if (input == null) return "";

        if (index < 0) index = 0;
        if (index > input.length()) index = input.length();

        return input.substring(0, index);
    }

}
