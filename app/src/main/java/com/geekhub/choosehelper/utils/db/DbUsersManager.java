package com.geekhub.choosehelper.utils.db;

import android.util.Log;

import com.geekhub.choosehelper.models.db.User;

import io.realm.Realm;

public class DbUsersManager {

    private static final String TAG = DbUsersManager.class.getSimpleName();

    // save user to local database
    public static void saveUser(User user) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            Log.i(TAG, "execute: " + user.getId());
            Log.i(TAG, "execute: " + user.getEmail());
            Log.i(TAG, "execute: " + user.getFullName());
            Log.i(TAG, "execute: " + user.getPhotoUrl());
            realm1.copyToRealmOrUpdate(user);
        });
    }

    // Get user to local database
    public static User getUser(String id) {
        return Realm.getDefaultInstance().where(User.class).equalTo("id", id).findFirstAsync();
    }
}
