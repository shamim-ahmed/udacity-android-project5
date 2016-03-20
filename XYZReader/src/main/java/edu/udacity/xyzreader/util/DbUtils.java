package edu.udacity.xyzreader.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import edu.udacity.xyzreader.data.ItemsContract;

public class DbUtils {
    private static final String TAG = DbUtils.class.getSimpleName();

    public static void close(Cursor cursor) {
        if (cursor != null) {
            try {
                cursor.close();
                ;
            } catch (Exception ex) {
                Log.w(TAG, "Error while closing cursor", ex);
            }
        }
    }

    public static ContentValues readValues(Cursor cursor) {
        int titleIndex = cursor.getColumnIndex(ItemsContract.Items.TITLE);
        int authorIndex = cursor.getColumnIndex(ItemsContract.Items.AUTHOR);
        int publishedDateIndex = cursor.getColumnIndex(ItemsContract.Items.PUBLISHED_DATE);
        int photoUrlIndex = cursor.getColumnIndex(ItemsContract.Items.PHOTO_URL);
        int aspectRatioIndex = cursor.getColumnIndex(ItemsContract.Items.ASPECT_RATIO);
        int bodyIndex = cursor.getColumnIndex(ItemsContract.Items.BODY);

        String title = cursor.getString(titleIndex);
        String author = cursor.getString(authorIndex);
        String publishedDateStr = cursor.getString(publishedDateIndex);
        String photoUrl = cursor.getString(photoUrlIndex);
        Double aspectRatio = cursor.getDouble(aspectRatioIndex);
        String body = cursor.getString(bodyIndex);

        ContentValues values = new ContentValues();
        values.put(ItemsContract.Items.TITLE, title);
        values.put(ItemsContract.Items.AUTHOR, author);
        values.put(ItemsContract.Items.PUBLISHED_DATE, publishedDateStr);
        values.put(ItemsContract.Items.PHOTO_URL, photoUrl);
        values.put(ItemsContract.Items.ASPECT_RATIO, aspectRatio);
        values.put(ItemsContract.Items.BODY, body);

        return values;
    }

    // private constructor to prevent instantiation
    private DbUtils() {
    }
}
