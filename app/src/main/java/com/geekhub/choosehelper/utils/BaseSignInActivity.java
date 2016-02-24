package com.geekhub.choosehelper.utils;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.geekhub.choosehelper.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;

import org.json.JSONArray;
import org.json.JSONException;

import butterknife.ButterKnife;

public class BaseSignInActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    //  Logs
    private static final String LT_MESS = BaseSignInActivity.class.getName();

    //  Request codes for sign in
    private static final int RC_GOOGLE_SIGN_IN = 9001;
    private static final int RC_VK_SIGN_IN = 10485;
    private static final int RC_APP_ACCOUNT_SIGN_IN = 9003;

    private GoogleApiClient mGoogleApiClient;
    private String[] mScope = {
            VKScope.FRIENDS,
            VKScope.PHOTOS,
            VKScope.EMAIL,
            VKScope.OFFLINE
    };

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignInOptions mGoogleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions)
                .build();
    }

    //  To sign in via G+ account
    //  call this method.
    //  App preferences will be set
    public void signInViaGoogle() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(intent, RC_GOOGLE_SIGN_IN);
    }

    //  To sign in via Vk account
    //  call this method.
    //  App preferences will be set
    public void signInViaVk() {
        VKSdk.login(this, mScope);
        Log.d(LT_MESS, "signInViaVk() method done");
    }

    //  To sign in via app account
    //  call this method
    public void signInViaAppAccount() {
        //  Will be soon...
    }

    //  To sign out from account
    //  call this method.
    //  App preferences will be update
    public void signOut() {

        switch (AppPreferences.getLoggedType()) {
            case AppPreferences.ACCOUNT_NONE:
                Toast.makeText(this, R.string.toast_sign_out_already, Toast.LENGTH_SHORT).show();

            case AppPreferences.ACCOUNT_GOOGLE_PLUS: {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d(LT_MESS, "Sign out success");
                    }
                });
            }
            break;
            case AppPreferences.ACCOUNT_VK: {
                VKSdk.logout();
            }
            break;
            case AppPreferences.ACCOUNT_APP: {

                //  Will be soon
            }
            break;
            default:
                break;
        }

        //  Update app preferences
        AppPreferences.setLoggedType(AppPreferences.ACCOUNT_NONE);
        doAfterSignOut();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LT_MESS, "onActivityResult()...");

        if (requestCode == RC_GOOGLE_SIGN_IN) {

            //  Google+ sign in
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();

                //  Set preferences
                AppPreferences.setLoggedType(AppPreferences.ACCOUNT_GOOGLE_PLUS);
                AppPreferences.setUserId(account.getId());
                AppPreferences.setUserName(account.getDisplayName());
                AppPreferences.setUserAvatarUrl(account.getPhotoUrl().toString());

                //  Calls to do next step
                doAfterSignIn();
            } else
                Log.d(LT_MESS, "Sign in failed. Result code: " + resultCode);

        } else if (requestCode == RC_VK_SIGN_IN) {
            Log.d(LT_MESS, "requestCode for Vk");

            //  Vk sign in
            VKCallback<VKAccessToken> callback = new VKCallback<VKAccessToken>() {
                @Override
                public void onResult(VKAccessToken res) {
                    AppPreferences.setLoggedType(AppPreferences.ACCOUNT_VK);
                    AppPreferences.setUserId(res.userId);
                    setVkUserInfo();
                    Log.d(LT_MESS, "setVkUserInfo() method done");

                    //  Calls to do next step
                    doAfterSignIn();
                    Log.d(LT_MESS, "doAfterSignIn() method calls");
                }

                @Override
                public void onError(VKError error) {
                    Toast.makeText(BaseSignInActivity.this,
                            R.string.toast_sign_in_error, Toast.LENGTH_SHORT).show();
                }
            };

            if (!VKSdk.onActivityResult(requestCode, resultCode, data, callback)) {
                super.onActivityResult(requestCode, resultCode, data);
            }

        } else if (requestCode == RC_APP_ACCOUNT_SIGN_IN) {

            //  App account sign in
            AppPreferences.setLoggedType(AppPreferences.ACCOUNT_APP);
            // code...

            //  Calls to do next step
            doAfterSignIn();
        }

    } // End of onActivityResult()

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    private static void setVkUserInfo() {
        VKRequest vkRequest = VKApi.users()
                .get(VKParameters.from(VKApiConst.USER_IDS, AppPreferences.getUserId(),
                        VKApiConst.FIELDS, "photo_200"));

        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    JSONArray jsonArray = response.json.getJSONArray("response");
                    VKApiUser vkApiUser = new VKApiUser(jsonArray.getJSONObject(0));
                    AppPreferences.setUserName(String.format("%s %s", vkApiUser.first_name, vkApiUser.last_name));
                    AppPreferences.setUserAvatarUrl(vkApiUser.photo_200);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //  Override this methods to do needed
    //  after user sign in/out
    public void doAfterSignIn() {
    }

    public void doAfterSignOut() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
