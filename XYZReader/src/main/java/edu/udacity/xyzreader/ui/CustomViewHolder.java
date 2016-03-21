package edu.udacity.xyzreader.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import edu.udacity.xyzreader.R;

public class CustomViewHolder extends RecyclerView.ViewHolder {
    private final ImageView thumbnailView;
    private final TextView titleView;
    private final TextView subtitleView;

    private String thumbnailUrl;

    public CustomViewHolder(View view) {
        super(view);
        thumbnailView = (ImageView) view.findViewById(R.id.thumbnail);
        titleView = (TextView) view.findViewById(R.id.article_summary_title);
        subtitleView = (TextView) view.findViewById(R.id.article_summary_subtitle);
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

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void loadThumbnail(Context context) {
        if (thumbnailUrl == null) {
            return;
        }

        Picasso.with(context).load(thumbnailUrl).fit().centerCrop().into(thumbnailView);
    }
}
