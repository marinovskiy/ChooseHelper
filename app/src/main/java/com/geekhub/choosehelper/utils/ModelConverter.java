package com.geekhub.choosehelper.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.geekhub.choosehelper.models.db.User;
import com.geekhub.choosehelper.models.network.NetworkUser;

/**
 * Created by Alex on 28.02.2016.
 */
public class ModelConverter {

    public static User convertToUser(@NonNull NetworkUser networkUser) {
        User user = new User();
        user.setId(Prefs.getUserId());
        user.setEmail(networkUser.getEmail());
        user.setFullName(networkUser.getFullName());
        user.setPhotoUrl(networkUser.getPhotoUrl());
        /*user.setBirthday(networkUser.getBirthday());
        user.setPlaceLive(networkUser.getPlaceLive());
        user.setAbout(networkUser.getAbout());*/
        /*Log.i("errorlogs", "AuthUtil execute: " + user.getId());
        Log.i("errorlogs", "AuthUtil execute: " + user.getEmail());
        Log.i("errorlogs", "AuthUtil execute: " + user.getFullName());
        Log.i("errorlogs", "AuthUtil execute: " + user.getPhotoUrl());
        Log.i("errorlogs", "AuthUtil execute: " + user);*/
        return user;
    }

}
