package edu.udacity.xyzreader.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import edu.udacity.xyzreader.R;
import edu.udacity.xyzreader.data.ItemsContract;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity {

    private static final String TAG = ArticleDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        }

        // find the item
        Uri articleUri = getIntent().getData();
        Log.i(TAG, String.format("The item URI is : %s", articleUri));

        if (articleUri != null) {
            Cursor cursor = getContentResolver().query(articleUri, null, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                ContentValues values = readValues(cursor);
                Log.i(TAG, "The photo url is " + values.get(ItemsContract.Items.PHOTO_URL));
                cursor.close();
            }
        }
    }

    private ContentValues readValues(Cursor cursor) {
        int titleIndex = cursor.getColumnIndex(ItemsContract.Items.TITLE);
        int photoUrlIndex = cursor.getColumnIndex(ItemsContract.Items.PHOTO_URL);
        int bodyIndex = cursor.getColumnIndex(ItemsContract.Items.BODY);

        String title = cursor.getString(titleIndex);
        String photoUrl = cursor.getString(photoUrlIndex);
        String body = cursor.getString(bodyIndex);

        ContentValues values = new ContentValues();
        values.put(ItemsContract.Items.TITLE, title);
        values.put(ItemsContract.Items.PHOTO_URL, photoUrl);
        values.put(ItemsContract.Items.BODY, body);

        return values;
    }
}
