package edu.udacity.xyzreader.tasks;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import edu.udacity.xyzreader.ui.ArticleDetailActivity;
import edu.udacity.xyzreader.util.DbUtils;

public class ReadArticleTask extends AsyncTask<Uri, Void, ContentValues> {
    private final ArticleDetailActivity activity;

    public ReadArticleTask(ArticleDetailActivity activity) {
        this.activity = activity;
    }

    @Override
    protected ContentValues doInBackground(Uri... params) {
        if (params == null || params.length == 0) {
            return null;
        }

        Uri contentUri = params[0];
        Cursor cursor = activity.getContentResolver().query(contentUri, null, null, null, null);
        ContentValues values = null;

        try {
            if (cursor != null && cursor.moveToFirst()) {
                values = DbUtils.readValues(cursor);
            }
        } finally {
            DbUtils.close(cursor);
        }

        return values;
    }

    @Override
    protected void onPostExecute(ContentValues values) {
        activity.updateViews(values);
    }
}
