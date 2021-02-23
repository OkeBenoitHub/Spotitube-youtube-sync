package com.syncrotube.www.models;

import androidx.lifecycle.ViewModel;

import com.syncrotube.www.sqlitebase.database.YtVideoEntry;

import java.util.List;

public class PlaylistViewModel extends ViewModel {
    public String mUserName;
    private String mUserId;
    public String mPlaylistName;
    public String mPlaylistId;
    public String mUserProfilePicPath;
    public boolean mIsPlaylistCreated;
    public boolean mAllVideosDonePlaying;
    public List<YtVideoEntry> mYtVideoEntries;

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getPlaylistName() {
        return mPlaylistName;
    }

    public void setPlaylistName(String playlistName) {
        mPlaylistName = playlistName;
    }

    public String getUserProfilePicPath() {
        return mUserProfilePicPath;
    }

    public void setUserProfilePicPath(String userProfilePicPath) {
        mUserProfilePicPath = userProfilePicPath;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getPlaylistId() {
        return mPlaylistId;
    }

    public void setPlaylistId(String playlistId) {
        mPlaylistId = playlistId;
    }

    public boolean isPlaylistCreated() {
        return mIsPlaylistCreated;
    }

    public void setPlaylistCreated(boolean playlistCreated) {
        mIsPlaylistCreated = playlistCreated;
    }

    public boolean isAllVideosDonePlaying() {
        return mAllVideosDonePlaying;
    }

    public void setAllVideosDonePlaying(boolean allVideosDonePlaying) {
        mAllVideosDonePlaying = allVideosDonePlaying;
    }

    public List<YtVideoEntry> getYtVideoEntries() {
        return mYtVideoEntries;
    }

    public void setYtVideoEntries(List<YtVideoEntry> ytVideoEntries) {
        mYtVideoEntries = ytVideoEntries;
    }
}
