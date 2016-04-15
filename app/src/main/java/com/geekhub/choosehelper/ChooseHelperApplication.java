package com.geekhub.choosehelper;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.firebase.client.Firebase;
import com.geekhub.choosehelper.models.db.Follower;
import com.geekhub.choosehelper.models.db.Following;
import com.geekhub.choosehelper.models.db.Variant;
import com.geekhub.choosehelper.utils.Prefs;

import java.util.concurrent.atomic.AtomicLong;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ChooseHelperApplication extends Application {

    // primary keys (id) for realm
    public static AtomicLong sVariantPrimaryKey;
    public static AtomicLong sFollowerPrimaryKey;
    public static AtomicLong sFollowingPrimaryKey;

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

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        Firebase.setAndroidContext(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder(this)
                .name("choose_helper_db.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configuration);
        Realm realm = Realm.getDefaultInstance();
        try {
            sVariantPrimaryKey = new AtomicLong(realm.where(Variant.class).max("id").longValue());
        } catch (NullPointerException e) {
            sVariantPrimaryKey = new AtomicLong(0);
        }
        try {
            sFollowerPrimaryKey = new AtomicLong(realm.where(Follower.class).max("id").longValue());
        } catch (NullPointerException e) {
            sFollowerPrimaryKey = new AtomicLong(0);
        }
        try {
            sFollowingPrimaryKey = new AtomicLong(realm.where(Following.class).max("id").longValue());
        } catch (NullPointerException e) {
            sFollowingPrimaryKey = new AtomicLong(0);
        }
        realm.close();
    }
}