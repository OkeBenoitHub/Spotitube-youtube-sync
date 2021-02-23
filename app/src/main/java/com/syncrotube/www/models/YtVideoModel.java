package com.syncrotube.www.models;

import android.os.Parcel;
import android.os.Parcelable;

public class YtVideoModel implements Parcelable {
    private final String title;
    private final String videoId;
    private final String thumbnailPath;
    private final String publishedAt;

    public YtVideoModel(String title, String videoId, String thumbnailPath, String publishedAt) {
        this.title = title;
        this.videoId = videoId;
        this.thumbnailPath = thumbnailPath;
        this.publishedAt = publishedAt;
    }

    public String getTitle() {
        return title;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    private YtVideoModel(Parcel in) {
         title = in.readString();
         videoId = in.readString();
         thumbnailPath = in.readString();
         publishedAt = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeString(videoId);
        out.writeString(thumbnailPath);
        out.writeString(publishedAt);
    }

    public static final Creator<YtVideoModel> CREATOR = new Creator<YtVideoModel>() {
        public YtVideoModel createFromParcel(Parcel in) {
            return new YtVideoModel(in);
        }

        public YtVideoModel[] newArray(int size) {
            return new YtVideoModel[size];
        }
    };
}
