package edu.udacity.xyzreader.ui;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.udacity.xyzreader.R;
import edu.udacity.xyzreader.data.ArticleLoader;
import edu.udacity.xyzreader.data.ItemsContract;
import edu.udacity.xyzreader.data.UpdaterService;

public class ArticleListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private boolean mIsRefreshing = false;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ArticleListActivity activity = (ArticleListActivity) getActivity();
        View view = inflater.inflate(R.layout.fragment_article_list, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
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

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        activity.getLoaderManager().initLoader(0, null, this);


        if (savedInstanceState == null) {
            refresh();
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mRefreshingReceiver);
    }

    private void refresh() {
        Activity activity = getActivity();
        activity.startService(new Intent(activity, UpdaterService.class));
    }

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
        return ArticleLoader.newAllArticlesInstance(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        CustomAdapter adapter = new CustomAdapter(cursor);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);

        String displaySpec = getString(R.string.display_spec);

        switch (displaySpec) {
            case "default": {
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
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
            View view = getActivity().getLayoutInflater().inflate(R.layout.list_item_article, parent, false);
            final CustomViewHolder vh = new CustomViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition())));
                    startActivity(intent);
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
            holder.loadThumbnail(getActivity().getApplicationContext());
        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }
    }
}
