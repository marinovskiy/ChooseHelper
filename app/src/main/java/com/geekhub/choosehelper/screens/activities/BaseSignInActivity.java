package com.geekhub.choosehelper.screens.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.network.NetworkUser;
import com.geekhub.choosehelper.utils.ModelConverter;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.Utils;
import com.geekhub.choosehelper.utils.db.DbComparesManager;
import com.geekhub.choosehelper.utils.db.DbUsersManager;
import com.geekhub.choosehelper.utils.firebase.FirebaseConstants;
import com.geekhub.choosehelper.utils.firebase.FirebaseUsersManager;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.util.Arrays;

import butterknife.ButterKnife;

public class BaseSignInActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    /**
     * CONSTANTS
     **/
    private static final String LOGIN_TYPE_FACEBOOK = "facebook";
    private static final String LOGIN_TYPE_PASSWORD = "password";
    private static final String LOGIN_TYPE_GOOGLE = "google";

    /**
     * FACEBOOK
     **/
    private CallbackManager mFacebookCallbackManager;
    private AccessTokenTracker mFacebookAccessTokenTracker;
    private String[] mFacebookPermissions = {
            "public_profile",
            "email",
            "user_about_me",
            "user_birthday",
            "user_location"
    };

    /**
     * GOOGLE PLUS
     **/
    private static final int RC_GOOGLE_LOGIN = 1;
    private GoogleApiClient mGoogleApiClient;

    /**
     * GENERAL
     **/
    private Firebase mFirebase;
    private AuthData mAuthData;
    private Firebase.AuthStateListener mAuthStateListener;
    private ProgressDialog mProgressDialog;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        /** FACEBOOK **/
        mFacebookCallbackManager = CallbackManager.Factory.create();
        mFacebookAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                BaseSignInActivity.this.onFacebookAccessTokenChange(currentAccessToken);
            }
        };

        /** GOOGLE PLUS **/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        /** GENERAL **/
        mFirebase = new Firebase(FirebaseConstants.FB_REF_MAIN);
        mAuthStateListener = authData -> {
            if (authData != null) {
                setAuthenticatedUser(authData);
            } else {
                logout();
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFacebookAccessTokenTracker != null) {
            mFacebookAccessTokenTracker.stopTracking();
        }
        mFirebase.removeAuthStateListener(mAuthStateListener);
        dismissProgressDialog();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_LOGIN) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi
                    .getSignInResultFromIntent(data);
            if (googleSignInResult.isSuccess()) {
                showProgressDialog();
                GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
                String email = null;
                if (googleSignInAccount != null) {
                    email = googleSignInAccount.getEmail();
                }
                getGoogleOAuthTokenAndLogin(email);
            }
        } else {
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * GOOGLE PLUS
     **/
    protected void googleLogin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_LOGIN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, R.string.toast_gp_sign_in_error, Toast.LENGTH_SHORT).show();
    }

    private void getGoogleOAuthTokenAndLogin(final String emailAddress) {

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

            String errorMessage = null;

            @Override
            protected String doInBackground(Void... params) {

                String token = null;

                try {
                    String scope = "oauth2:profile email";
                    token = GoogleAuthUtil.getToken(BaseSignInActivity.this, emailAddress, scope);
                } catch (IOException transientEx) {
                    /* network or server error */
                    Toast.makeText(getApplicationContext(), R.string.toast_gp_sign_in_error,
                            Toast.LENGTH_SHORT).show();
                    errorMessage = "Network error: " + transientEx.getMessage();
                } catch (GoogleAuthException authEx) {
                    /* The call is not ever expected to succeed assuming you have already verified that
                     * Google Play services is installed. */
                    errorMessage = "Error authenticating with Google: " + authEx.getLocalizedMessage();
                }

                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                if (token != null) {
                    mFirebase.authWithOAuthToken(LOGIN_TYPE_GOOGLE, token,
                            new AuthResultHandler(LOGIN_TYPE_GOOGLE));
                } else if (errorMessage != null) {
                    hideProgressDialog();
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        };
        task.execute();
    }

    /**
     * FACEBOOK
     **/
    protected void facebookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList(mFacebookPermissions));
        LoginManager.getInstance().registerCallback(mFacebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        showProgressDialog();
                    }

                    @Override
                    public void onCancel() {
                        hideProgressDialog();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        hideProgressDialog();
                    }
                });
    }

    private void onFacebookAccessTokenChange(AccessToken token) {
        if (token != null) {
            mFirebase.authWithOAuthToken(LOGIN_TYPE_FACEBOOK, token.getToken(),
                    new AuthResultHandler(LOGIN_TYPE_FACEBOOK));
        } else {
            if (this.mAuthData != null && this.mAuthData.getProvider().equals(LOGIN_TYPE_FACEBOOK)) {
                mFirebase.unauth();
            }
        }
    }

    /**
     * GENERAL
     **/
    protected void loginEmailPassword(String email, String password) {
        if (email.equals("") || password.equals("")) {
            Toast.makeText(getApplicationContext(), R.string.toast_empty_fields,
                    Toast.LENGTH_SHORT).show();
        } else {
            showProgressDialog();
            mFirebase.authWithPassword(email, password, new AuthResultHandler(LOGIN_TYPE_PASSWORD));
        }
    }

    private class AuthResultHandler implements Firebase.AuthResultHandler {

        private final String provider;

        public AuthResultHandler(String provider) {
            this.provider = provider;
        }

        @Override
        public void onAuthenticated(AuthData authData) {
            switch (provider) {
                case LOGIN_TYPE_FACEBOOK:
                    Prefs.setLoggedType(Prefs.FACEBOOK_LOGIN);
                    break;
                case LOGIN_TYPE_GOOGLE:
                    Prefs.setLoggedType(Prefs.GOOGLE_LOGIN);
                    break;
                case LOGIN_TYPE_PASSWORD:
                    Prefs.setLoggedType(Prefs.FIREBASE_LOGIN);
                    break;
            }
            setAuthenticatedUser(authData);
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            hideProgressDialog();
            Toast.makeText(getApplicationContext(), firebaseError.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void setAuthenticatedUser(AuthData authData) {
        if (authData != null) {
            Prefs.setUserId(authData.getUid());
            int loggedType = Prefs.getLoggedType();
            if (loggedType == Prefs.GOOGLE_LOGIN || loggedType == Prefs.FACEBOOK_LOGIN) {
                NetworkUser networkUser = new NetworkUser(
                        authData.getProviderData().get("email").toString(),
                        authData.getProviderData().get("displayName").toString(),
                        authData.getProviderData().get("profileImageURL").toString()
                );
                DbUsersManager.saveUser(ModelConverter.convertToUser(networkUser, Prefs.getUserId()));
                FirebaseUsersManager.saveUserToFirebase(networkUser);
                startMainActivity();
            } else if (loggedType == Prefs.FIREBASE_LOGIN) {
                Firebase firebase = new Firebase(FirebaseConstants.FB_REF_MAIN)
                        .child(FirebaseConstants.FB_REF_USERS)
                        .child(Prefs.getUserId());
                firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        NetworkUser networkUser = dataSnapshot.getValue(NetworkUser.class);
                        DbUsersManager.saveUser(ModelConverter.convertToUser(networkUser, Prefs.getUserId()));
                        startMainActivity();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        // TODO toast or dialog of exception
                    }
                });
            }
        }
        this.mAuthData = authData;
        //startMainActivity();
    }

    protected void logout() {
        mFirebase.unauth();
        if (Prefs.getLoggedType() == Prefs.FACEBOOK_LOGIN) {
            LoginManager.getInstance().logOut();
        } else if (Prefs.getLoggedType() == Prefs.GOOGLE_LOGIN) {
            if (mGoogleApiClient.isConnected()) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            }
        }
        DbComparesManager.clearCompares();
        Prefs.setLoggedType(Prefs.NOT_LOGIN);
    }

    /**
     * OTHER
     **/
    protected void startMainActivity() {
        Intent intentMain = new Intent(BaseSignInActivity.this, MainActivity.class);
        intentMain.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intentMain);
        finish();
    }

    protected void startSignInActivity() {
        Intent intentSignIn = new Intent(BaseSignInActivity.this, SignInActivity.class);
        intentSignIn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentSignIn);
        finish();
    }

    /**
     * METHODS FOR SHOW PROGRESS
     **/
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.pd_msg_authenticating));
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
