package com.geekhub.choosehelper.screens.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.screens.authentication.GooglePlusProfile;
import com.geekhub.choosehelper.screens.entities.UserProfile;
import com.geekhub.choosehelper.screens.keys.Key;
import com.geekhub.choosehelper.utils.AuthorizationUtil;
import com.geekhub.choosehelper.utils.Prefs;
import com.google.android.gms.common.AccountPicker;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import butterknife.OnClick;

public class SignInActivity extends BaseActivity {

    private String[] mScope = {
            VKScope.FRIENDS,
            VKScope.PHOTOS,
            VKScope.EMAIL,
            VKScope.OFFLINE
    };

    private static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    private static final String[] ACCOUNT_TYPES = new String[]{"com.google"};
    private static final String GOOGLE_ACCOUNT_TYPE = "com.google";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Prefs.isExpired()) {
            startMainActivity();
        }
    }

    @OnClick(R.id.sign_up_tv)
    public void signUp() {
        startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
    }

    @OnClick(R.id.sign_in_btn_google_plus)
    public void signInGooglePlus() {
        pickUserAccount();
        Prefs.setLoggedType(Prefs.PREFS_GOOGLE_PLUS);
    }

    @OnClick(R.id.sign_in_btn_vk)
    public void signInVk() {
        VKSdk.login(SignInActivity.this, mScope);
        Prefs.setLoggedType(Prefs.PREFS_VK);
    }

    private void pickUserAccount() {
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                ACCOUNT_TYPES, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);

        //  LOG
        Log.d(Key.LOG_TAG, "Started activity for result");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (Prefs.getLoggedType()) {
            case Prefs.PREFS_GOOGLE_PLUS:
                // u need to use these two methods before start main activity
//                Prefs.setExpired(true);
//                Prefs.setUserId(res.userId);
                if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
                    if (resultCode == RESULT_OK) {
                        String userEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                        getUserProfile(new Account(userEmail, GOOGLE_ACCOUNT_TYPE));

                        //  LOG
                        Log.d(Key.LOG_TAG, "Done");
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.tip_incorrect_r_code,
                            Toast.LENGTH_SHORT).show();
                }
//        Intent intent = new Intent(this, ProfileActivity.class);
//        intent.putExtras(data);
//        startActivity(intent);
                super.onActivityResult(requestCode, resultCode, data);
                break;
            case Prefs.PREFS_VK:
                VKCallback<VKAccessToken> callback = new VKCallback<VKAccessToken>() {
                    @Override
                    public void onResult(VKAccessToken res) {
                        Prefs.setExpired(true);
                        Prefs.setUserId(res.userId);
                        startMainActivity();
                    }

                    @Override
                    public void onError(VKError error) {
                        Toast.makeText(SignInActivity.this, "Error. Maybe wrong pass or login", Toast.LENGTH_SHORT).show();
                    }
                };

                if (!VKSdk.onActivityResult(requestCode, resultCode, data, callback)) {
                    super.onActivityResult(requestCode, resultCode, data);
                }
                break;
            case Prefs.PREFS_APP_ACCOUNT:

                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startMainActivity() {
        AuthorizationUtil.setVkUserProfileInfo();
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private UserProfile getUserProfile(Account account) {
        UserProfile userProfile = null;

        new GooglePlusProfile(this, account).execute();

        //  Return new user profile
        return userProfile;
    }
}
