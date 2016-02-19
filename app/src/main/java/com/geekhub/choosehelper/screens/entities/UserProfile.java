package com.geekhub.choosehelper.screens.entities;


public class UserProfile {

    private String mUsername;

    public UserProfile() {
    }

    public UserProfile(String userName) {
        this.mUsername = userName;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }
}
