package edu.udacity.xyzreader.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import edu.udacity.xyzreader.R;
import edu.udacity.xyzreader.data.ItemsContract;
import edu.udacity.xyzreader.util.DbUtils;
import edu.udacity.xyzreader.util.StringUtils;

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
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // find the item
        Uri contentUri = getIntent().getData();
        Log.i(TAG, String.format("The item URI is : %s", contentUri));

        if (contentUri != null) {
            Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);

            try {
                if (cursor != null && cursor.moveToFirst()) {
                    ContentValues values = DbUtils.readValues(cursor);
                    updateViews(values);
                }
            } finally {
                DbUtils.close(cursor);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.article_detail_menu, menu);
        return true;
    }

    private void updateViews(ContentValues values) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setExpandedTitleTextAppearance(R.style.TransparentText);
        }

        // display title
        TextView titleView = (TextView) findViewById(R.id.article_detail_title);
        final String title = values.getAsString(ItemsContract.Items.TITLE);
        titleView.setText(title);

        // compute subtitle
        String publishedDateStr = StringUtils.formatDate(values.getAsLong(ItemsContract.Items.PUBLISHED_DATE));

        if (StringUtils.isBlank(publishedDateStr)) {
            publishedDateStr = getString(R.string.publish_date_default_value);
        }

        String author = values.getAsString(ItemsContract.Items.AUTHOR);

        if (StringUtils.isBlank(author)) {
            author = getString(R.string.author_default_value);
        }

        // display subtitle
        String subtitle = constructByline(values);
        TextView subtitleView = (TextView) findViewById(R.id.article_detail_subtitle);
        subtitleView.setText(subtitle);

        // load the image
        final Uri imageUri = Uri.parse(values.getAsString(ItemsContract.Items.PHOTO_URL));
        ImageView imageView = (ImageView) findViewById(R.id.article_image);
        Picasso.with(this).load(imageUri).fit().centerCrop().into(imageView);

        // display body
        TextView textView = (TextView) findViewById(R.id.article_detail_body);
        textView.setText(Html.fromHtml(values.getAsString(ItemsContract.Items.BODY)));

        // share button
        FloatingActionButton shareButton = (FloatingActionButton) findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra("textToShare", getString(R.string.share_text_format, title, imageUri.toString()));
                shareIntent.setType("text/plain");
                startActivity(shareIntent);
            }
        });
    }

    private String constructByline(ContentValues values) {
        String publishedDateStr = StringUtils.formatDate(values.getAsLong(ItemsContract.Items.PUBLISHED_DATE));

        if (StringUtils.isBlank(publishedDateStr)) {
            publishedDateStr = getString(R.string.publish_date_default_value);
        }

        String author = values.getAsString(ItemsContract.Items.AUTHOR);

        if (StringUtils.isBlank(author)) {
            author = getString(R.string.author_default_value);
        }

        return getString(R.string.article_subtitle_format, publishedDateStr, author);
    }
}
