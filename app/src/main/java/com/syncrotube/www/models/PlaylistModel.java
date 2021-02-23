package com.syncrotube.www.models;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class PlaylistModel {
    private String id, name, ownerId, createdAtTime, createdOnDate;
    private List<String> sync, wall;
    private List<YtVideoModel> videos;

    public PlaylistModel() {
    }

    public PlaylistModel(String id, String name, String ownerId, String createdAtTime, String createdOnDate, List<YtVideoModel> videos, List<String> sync, List<String> wall) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.createdAtTime = createdAtTime;
        this.createdOnDate = createdOnDate;
        this.videos = videos;
        this.sync = sync;
        this.wall = wall;
    }

    public PlaylistModel(String id, String ownerId, List<YtVideoModel> videos, List<String> sync, List<String> wall) {
        this.id = id;
        this.ownerId = ownerId;
        this.videos = videos;
        this.sync = sync;
        this.wall = wall;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getCreatedAtTime() {
        return createdAtTime;
    }

    public String getCreatedOnDate() {
        return createdOnDate;
    }

    public List<YtVideoModel> getVideos() {
        return videos;
    }

    public List<String> getSync() {
        return sync;
    }

    public List<String> getWall() {
        return wall;
    }
}
