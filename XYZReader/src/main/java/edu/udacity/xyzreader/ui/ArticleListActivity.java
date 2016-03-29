package edu.udacity.xyzreader.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import edu.udacity.xyzreader.R;
import edu.udacity.xyzreader.util.Constants;

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
        if (contentUri == null) {
            Log.w(TAG, "content URI is null, will take no action");
            return;
        }

        Log.i(TAG, String.format("content uri : %s", contentUri));

        if (twoPaneRenderMode) {
            ArticleDetailFragment detailFragment = new ArticleDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.CONTENT_URI_ATTR_NAME, contentUri);
            detailFragment.setArguments(bundle);

            try {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.article_detail_container, detailFragment, Constants.ARTICLE_DETAIL_FRAGMENT_TAG)
                .commitAllowingStateLoss();
            } catch (Exception ex) {
                Log.w(TAG, "Error while committing fragment transaction", ex);
            }
        } else {
            Intent intent = new Intent(this, ArticleDetailActivity.class);
            intent.setData(contentUri);
            startActivity(intent);
        }
    }
}
