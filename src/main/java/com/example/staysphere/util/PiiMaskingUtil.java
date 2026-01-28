package com.example.staysphere.util;

public class PiiMaskingUtil {
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        String[] parts = email.split("@");
        String user = parts[0];
        String domain = parts[1];
        String maskedUser = user.length() > 1 ? user.charAt(0) + "***" : "*";
        String maskedDomain = domain.length() > 1 ? domain.charAt(0) + "***" : "*";
        return maskedUser + "@" + maskedDomain + ".com";
    }

    public static String maskName(String name) {
        if (name == null || name.length() < 2) return name;
        return name.charAt(0) + "***";
    }
}
