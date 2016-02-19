package com.geekhub.choosehelper.screens.authentication;

import android.accounts.Account;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.geekhub.choosehelper.screens.entities.UserProfile;
import com.geekhub.choosehelper.screens.keys.Key;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.plus.Plus;

import java.io.IOException;


public class GooglePlusProfile extends AsyncTask<Account, Void, UserProfile> {

    private static final String SCOPE = "oauth2:" + Plus.SCOPE_PLUS_LOGIN;

    private Activity mActivity;
    private Account mAccount;

    public GooglePlusProfile(Activity activity, Account account) {
        this.mActivity = activity;
        this.mAccount = account;
    }

    @Override
    protected UserProfile doInBackground(Account... params) {
        UserProfile userProfile = null;
        try {
            String token = GoogleAuthUtil.getToken(mActivity, mAccount, SCOPE); // PROBLEM. Do nothing just freeze
            if (token != null) {
                userProfile = new UserProfile();

                //  Set user profile data
                Log.d(Key.LOG_TAG, "TOKEN: " + token);
            }

            // LOG
            Log.d(Key.LOG_TAG, "Done but token = null");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (GoogleAuthException e) {
            e.printStackTrace();
        }
        return userProfile;
    }

    @Override
    protected void onPostExecute(UserProfile userProfile) {
        super.onPostExecute(userProfile);

    }
}
