package com.geekhub.choosehelper.models.db;


public class Friend {

    private String mFriendName;
    private String mFriendAvatarUrl;

    public Friend(String mFriendName, String mFriendAvatarUrl) {
        this.mFriendName = mFriendName;
        this.mFriendAvatarUrl = mFriendAvatarUrl;
    }

    public String getFriendName() {
        return mFriendName;
    }

    public void setFriendName(String friendName) {
        this.mFriendName = friendName;
    }

    public String getFriendAvatarUrl() {
        return mFriendAvatarUrl;
    }

    public void setFriendAvatarUrl(String friendAvatarUrl) {
        this.mFriendAvatarUrl = friendAvatarUrl;
    }
}
