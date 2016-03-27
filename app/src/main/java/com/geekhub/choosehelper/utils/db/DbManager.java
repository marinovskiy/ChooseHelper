package com.geekhub.choosehelper.utils.db;

import android.util.Log;

import com.geekhub.choosehelper.models.db.Variant;

import io.realm.Realm;

/**
 * Created by Alex on 27.03.2016.
 */
public class DbManager {

    public static long getNextVariantId() {
        long id;
        try {
            id = Realm.getDefaultInstance().where(Variant.class).max("id").longValue() + 1;
        } catch (NullPointerException e) {
            id = 0;
        }
        Log.i("logtags", "id = " + id);
        return id;
    }

}
