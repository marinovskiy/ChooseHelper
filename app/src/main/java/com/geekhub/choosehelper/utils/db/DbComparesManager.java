package com.geekhub.choosehelper.utils.db;

import com.geekhub.choosehelper.models.db.Compare;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class DbComparesManager {

    public static void saveCompares(List<Compare> compares) {
        cleanCompares();
        Realm.getDefaultInstance().executeTransaction(realm1 ->
                realm1.copyToRealmOrUpdate(compares));
    }

    public static void saveCompare(Compare compare) {
        Realm.getDefaultInstance().executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(compare));
    }

    public static RealmResults<Compare> getCompares() {
        return Realm.getDefaultInstance().where(Compare.class)
                .findAllSortedAsync(DbFields.DB_COMPARES_DATE, false);
    }

    public static Compare getCompareById(String id) {
        return Realm.getDefaultInstance()
                .where(Compare.class)
                .equalTo(DbFields.DB_COMPARES_ID, id)
                .findFirstAsync();
    }

    private static void cleanCompares() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Compare> compares = realm.where(Compare.class).findAll();
        realm.beginTransaction();
        compares.clear();
        realm.commitTransaction();
        realm.close();
    }

}
