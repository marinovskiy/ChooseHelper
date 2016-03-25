package com.geekhub.choosehelper.utils.db;

import com.geekhub.choosehelper.models.db.Compare;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class DbComparesManager {

    public static void saveCompares(List<Compare> compares) {
        Realm.getDefaultInstance().executeTransaction(realm1 ->
                realm1.copyToRealmOrUpdate(compares));
    }

    public static RealmResults<Compare> getCompares() {
        return Realm.getDefaultInstance()
                .where(Compare.class)
                .findAllAsync();
    }

}
