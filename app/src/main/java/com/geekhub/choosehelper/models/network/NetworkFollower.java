package com.geekhub.choosehelper.models.network;

public class NetworkFollower {

    private String userId;

    private String followerId;

    public NetworkFollower() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFollowerId() {
        return followerId;
    }

    public void setFollowerId(String followerId) {
        this.followerId = followerId;
    }
}