package com.geekhub.choosehelper.utils;

import android.support.annotation.NonNull;

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
        return user;
    }

}
