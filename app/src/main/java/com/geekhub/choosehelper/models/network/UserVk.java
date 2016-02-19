package com.geekhub.choosehelper.models.network;

import com.google.gson.annotations.SerializedName;

public class UserVk {

    public UserVk() {

    }

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("photo_100")
    private String avatarUrl;

}
