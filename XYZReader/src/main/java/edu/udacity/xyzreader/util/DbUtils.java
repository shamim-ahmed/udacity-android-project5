package edu.udacity.xyzreader.util;

import android.database.Cursor;
import android.util.Log;

public class DbUtils {
    private static final String TAG = DbUtils.class.getSimpleName();

    public static void close(Cursor cursor) {
        if (cursor != null) {
            try {
                cursor.close();;
            } catch (Exception ex) {
                Log.w(TAG, "Error while closing cursor", ex);
            }
        }
    }

    // private constructor to prevent instantiation
    private DbUtils() {
    }
}
