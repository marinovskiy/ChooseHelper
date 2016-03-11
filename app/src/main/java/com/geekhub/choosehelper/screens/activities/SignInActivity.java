package com.geekhub.choosehelper.screens.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.geekhub.choosehelper.R;

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
        setContentView(R.layout.activity_login);
    }

    @OnClick({R.id.btn_go_sign_up, R.id.btn_sign_in, R.id.btn_sign_in_skip_login, R.id.btn_sign_in_google, R.id.btn_sign_in_facebook})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_go_sign_up:
                Intent intentSignUp = new Intent(SignInActivity.this, SignUpActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(),
                        R.anim.slide_in_to_left, R.anim.do_nothing).toBundle();
                startActivity(intentSignUp, bndlanimation);
                break;
            case R.id.btn_sign_in:
                mEmail = mEtSignInEmail.getText().toString();
                mPassword = mEtSignInPassword.getText().toString();
                loginEmailPassword(mEmail, mPassword);
                break;
            case R.id.btn_sign_in_skip_login:
                startMainActivity();
                break;
            case R.id.btn_sign_in_google:
                googleLogin();
                break;
            case R.id.btn_sign_in_facebook:
                facebookLogin();
                break;
        }
    }

}
