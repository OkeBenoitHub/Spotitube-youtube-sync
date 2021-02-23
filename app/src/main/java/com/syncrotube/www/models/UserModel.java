package com.syncrotube.www.models;

public class UserModel {
    private String id, name, profilePicPath, lastOnlineTime, lastOnlineDate;

    public UserModel() {
    }

    public UserModel(String id, String name, String profilePicPath, String lastOnlineTime, String lastOnlineDate) {
        this.id = id;
        this.name = name;
        this.profilePicPath = profilePicPath;
        this.lastOnlineTime = lastOnlineTime;
        this.lastOnlineDate = lastOnlineDate;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProfilePicPath() {
        return profilePicPath;
    }

    public String getLastOnlineTime() {
        return lastOnlineTime;
    }

    public String getLastOnlineDate() {
        return lastOnlineDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setProfilePicPath(String profilePicPath) {
        this.profilePicPath = profilePicPath;
    }

    public void setLastOnlineTime(String lastOnlineTime) {
        this.lastOnlineTime = lastOnlineTime;
    }

    public void setLastOnlineDate(String lastOnlineDate) {
        this.lastOnlineDate = lastOnlineDate;
    }
}
