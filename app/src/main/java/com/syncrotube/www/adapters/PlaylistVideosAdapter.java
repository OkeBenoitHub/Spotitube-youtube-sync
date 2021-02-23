package com.syncrotube.www.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.syncrotube.www.R;
import com.syncrotube.www.sqlitebase.database.YtVideoEntry;
import com.syncrotube.www.utils.SharedPrefUtil;

import java.util.ArrayList;
import java.util.List;

import static com.syncrotube.www.utils.AppConstUtil.PLAYBACK_INDEX_KEY_PREF;

public class PlaylistVideosAdapter extends RecyclerView.Adapter<PlaylistVideosAdapter.PlaylistVideosViewHolder> {

    private List<YtVideoEntry> mYtVideoEntries;
    private final PlaylistVideosAdapter.ListItemClickListener mOnClickListener;
    private final Context mContext;

    public PlaylistVideosAdapter(Context mContext, List<YtVideoEntry> ytVideoEntries, PlaylistVideosAdapter.ListItemClickListener mOnClickListener) {
        this.mYtVideoEntries = ytVideoEntries;
        this.mOnClickListener = mOnClickListener;
        this.mContext = mContext;
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface ListItemClickListener {
        void onListItemClick(YtVideoEntry ytVideoEntry);
    }

    @NonNull
    @Override
    public PlaylistVideosAdapter.PlaylistVideosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.playlist_video_entry;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new PlaylistVideosAdapter.PlaylistVideosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistVideosAdapter.PlaylistVideosViewHolder holder, int position) {
        final YtVideoEntry ytVideoEntry = mYtVideoEntries.get(position);
        // set video title
        holder.mVideoTitle.setText(ytVideoEntry.getTitle());
        // set video year
        holder.mVideoPublishedDate.setText(ytVideoEntry.getPublishedAt());

        int video_playback_index_key_value = SharedPrefUtil.getDataIntFromSharedPreferences(mContext,PLAYBACK_INDEX_KEY_PREF);
        if (ytVideoEntry.getVideoId().equals(mYtVideoEntries.get(video_playback_index_key_value).getVideoId())) {
            holder.mVideoTitle.setSelected(true);
            holder.mYtVideoItemLayout.setBackgroundColor(mContext.getResources().getColor(R.color.yt_video_item_selected));
        } else {
            holder.mVideoTitle.setSelected(false);
            holder.mYtVideoItemLayout.setBackgroundColor(mContext.getResources().getColor(R.color.main_bg_app_color));
        }

        // set video youtube thumbnail
        Glide.with(mContext)
                .load(ytVideoEntry.getThumbnailPath())
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.synchroner_64)
                .placeholder(new ColorDrawable(Color.GRAY))
                .into(holder.mVideoThumbnail);
    }

    @Override
    public int getItemCount() {
        if (mYtVideoEntries != null) {
            return mYtVideoEntries.size();
        }
        return 0;
    }

    /**
     * When data changes, this method updates the list of data
     * and notifies the adapter to use the new values on it
     */
    public void setYtVideoEntries(List<YtVideoEntry> ytVideoEntries) {
        mYtVideoEntries = ytVideoEntries;
        notifyDataSetChanged();
    }

    public class PlaylistVideosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView mVideoThumbnail;
        private final TextView mVideoTitle;
        private final TextView mVideoPublishedDate;
        private final MaterialRippleLayout mYtVideoItemLayout;

        public PlaylistVideosViewHolder(@NonNull View itemView) {
            super(itemView);
            mVideoThumbnail = itemView.findViewById(R.id.video_thumbnail);
            mVideoTitle = itemView.findViewById(R.id.video_title);
            mVideoPublishedDate = itemView.findViewById(R.id.video_publish_date);
            mYtVideoItemLayout = itemView.findViewById(R.id.yt_video_item_layout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnClickListener != null)
                mOnClickListener.onListItemClick(mYtVideoEntries.get(getAdapterPosition()));
        }
    }
}
