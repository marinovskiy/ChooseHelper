package com.geekhub.choosehelper.screens.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.utils.Prefs;

import butterknife.Bind;
import butterknife.OnClick;

public class SignInActivity extends BaseSignInActivity {

    @Bind(R.id.sign_in_et_email)
    EditText mEtEmail;

    @Bind(R.id.sign_in_et_password)
    EditText mEtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        if (Prefs.getLoggedType() != Prefs.ACCOUNT_NONE) {
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

    @OnClick(R.id.sign_in_btn)
    public void signInApp() {
        String email = String.valueOf(mEtEmail.getText());
        String password = String.valueOf(mEtPassword.getText());
        if (email.equals("") || password.equals("")) {
            Toast.makeText(SignInActivity.this,
                    R.string.toast_empty_fields,
                    Toast.LENGTH_SHORT).show();
        } else {
            signInViaAppAccount(email, password);
        }
    }

    @Override
    public void doAfterSignIn() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
