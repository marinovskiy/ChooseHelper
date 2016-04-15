package com.geekhub.choosehelper.screens.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.ui.dialogs.DialogResetPassword;
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
    }

    @OnClick({R.id.sign_in_btn_login, R.id.sign_in_tv_forgot_password, R.id.sign_in_google,
            R.id.sign_in_facebook, R.id.tv_create_an_account})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_btn_login:
                String email = mEtSignInEmail.getText().toString();
                String password = mEtSignInPassword.getText().toString();

                if (email.equals("") || password.equals("")) {
                    Utils.showMessage(getApplicationContext(), getString(R.string.toast_empty_fields));
                } else {
                    loginEmailPassword(email, password);
                }
                break;
            case R.id.sign_in_tv_forgot_password:
                DialogResetPassword dialogResetPassword = DialogResetPassword.newInstance();
                dialogResetPassword.show(getSupportFragmentManager(),
                        DialogResetPassword.class.getSimpleName());
                break;
            case R.id.sign_in_facebook:
                facebookLogin();
                break;
            case R.id.sign_in_google:
                googleLogin();
                break;
            case R.id.tv_create_an_account:
                Intent intentSignUp = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intentSignUp);
                overridePendingTransition(R.anim.slide_in_to_left, R.anim.do_nothing);
                break;
        }
    }
}