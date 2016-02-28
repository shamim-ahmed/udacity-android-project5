package edu.udacity.xyzreader.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import edu.udacity.xyzreader.R;

public class CustomViewHolder extends RecyclerView.ViewHolder {
    private final ImageView thumbnailView;
    private final TextView titleView;
    private final TextView subtitleView;

    public CustomViewHolder(View view) {
        super(view);
        thumbnailView = (ImageView) view.findViewById(R.id.thumbnail);
        titleView = (TextView) view.findViewById(R.id.article_title);
        subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
    }

    public TextView getTitleView() {
        return titleView;
    }

    public TextView getSubtitleView() {
        return subtitleView;
    }

    public ImageView getThumbnailView() {
        return thumbnailView;
    }
}

