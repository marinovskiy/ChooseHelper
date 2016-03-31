package com.geekhub.choosehelper.utils.db;

import com.geekhub.choosehelper.models.db.Compare;
import com.geekhub.choosehelper.models.db.User;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class DbUsersManager {

    private static final String TAG = DbUsersManager.class.getSimpleName();

    // save user to local database
    public static void saveUser(User user) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(user));
    }

    public static void saveUsers(List<User> user) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(user));
    }

    public static User getUserById(String userId) {
        return Realm.getDefaultInstance()
                .where(User.class)
                .equalTo("userId", userId)
                .findFirstAsync();
    }

    // Get user to local database
    public static User getUserAsync(String id) {
        return Realm.getDefaultInstance().where(User.class).equalTo("id", id).findFirstAsync();
    }

    public static User getUserNotAsync(String id) {
        return Realm.getDefaultInstance().where(User.class).equalTo("id", id).findFirst();
    }
}
