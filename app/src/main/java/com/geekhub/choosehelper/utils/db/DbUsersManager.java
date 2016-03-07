package com.geekhub.choosehelper.utils.db;

import android.util.Log;

import com.geekhub.choosehelper.models.db.User;

import io.realm.Realm;
import io.realm.RealmResults;

public class DbUsersManager {

    private static final String TAG = DbUsersManager.class.getSimpleName();

    // Save user to local database
    public static void saveUser(User user) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.i(TAG, "execute: " + user.getId());
                Log.i(TAG, "execute: " + user.getEmail());
                Log.i(TAG, "execute: " + user.getFullName());
                Log.i(TAG, "execute: " + user.getPhotoUrl());
                realm.copyToRealmOrUpdate(user);
            }
        }, new Realm.Transaction.Callback() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "onSuccess: ");
                super.onSuccess();
            }

            @Override
            public void onError(Exception e) {
                Log.i(TAG, "onSuccess: ");
                e.printStackTrace();
                super.onError(e);
            }
        });
    }

    // Get user to local database
    public static User getUser(String id) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<User> users = realm.where(User.class).findAll();
        User user = users.where().equalTo("id", id).findFirst();
        return user;
//        return Realm.getDefaultInstance().where(User.class).equalTo("id", id).findFirst();
    }
}
