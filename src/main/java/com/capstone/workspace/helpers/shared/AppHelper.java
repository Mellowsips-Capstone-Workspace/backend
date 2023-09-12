package com.capstone.workspace.helpers.shared;

import java.util.regex.Pattern;

public class AppHelper {
    public static boolean isEmail(String s) {
        return Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$").matcher(s).matches();
    }

    public static boolean isVietnamNumberPhone(String s) {
        return Pattern.compile("(84[3|5|7|8|9])+(\\d{8})\\b").matcher(s).matches();
    }
}
