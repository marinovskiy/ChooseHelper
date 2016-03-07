package com.geekhub.choosehelper.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.geekhub.choosehelper.models.db.User;
import com.geekhub.choosehelper.models.network.NetworkUser;

/**
 * Created by Alex on 28.02.2016.
 */
public class ModelConverter {

    private static final String TAG = "AuthorizationUtil";

    public static User convertToUser(@NonNull NetworkUser networkUser) {
        User user = new User();
        user.setId(Prefs.getUserId());
        user.setEmail(networkUser.getEmail());
        user.setFullName(networkUser.getFullName());
        Log.i(TAG, "setUpNavHeader: " + networkUser.getFullName());
        Log.i(TAG, "setUpNavHeader: " + networkUser.getEmail());
        Log.i(TAG, "setUpNavHeader: " + networkUser.getPhotoUrl());
        Log.i(TAG, "setUpNavHeader: " + user.getFullName());
        Log.i(TAG, "setUpNavHeader: " + user.getEmail());
        Log.i(TAG, "setUpNavHeader: " + user.getPhotoUrl());
        return user;
    }

}
