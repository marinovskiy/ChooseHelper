package com.geekhub.choosehelper.screens.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.keys.Key;
import com.geekhub.choosehelper.utils.AuthorizationUtil;
import com.geekhub.choosehelper.utils.Prefs;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN_GOOGLE = 9001;
    private static final int RC_SIGN_IN_VK = 9002;

    private GoogleSignInOptions mGoogleSignInOptions;
    private static GoogleApiClient mGoogleApiClient;

    private String[] mScope = {
            VKScope.FRIENDS,
            VKScope.PHOTOS,
            VKScope.EMAIL,
            VKScope.OFFLINE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        if (Prefs.isExpired()) {
            startMainActivity(Prefs.getLoggedType());
        }

        ButterKnife.bind(this);

        // Google+ sign in
        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions)
                .build();
    }

    @OnClick(R.id.sign_up_tv)
    public void signUp() {
        startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
    }

    @OnClick(R.id.sign_in_btn_google_plus)
    public void signInGooglePlus() {
        Prefs.setLoggedType(Prefs.PREFS_GOOGLE_PLUS);
        googlePlusSignIn();
    }

    @OnClick(R.id.sign_in_btn_vk)
    public void signInVk() {
        Prefs.setLoggedType(Prefs.PREFS_VK);
        VKSdk.login(SignInActivity.this, mScope);
    }

    private void googlePlusSignIn() {
        Intent signInIntent = new Intent(Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient));
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (Prefs.getLoggedType()) {

            //  Google Plus
            case Prefs.PREFS_GOOGLE_PLUS:
                super.onActivityResult(requestCode, resultCode, data);

                if (requestCode == RC_SIGN_IN_GOOGLE) {
                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                    handleSignInResult(result);
                    startMainActivity(Prefs.PREFS_GOOGLE_PLUS);
                }
                break;

            //  Vk.com
            case Prefs.PREFS_VK:
                VKCallback<VKAccessToken> callback = new VKCallback<VKAccessToken>() {
                    @Override
                    public void onResult(VKAccessToken res) {
                        Prefs.setExpired(true);
                        Prefs.setUserId(res.userId);
                        startMainActivity(Prefs.PREFS_VK);
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

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(Key.LT_MESSAGE, "handleSignInResult: " + result.isSuccess());

        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            Prefs.setUserId(acct.getId());
            Prefs.setUserName(acct.getDisplayName());
            Prefs.setUserAvatarUrl(acct.getPhotoUrl().toString());
        }
    }

    private void startMainActivity(int logType) {

        if (logType == Prefs.getLoggedType())
            AuthorizationUtil.setVkUserProfileInfo();

        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        intent.setFlags(/*Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | */Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(Key.LT_MESSAGE, "on ConnectionFailed");
    }

    public static GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }
}
