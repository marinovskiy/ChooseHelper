package com.geekhub.choosehelper.screens.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.utils.AuthorizationUtil;
import com.geekhub.choosehelper.utils.Prefs;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
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
        implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final String LOG_TAG = BaseSignInActivity.class.getSimpleName();

    public static final String FIREBASE_BASE_REFERENCE = "https://choosehelper.firebaseio.com/";

    //  Request codes for sign in
    private static final int RC_GOOGLE_SIGN_IN = 9001;
    private static final int RC_VK_SIGN_IN = 10485;

    // Google plus
    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult mConnectionResult;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;

    private String fullName;
    private String photoUrl;

    // Vk
    private String[] mScope = {
            VKScope.FRIENDS,
            VKScope.PHOTOS,
            VKScope.EMAIL,
            VKScope.OFFLINE
    };

    // App account
    protected Firebase mFirebase;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Google plus
        // Init API client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        // App account
        mFirebase = new Firebase(FIREBASE_BASE_REFERENCE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person account = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            String userId = account.getId();
            Prefs.setLoggedType(Prefs.ACCOUNT_GOOGLE_PLUS);
            if (AuthorizationUtil.isExistInFb(userId)) {
                AuthorizationUtil.saveUserFromFb(userId);
            } else {
                AuthorizationUtil.saveNewUser(userId,
                        Plus.AccountApi.getAccountName(mGoogleApiClient),
                        account.getDisplayName(),
                        String.valueOf(account.getImage().getUrl()));
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = connectionResult;

            if (mSignInClicked) {
                resolveSignInError();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_GOOGLE_SIGN_IN:
//                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//                if (result.isSuccess()) {
//                    GoogleSignInAccount account = result.getSignInAccount();
//                    String userId = account.getId();
//                    Prefs.setLoggedType(Prefs.ACCOUNT_GOOGLE_PLUS);
//                    if (AuthorizationUtil.isExistInFb(userId)) {
//                        AuthorizationUtil.saveUserFromFb(userId);
//                    } else {
//                        AuthorizationUtil.saveNewUser(userId,
//                                account.getEmail(),
//                                account.getDisplayName(),
//                                String.valueOf(account.getPhotoUrl()));
//                    }
//                if (responseCode != RESULT_OK) {
//                    mSignInClicked = false;
//                }

                mIntentInProgress = false;

                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }

                doAfterSignIn();
//                } else
//                    Log.d(LOG_TAG, "Sign in failed. Result code: " + resultCode);
                break;
            case RC_VK_SIGN_IN:
                VKCallback<VKAccessToken> callback = new VKCallback<VKAccessToken>() {
                    @Override
                    public void onResult(VKAccessToken res) {
                        Prefs.setLoggedType(Prefs.ACCOUNT_VK);
                        String userId = res.userId;
                        String email = res.email;
                        if (AuthorizationUtil.isExistInFb(userId)) {
                            AuthorizationUtil.saveUserFromFb(userId);
                        } else {
                            setVkUserInfo(userId, email);
                        }
                        doAfterSignIn();
                    }

                    @Override
                    public void onError(VKError error) {
                        Log.d(LOG_TAG, "Sign in failed. Result code: " + error.errorCode +
                                " error reason: " + error.errorReason +
                                " error message: " + error.errorMessage);
                        Toast.makeText(BaseSignInActivity.this,
                                R.string.toast_sign_in_error, Toast.LENGTH_SHORT).show();
                    }
                };
                if (!VKSdk.onActivityResult(requestCode, resultCode, data, callback)) {
                    super.onActivityResult(requestCode, resultCode, data);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void signInViaGoogle() {
//        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//        startActivityForResult(intent, RC_GOOGLE_SIGN_IN);
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_GOOGLE_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    protected void signInViaVk() {
        VKSdk.login(BaseSignInActivity.this, mScope);
    }

    protected void signInViaAppAccount(String email, String password) {
        mFirebase.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                String uId = authData.getUid();
                Prefs.setLoggedType(Prefs.ACCOUNT_APP);
                Prefs.setUserId(uId);
                AuthorizationUtil.saveUserFromFb(uId);
                doAfterSignIn();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                Log.i(LOG_TAG, "onError! Code: " + firebaseError.getCode() + "Message: "
                        + firebaseError.getMessage() + "Details: " + firebaseError.getDetails());
            }
        });
    }

    private void setVkUserInfo(final String id, final String email) {
        VKRequest vkRequest = VKApi.users()
                .get(VKParameters.from(VKApiConst.USER_IDS, id,
                        VKApiConst.FIELDS, getString(R.string.vk_api_photo_200)));

        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    JSONArray jsonArray = response.json.getJSONArray("response");
                    VKApiUser vkApiUser = new VKApiUser(jsonArray.getJSONObject(0));
                    fullName = String.format("%s %s",
                            vkApiUser.first_name,
                            vkApiUser.last_name);
                    photoUrl = vkApiUser.photo_200;
                    AuthorizationUtil.saveNewUser(id,
                            email,
                            fullName,
                            photoUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void signOut() {
        switch (Prefs.getLoggedType()) {
            case Prefs.ACCOUNT_NONE:
                Toast.makeText(this, R.string.toast_sign_out_already, Toast.LENGTH_SHORT).show();

            case Prefs.ACCOUNT_GOOGLE_PLUS: {
//                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(Status status) {
//                        Log.d(LOG_TAG, "Sign out success");
//                    }
//                });
//            }
                if (mGoogleApiClient.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                }
            }
            break;
            case Prefs.ACCOUNT_VK: {
                VKSdk.logout();
            }
            break;
            case Prefs.ACCOUNT_APP: {
                mFirebase.unauth();
            }
            break;
            default:
                break;
        }

        //  Update app preferences
        Prefs.setLoggedType(Prefs.ACCOUNT_NONE);
        doAfterSignOut();
    }

    //  Override this methods to do needed
    //  after user sign in/out
    public void doAfterSignIn() {
    }

    public void doAfterSignOut() {
    }
}
