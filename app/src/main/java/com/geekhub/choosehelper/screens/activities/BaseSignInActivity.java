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
import com.geekhub.choosehelper.models.network.NetworkUser;
import com.geekhub.choosehelper.models.vk.VkUser;
import com.geekhub.choosehelper.utils.AuthorizationUtil;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.firebase.FirebaseUsersManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE).build();

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

        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person account = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            String userId = account.getId();
            Prefs.setUserId(userId);
            NetworkUser newUser = new NetworkUser(
                    Plus.AccountApi.getAccountName(mGoogleApiClient),
                    account.hasDisplayName() ? account.getDisplayName() : "null",
                    account.getImage().hasUrl() ? account.getImage().getUrl() : "null",
                    account.hasBirthday() ? account.getBirthday() : "null",
                    account.hasPlacesLived() ? account.getPlacesLived().get(0).getValue() : "null",
                    account.hasAboutMe() ? account.getAboutMe() : "null"
            );
            AuthorizationUtil.saveUser(newUser);

            Prefs.setLoggedType(Prefs.ACCOUNT_GOOGLE_PLUS);
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
                mIntentInProgress = false;
                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
                doAfterSignIn();
                break;
            case RC_VK_SIGN_IN:
                VKCallback<VKAccessToken> callback = new VKCallback<VKAccessToken>() {
                    @Override
                    public void onResult(VKAccessToken res) {
                        String userId = res.userId;
                        String email = res.email;
                        Prefs.setUserId(userId);
                        getVkUser(userId, email);
                        Prefs.setLoggedType(Prefs.ACCOUNT_VK);
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

    protected void signInViaAppAccount(String email, String password) {
        mFirebase.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                Prefs.setUserId(authData.getUid());
                FirebaseUsersManager.saveUserFromFirebase();
                Prefs.setLoggedType(Prefs.ACCOUNT_APP);
                doAfterSignIn();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                Log.i(LOG_TAG, "onError! Code: " + firebaseError.getCode() + "Message: "
                        + firebaseError.getMessage() + "Details: " + firebaseError.getDetails());
            }
        });
    }

    protected void signInViaGoogle() {
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

    private void getVkUser(String id, String email) {
        VKRequest vkRequest = VKApi.users()
                .get(VKParameters.from(VKApiConst.USER_IDS, id,
                        VKApiConst.FIELDS, "photo_200,bdate,city,country,about"));

        vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    VkUser vkUser = (gson.fromJson(String.valueOf(response.json
                            .getJSONArray("response").getJSONObject(0)), VkUser.class));

                    NetworkUser newUser = new NetworkUser(
                            email,
                            vkUser.getFirstName() + " " + vkUser.getLastName(),
                            vkUser.getImageUrl(),
                            vkUser.getBirthday(),
                            vkUser.getCity().getTitle() + ", " + vkUser.getCountry().getTitle(),
                            vkUser.getAbout()
                    );
                    AuthorizationUtil.saveUser(newUser);

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
