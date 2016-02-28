package com.geekhub.choosehelper.utils;

import android.util.Log;

import com.geekhub.choosehelper.models.db.User;

import io.realm.Realm;

public class DbUtil {

    private static final String TAG = DbUtil.class.getSimpleName();

    // Save user to local database
    public static void saveUser(final User user) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
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
        return Realm.getDefaultInstance().where(User.class).equalTo("id", id).findFirstAsync();
    }
}
