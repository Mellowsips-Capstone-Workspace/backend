package com.capstone.workspace.helpers.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class AppHelper {
    private static Logger logger = LoggerFactory.getLogger(AppHelper.class);
    public static String[] commonProperties = new String[]{"id", "createdAt", "createdBy", "updatedAt", "updatedBy", "isDeleted"};

    public static boolean isEmail(String s) {
        return Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$").matcher(s).matches();
    }

    public static boolean isVietnamNumberPhone(String s) {
        return Pattern.compile("(84[3|5|7|8|9])+(\\d{8})\\b").matcher(s).matches();
    }

    public static Map<String, Object> copyPropertiesToMap(Object o) {
        Map<String, Object> result = new HashMap<>();

        Class clazz = o.getClass();
        for (Method method: clazz.getMethods()) {
            if (method.getName().startsWith("get") && !method.getName().equals("getClass")) {
                String key = method.getName().substring(3);
                String lowercaseKey = key.substring(0, 1).toLowerCase() + key.substring(1);

                Object value = null;
                try {
                    value = method.invoke(o);
                } catch (Exception e) {
                    logger.warn("Class does not support method " + method.getName());
                }

                if (value != null) {
                    result.put(lowercaseKey, value);
                }
            }
        }

        return result;
    }

    public static String getCurrentVietnamTimeString(String format) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }

    public static List removeDuplicates(List data) {
        return new ArrayList<>(new HashSet<>(data));
    }
}
