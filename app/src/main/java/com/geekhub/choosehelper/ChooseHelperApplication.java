package com.geekhub.choosehelper;

import android.app.Application;
import android.content.Intent;

import com.geekhub.choosehelper.screens.activities.SignInActivity;
import com.geekhub.choosehelper.utils.Prefs;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

public class ChooseHelperApplication extends Application {

    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
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
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);
    }
}
