package com.geekhub.choosehelper.screens.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.utils.Utils;

import butterknife.Bind;
import butterknife.OnClick;

public class SignInActivity extends BaseSignInActivity {

    @Bind(R.id.sign_in_et_email)
    EditText mEtSignInEmail;

    @Bind(R.id.sign_in_et_password)
    EditText mEtSignInPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        /*if (Prefs.getLoggedType() != Prefs.NOT_LOGIN) {
            startMainActivity();
        }*/
    }

    @OnClick({R.id.sign_in_btn_login, R.id.sign_in_tv_forgot_password, R.id.sign_in_google,
            R.id.sign_in_facebook, R.id.tv_create_an_account})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_btn_login:
                String email = mEtSignInEmail.getText().toString();
                String password = mEtSignInPassword.getText().toString();

                if (email.equals("") || password.equals("")) {
                    Utils.showMessage(getApplicationContext(), "You did not fill all fields");
                } else {
                    loginEmailPassword(email, password);
                }
                break;
            case R.id.sign_in_tv_forgot_password:
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
//                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(),
//                        R.anim.slide_in_to_left, R.anim.do_nothing).toBundle();
//                startActivity(intentSignUp, bndlanimation);
                startActivity(intentSignUp);
                overridePendingTransition(R.anim.slide_in_to_left, R.anim.do_nothing);
                finish();
                break;
        }
    }

}
