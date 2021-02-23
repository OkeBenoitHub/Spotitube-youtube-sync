package com.syncrotube.www.sqlitebase.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "playlist")
public class YtVideoEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String videoId;
    private String title;
    private String thumbnailPath;
    private String publishedAt;
    private boolean isFavorite;
    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    // adder fields
    private String adderId;
    private String adderName;

    public YtVideoEntry(int id, String videoId, String title, String thumbnailPath, String publishedAt, boolean isFavorite, Date updatedAt, String adderId, String adderName) {
        this.id = id;
        this.videoId = videoId;
        this.title = title;
        this.thumbnailPath = thumbnailPath;
        this.publishedAt = publishedAt;
        this.isFavorite = isFavorite;
        this.updatedAt = updatedAt;
        this.adderId = adderId;
        this.adderName = adderName;
    }

    @Ignore
    public YtVideoEntry(String videoId, String title, String thumbnailPath, String publishedAt, boolean isFavorite, Date updatedAt, String adderId, String adderName) {
        this.videoId = videoId;
        this.title = title;
        this.thumbnailPath = thumbnailPath;
        this.publishedAt = publishedAt;
        this.isFavorite = isFavorite;
        this.updatedAt = updatedAt;
        this.adderId = adderId;
        this.adderName = adderName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAdderId() {
        return adderId;
    }

    public void setAdderId(String adderId) {
        this.adderId = adderId;
    }

    public String getAdderName() {
        return adderName;
    }

    public void setAdderName(String adderName) {
        this.adderName = adderName;
    }
}
