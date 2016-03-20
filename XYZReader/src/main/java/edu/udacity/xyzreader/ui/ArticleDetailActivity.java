package edu.udacity.xyzreader.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import edu.udacity.xyzreader.R;
import edu.udacity.xyzreader.data.ItemsContract;
import edu.udacity.xyzreader.util.DbUtils;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity {

    private static final String TAG = ArticleDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        // add back button to go to home screen
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        }

        // find the item
        Uri contentUri = getIntent().getData();
        Log.i(TAG, String.format("The item URI is : %s", contentUri));

        if (contentUri != null) {
            Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);

            if (cursor != null) {
                try {
                    cursor.moveToFirst();
                    ContentValues values = readValues(cursor);
                    updateViews(values);
                } finally {
                    DbUtils.close(cursor);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void updateViews(ContentValues values) {
        // display title
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(values.getAsString(ItemsContract.Items.TITLE));

        // load the image
        Uri imageUri = Uri.parse(values.getAsString(ItemsContract.Items.PHOTO_URL));
        ImageView imageView = (ImageView) findViewById(R.id.article_image);
        Picasso.with(this).load(imageUri).fit().centerCrop().into(imageView);

        // display body
        TextView textView = (TextView) findViewById(R.id.article_body);
        textView.setText(Html.fromHtml(values.getAsString(ItemsContract.Items.BODY)));
    }

    private ContentValues readValues(Cursor cursor) {
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
}
