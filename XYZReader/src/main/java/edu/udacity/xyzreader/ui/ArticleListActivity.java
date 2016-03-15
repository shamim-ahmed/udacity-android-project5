package edu.udacity.xyzreader.ui;

import android.app.ActivityOptions;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import edu.udacity.xyzreader.R;
import edu.udacity.xyzreader.data.ArticleLoader;
import edu.udacity.xyzreader.data.ItemsContract;
import edu.udacity.xyzreader.data.UpdaterService;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends ActionBarActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        int offsetFromTop = 250;
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, offsetFromTop);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        getLoaderManager().initLoader(0, null, this);

        if (savedInstanceState == null) {
            refresh();
        }
    }

    private void refresh() {
        startService(new Intent(this, UpdaterService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);
    }

    private boolean mIsRefreshing = false;

    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                updateRefreshingUI();
            }
        }
    };

    private void updateRefreshingUI() {
        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        CustomAdapter adapter = new CustomAdapter(cursor);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);

        String displaySpec = getString(R.string.display_spec);

        switch (displaySpec) {
            case "default": {
                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                mRecyclerView.setLayoutManager(layoutManager);
                break;
            }

            default: {
                int columnCount = getResources().getInteger(R.integer.list_column_count);
                StaggeredGridLayoutManager sglm =
                        new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(sglm);

                break;
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    private class CustomAdapter extends RecyclerView.Adapter<CustomViewHolder> {
        private Cursor mCursor;

        public CustomAdapter(Cursor cursor) {
            mCursor = cursor;
        }

        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(ArticleLoader.Query._ID);
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_article, parent, false);
            final CustomViewHolder vh = new CustomViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView thumbnailView = vh.getThumbnailView();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(ArticleListActivity.this, thumbnailView, thumbnailView.getTransitionName())
                                .toBundle();
                        Intent intent = new Intent(Intent.ACTION_VIEW, ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition())));
                        startActivity(intent, bundle);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition())));
                        startActivity(intent);
                    }
                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {
            mCursor.moveToPosition(position);
            holder.getTitleView().setText(mCursor.getString(ArticleLoader.Query.TITLE));
            holder.getSubtitleView().setText(
                    DateUtils.getRelativeTimeSpanString(
                            mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + " by "
                            + mCursor.getString(ArticleLoader.Query.AUTHOR));

            holder.setThumbnailUrl(mCursor.getString(ArticleLoader.Query.THUMB_URL));
            holder.loadThumbnail(getApplicationContext());
        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }
    }
}
