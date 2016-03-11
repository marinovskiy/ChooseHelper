package com.geekhub.choosehelper.utils.db;

import android.util.Log;

import com.geekhub.choosehelper.models.db.User;

import io.realm.Realm;
import io.realm.RealmResults;

public class DbUsersManager {

    private static final String TAG = DbUsersManager.class.getSimpleName();

    // save user to local database
    public static void saveUser(User user) {
        Realm realm = Realm.getDefaultInstance();
//        realm.beginTransaction();
//        realm.copyToRealm(user);
//        realm.commitTransaction();
//        realm.close();
        realm.executeTransaction(realm1 -> {
            Log.i(TAG, "execute: " + user.getId());
            Log.i(TAG, "execute: " + user.getEmail());
            Log.i(TAG, "execute: " + user.getFullName());
            Log.i(TAG, "execute: " + user.getPhotoUrl());
            realm1.copyToRealmOrUpdate(user);
        }/*, new Realm.Transaction.Callback() {
            @Override
            public void onSuccess() {
                Log.i("errorlogs", "DbUsersManager.saveUser() -> execute onSuccess");
                Log.i(TAG, "onSuccess: ");
                super.onSuccess();
            }

            @Override
            public void onError(Exception e) {
                Log.i("errorlogs", "DbUsersManager.saveUser() -> execute onError: " + e.getMessage());
                Log.i(TAG, "onError: ");
                e.printStackTrace();
                super.onError(e);
            }
        }*/);
    }

    // Get user to local database
    public static User getUser(String id) {
        return Realm.getDefaultInstance().where(User.class).equalTo("id", id).findFirstAsync();
//        return Realm.getDefaultInstance().where(User.class).equalTo("id", id).findFirst();
    }
}
