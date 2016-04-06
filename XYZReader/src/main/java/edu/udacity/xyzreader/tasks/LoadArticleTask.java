package edu.udacity.xyzreader.tasks;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import edu.udacity.xyzreader.R;
import edu.udacity.xyzreader.data.ItemsContract;
import edu.udacity.xyzreader.ui.ArticleDetailFragment;
import edu.udacity.xyzreader.util.DbUtils;

public class LoadArticleTask extends AsyncTask<Uri, Void, ContentValues> {
    private static final String TAG = LoadArticleTask.class.getSimpleName();

    private final ArticleDetailFragment fragment;

    public LoadArticleTask(ArticleDetailFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    protected ContentValues doInBackground(Uri... params) {
        if (params == null || params.length == 0 || fragment == null) {
            return null;
        }

        Activity activity = fragment.getActivity();

        if (activity == null) {
            return null;
        }

        Uri contentUri = params[0];
        Cursor cursor = null;
        ContentValues values = new ContentValues();

        try {
            cursor = activity.getContentResolver().query(contentUri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                values = DbUtils.readValues(cursor);
            } else {
                values.put(ItemsContract.Items.TITLE, activity.getString(R.string.article_title_default_value));
            }
        } catch (Exception ex) {
            Log.e(TAG, "error while retrieving data from database", ex);
        } finally {
            DbUtils.close(cursor);
        }

        return values;
    }

    @Override
    protected void onPostExecute(ContentValues values) {
        if (fragment != null) {
            fragment.updateViews(values);
        }
    }
}
