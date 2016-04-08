package com.geekhub.choosehelper.models.network;

import java.util.List;

public class NetworkUser {

    private String email;

    private String fullName;

    private String photoUrl;

    private List<String> followings;

    public NetworkUser() {

    }

    public NetworkUser(String email, String fullName, String photoUrl) {
        this.email = email;
        this.fullName = fullName;
        this.photoUrl = photoUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<String> getFollowings() {
        return followings;
    }

    public void setFollowings(List<String> followings) {
        this.followings = followings;
    }
}
