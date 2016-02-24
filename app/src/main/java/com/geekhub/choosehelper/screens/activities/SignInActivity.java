package com.geekhub.choosehelper.screens.activities;

import android.content.Intent;
import android.os.Bundle;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.utils.AppPreferences;
import com.geekhub.choosehelper.utils.BaseSignInActivity;

import butterknife.OnClick;

public class SignInActivity extends BaseSignInActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        if (AppPreferences.getLoggedType() != AppPreferences.ACCOUNT_NONE) {
            doAfterSignIn();
        }
    }

    @OnClick(R.id.sign_up_tv)
    public void signUp() {
        startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
    }


    @OnClick(R.id.sign_in_btn_google_plus)
    public void signInGooglePlus() {
        signInViaGoogle();
    }

    @OnClick(R.id.sign_in_btn_vk)
    public void signInVk() {
        signInViaVk();
    }


    @Override
    public void doAfterSignIn() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
