package com.geekhub.choosehelper;

import android.app.Application;
import android.content.Intent;

import com.firebase.client.Firebase;
import com.geekhub.choosehelper.screens.activities.SignInActivity;
import com.geekhub.choosehelper.utils.Prefs;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ChooseHelperApplication extends Application {

    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null && Prefs.getLoggedType() == Prefs.ACCOUNT_VK) {
                Intent intent = new Intent(ChooseHelperApplication.this, SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Prefs.init(this);
        Firebase.setAndroidContext(this);
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder(this)
                .name("choose_helper_db.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configuration);
    }
}
