package com.geekhub.choosehelper.screens.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.utils.Prefs;

import butterknife.Bind;
import butterknife.OnClick;

public class SignInActivity extends BaseSignInActivity {

    @Bind(R.id.et_sign_in_email)
    EditText mEtSignInEmail;

    @Bind(R.id.et_sign_in_password)
    EditText mEtSignInPassword;

    private String mEmail;

    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        if (Prefs.getLoggedType() != Prefs.NOT_LOGIN) {
            startMainActivity();
        }
    }

    @OnClick({R.id.btn_sign_in, R.id.tv_forgot_password, R.id.sign_in_google, R.id.sign_in_facebook, R.id.tv_create_an_account})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sign_in:
                mEmail = mEtSignInEmail.getText().toString();
                mPassword = mEtSignInPassword.getText().toString();
                loginEmailPassword(mEmail, mPassword);
                break;
            case R.id.tv_forgot_password:
                //TODO forgot password
                break;
            case R.id.sign_in_facebook:
                facebookLogin();
                break;
            case R.id.sign_in_google:
                googleLogin();
                break;
            case R.id.tv_create_an_account:
                Intent intentSignUp = new Intent(SignInActivity.this, SignUpActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(),
                        R.anim.slide_in_to_left, R.anim.do_nothing).toBundle();
                startActivity(intentSignUp, bndlanimation);
                break;
        }
    }

}
