package com.geekhub.choosehelper.utils.db;

import com.geekhub.choosehelper.models.db.User;
import com.geekhub.choosehelper.utils.Prefs;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class DbUsersManager {

    public static void saveUser(User user) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(user));
    }

    public static User getUserById(String userId) {
        return Realm.getDefaultInstance()
                .where(User.class)
                .equalTo(DbFields.DB_ID, userId)
                .findFirstAsync();
    }

    public static User getCurrentUser() {
        return Realm.getDefaultInstance()
                .where(User.class)
                .equalTo(DbFields.DB_ID, Prefs.getUserId())
                .findFirst();
    }

    public static RealmResults<User> getUserByIdSync(String userId) {
        return Realm.getDefaultInstance()
                .where(User.class)
                .equalTo(DbFields.DB_ID, userId)
                .findAll();
    }
}
