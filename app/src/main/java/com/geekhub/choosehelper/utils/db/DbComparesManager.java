package com.geekhub.choosehelper.utils.db;

import android.util.Log;

import com.geekhub.choosehelper.models.db.Compare;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class DbComparesManager {

    public static void saveCompares(List<Compare> compares) {
        Realm.getDefaultInstance().executeTransaction(realm1 ->
                realm1.copyToRealmOrUpdate(compares));
    }

    public static void saveCompare(Compare compare) {
        Log.i("commentslogtags", "saveCompare");
        Log.i("commentslogtags", "compare.comment=" + compare.getComments().get(0).getCommentText());
        Realm.getDefaultInstance().executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(compare));
    }

    public static RealmResults<Compare> getCompares() {
        RealmResults<Compare> compares = Realm.getDefaultInstance()
                .where(Compare.class)
                .findAllSortedAsync(DbFields.DB_COMPARES_DATE, false);
        Log.i("logtags", "fetchComparesFromDb mCompares.size=" + compares.size());
        return compares;
    }

    public static Compare getCompareById(String id) {
        return Realm.getDefaultInstance()
                .where(Compare.class)
                .equalTo(DbFields.DB_COMPARES_ID, id)
                .findFirstAsync();
    }

}
