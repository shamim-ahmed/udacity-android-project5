package edu.udacity.xyzreader.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import edu.udacity.xyzreader.R;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity implements ArticleListFragment.Callback {
    private static final String TAG = ArticleListActivity.class.getSimpleName();

    private boolean twoPaneRenderMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);
        twoPaneRenderMode = (findViewById(R.id.article_detail_container) != null);
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        Log.i(TAG, "content uri : " + contentUri);

        if (twoPaneRenderMode) {
            // TODO implement it
        } else {
            Intent intent = new Intent(this, ArticleDetailActivity.class);
            intent.setData(contentUri);
            startActivity(intent);
        }
    }
}
