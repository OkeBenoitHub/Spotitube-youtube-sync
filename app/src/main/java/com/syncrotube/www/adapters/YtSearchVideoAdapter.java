package com.syncrotube.www.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.syncrotube.www.R;
import com.syncrotube.www.models.YtVideoModel;
import com.syncrotube.www.sqlitebase.database.YtVideoEntry;

import java.util.ArrayList;

public class YtSearchVideoAdapter extends RecyclerView.Adapter<YtSearchVideoAdapter.VideoViewHolder> {
    private final ArrayList<YtVideoModel> mVideoItems;
    private final ListItemClickListener mOnClickListener;
    private final Context mContext;

    public YtSearchVideoAdapter(Context mContext, ArrayList<YtVideoModel> mVideoItems, ListItemClickListener mOnClickListener) {
        this.mVideoItems = mVideoItems;
        this.mOnClickListener = mOnClickListener;
        this.mContext = mContext;
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.yt_video_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        final YtVideoModel ytVideoModel = mVideoItems.get(position);
        // set video title
        holder.mMusicTitle.setText(ytVideoModel.getTitle());
        // set video year
        holder.mMusicPublishedDate.setText(ytVideoModel.getPublishedAt());

        // set video youtube thumbnail
        Glide.with(mContext)
                .load(ytVideoModel.getThumbnailPath())
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(new ColorDrawable(Color.GRAY))
                .into(holder.mMusicThumbnail);
    }

    @Override
    public int getItemCount() {
        if (mVideoItems != null) {
            return mVideoItems.size();
        }
        return 0;
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView mMusicThumbnail;
        private final TextView mMusicTitle;
        private final TextView mMusicPublishedDate;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            mMusicThumbnail = itemView.findViewById(R.id.music_thumbnail);
            mMusicTitle = itemView.findViewById(R.id.music_title);
            mMusicPublishedDate = itemView.findViewById(R.id.music_publish_date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnClickListener != null)
                mOnClickListener.onListItemClick(getAdapterPosition());
        }
    }
}
