package com.geekhub.choosehelper.utils.db;

import com.geekhub.choosehelper.models.db.Compare;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class DbComparesManager {

    public static void saveCompares(List<Compare> compares) {
        clearCompares();
        Realm.getDefaultInstance().executeTransaction(realm ->
                realm.copyToRealmOrUpdate(compares));
    }

    public static void saveCompare(Compare compare) {
        Realm.getDefaultInstance().executeTransaction(realm ->
                realm.copyToRealmOrUpdate(compare));
    }

    public static RealmResults<Compare> getCompares() {
        return Realm.getDefaultInstance().where(Compare.class)
                .findAllSortedAsync(DbFields.DB_COMPARES_DATE, Sort.DESCENDING);
    }

    public static Compare getCompareById(String id) {
        return Realm.getDefaultInstance()
                .where(Compare.class)
                .equalTo(DbFields.DB_COMPARES_ID, id)
                .findFirstAsync();
    }

    public static void clearCompares() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Compare> compares = realm.where(Compare.class).findAll();
        realm.beginTransaction();
        compares.clear();
        realm.commitTransaction();
        realm.close();
    }
}
