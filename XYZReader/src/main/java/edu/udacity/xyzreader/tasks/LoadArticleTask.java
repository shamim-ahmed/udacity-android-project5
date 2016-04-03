package edu.udacity.xyzreader.tasks;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import edu.udacity.xyzreader.ui.ArticleDetailFragment;
import edu.udacity.xyzreader.util.DbUtils;

public class LoadArticleTask extends AsyncTask<Uri, Void, ContentValues> {
    private final ArticleDetailFragment fragment;

    public LoadArticleTask(ArticleDetailFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    protected ContentValues doInBackground(Uri... params) {
        if (params == null || params.length == 0 || fragment == null) {
            return null;
        }

        Uri contentUri = params[0];
        Cursor cursor = fragment.getActivity().getContentResolver().query(contentUri, null, null, null, null);
        ContentValues values = new ContentValues();

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
        if (fragment != null) {
            fragment.updateViews(values);
        }
    }
}
