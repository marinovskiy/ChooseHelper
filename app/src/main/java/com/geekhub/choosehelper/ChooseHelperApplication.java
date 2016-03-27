package com.geekhub.choosehelper;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.firebase.client.Firebase;
import com.geekhub.choosehelper.models.db.Variant;
import com.geekhub.choosehelper.utils.Prefs;

import java.util.concurrent.atomic.AtomicLong;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ChooseHelperApplication extends Application {

    public static AtomicLong sPrimaryKey;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Prefs.init(this);
        Firebase.setAndroidContext(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder(this)
                .name("choose_helper_db.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configuration);
        Realm realm = Realm.getDefaultInstance();
        try {
            sPrimaryKey = new AtomicLong(realm.where(Variant.class).max("id").longValue());
        } catch (NullPointerException e) {
            sPrimaryKey = new AtomicLong(0);
        }
        realm.close();
    }
}
