package com.geekhub.choosehelper.entities;


import com.google.android.gms.common.api.GoogleApiClient;

public class UserProfile {

    private static UserProfile userProfile;

    private String mUsername;
    private String mEmail;
    private String mPhotoUrl;

    //  Only for G+ account
    private GoogleApiClient mGoogleApiClient;

    private UserProfile() {
    }

    public static UserProfile getInstance() {
        if (userProfile == null) {
            return userProfile = new UserProfile();
        } else
            return userProfile;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public void setPhotoUrl(String mPhotoUrl) {
        this.mPhotoUrl = mPhotoUrl;
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void setGoogleApiClient(GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;
    }
}
