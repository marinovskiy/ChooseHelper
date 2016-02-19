package com.geekhub.choosehelper.screens.activities;


import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.geekhub.choosehelper.R;

public class ProfileActivity extends AppCompatActivity {

    private TextView mTvUsername;
    private TextView mTvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mTvUsername = (TextView) findViewById(R.id.profile_tv_username);
        mTvEmail = (TextView) findViewById(R.id.profile_tv_email);

        Intent mIntentData = getIntent();

        if (mIntentData != null) {
            String email = mIntentData.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            mTvEmail.setText(email);
            Log.d("TEST", "N: " + email);
        }
    }
}
