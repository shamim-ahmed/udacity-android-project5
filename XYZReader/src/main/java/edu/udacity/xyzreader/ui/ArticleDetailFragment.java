package edu.udacity.xyzreader.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import edu.udacity.xyzreader.R;
import edu.udacity.xyzreader.data.ItemsContract;
import edu.udacity.xyzreader.tasks.LoadArticleTask;
import edu.udacity.xyzreader.util.Constants;
import edu.udacity.xyzreader.util.StringUtils;

public class ArticleDetailFragment extends Fragment {
    private static final String TAG = ArticleDetailFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_article_detail, container, false);
        Activity activity = getActivity();

        // add back button to go to home screen
        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        boolean flag = activity instanceof ArticleDetailActivity;

        if (toolbar != null && flag) {
            ArticleDetailActivity articleDetailActivity = (ArticleDetailActivity) activity;
            articleDetailActivity.setSupportActionBar(toolbar);
            ActionBar actionBar = articleDetailActivity.getSupportActionBar();

            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        // find the item
        Uri contentUri;

        if (flag) {
            contentUri = activity.getIntent().getData();
        } else {
            contentUri = (Uri) getArguments().get(Constants.CONTENT_URI_ATTR_NAME);
        }
        Log.i(TAG, String.format("The item URI is : %s", contentUri));

        LoadArticleTask task = new LoadArticleTask(this);
        task.execute(contentUri);

        return view;
    }

    public void updateViews(ContentValues values) {
        View view = getView();

        if (view == null) {
            Log.w(TAG, "article detail fragment not populated");
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setExpandedTitleTextAppearance(R.style.TransparentText);
        }

        // display title
        TextView titleView = (TextView) view.findViewById(R.id.article_detail_title);
        final String title = values.getAsString(ItemsContract.Items.TITLE);
        titleView.setText(title);

        // display subtitle
        String subtitle = constructSubtitle(values);
        TextView subtitleView = (TextView) view.findViewById(R.id.article_detail_subtitle);
        subtitleView.setText(subtitle);

        // load the image
        final String imageUriStr = values.getAsString(ItemsContract.Items.PHOTO_URL);

        if (!StringUtils.isBlank(imageUriStr)) {
            final Uri imageUri = Uri.parse(values.getAsString(ItemsContract.Items.PHOTO_URL));
            ImageView imageView = (ImageView) view.findViewById(R.id.article_image);
            Picasso.with(getActivity()).load(imageUri).fit().centerCrop().into(imageView);
        }

        // display body
        TextView textView = (TextView) view.findViewById(R.id.article_detail_body);
        String bodyText = values.getAsString(ItemsContract.Items.BODY);

        if (!StringUtils.isBlank(bodyText)) {
            textView.setText(Html.fromHtml(bodyText));
        }

        // share button
        final FloatingActionButton shareButton = (FloatingActionButton) view.findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text_format, title, imageUriStr));
                shareIntent.setType("text/plain");
                startActivity(shareIntent);
            }
        });
    }

    private String constructSubtitle(ContentValues values) {
        String publishedDateStr = StringUtils.formatDate(values.getAsLong(ItemsContract.Items.PUBLISHED_DATE));
        String author = values.getAsString(ItemsContract.Items.AUTHOR);

        String result;

        if (!StringUtils.isBlank(publishedDateStr) && !StringUtils.isBlank(author)) {
            result = getString(R.string.article_subtitle_format, publishedDateStr, author);
        } else {
            result = StringUtils.EMPTY_STR;
        }

        return result;
    }
}

