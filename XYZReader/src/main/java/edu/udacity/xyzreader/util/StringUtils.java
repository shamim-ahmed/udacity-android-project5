package edu.udacity.xyzreader.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StringUtils {
    public static final String DATE_FORMAT_STR = "dd/MM/yyyy";
    public static final String EMPTY_STR = "";

    public static boolean isBlank(String str) {
        return str == null || str.trim().equals("");
    }

    public static String formatDate(Long value) {
        if (value == null) {
            return EMPTY_STR;
        }

        Date date = new Date(value);
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STR, Locale.US);
        return dateFormat.format(date);
    }
    // private constructor to prevent instantiation
    private StringUtils() {
    }
}
