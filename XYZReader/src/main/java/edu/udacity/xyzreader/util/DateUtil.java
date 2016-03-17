package edu.udacity.xyzreader.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    private static final String TAG = DateUtil.class.getSimpleName();

    private static final String DATE_FORMAT_STR = "dd/MM/yyyy";

    public static Date parseDate(String dateStr) {
        if (dateStr == null) {
            return null;
        }

        Date result = null;
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STR, Locale.US);

        try {
            result = dateFormat.parse(dateStr);
        } catch (ParseException ex) {
            Log.w(TAG, "date parsing failed", ex);
        }

        return result;
    }

    // private constructor to prevent instantiation
    private DateUtil() {
    }
}
