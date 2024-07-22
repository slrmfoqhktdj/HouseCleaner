package com.example.housecleaner02.filemanage;

public class Utils {
    private Utils() {
        // Private constructor to prevent instantiation
    }

    public static <T> T checkNotNull(T object) {
        if (object == null) {
            throw new NullPointerException();
        }
        return object;
    }

    public static boolean isEmpty(CharSequence charSequence) {
        return charSequence == null || charSequence.length() == 0;
    }

    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        int length = a.length();
        if (length != b.length()) return false;
        if (a instanceof String && b instanceof String) {
            return a.equals(b);
        } else {
            for (int i = 0; i < length; i++) {
                if (a.charAt(i) != b.charAt(i)) return false;
            }
            return true;
        }
    }
}
