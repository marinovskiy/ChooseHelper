package com.geekhub.choosehelper.screens.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.utils.AuthorizationUtil;
import com.geekhub.choosehelper.utils.Prefs;

import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

public class SignUpActivity extends BaseSignInActivity {

    private static final String LOG_TAG = SignUpActivity.class.getSimpleName();

    @Bind(R.id.sign_up_et_full_name)
    EditText mEtFullName;

    @Bind(R.id.sign_up_et_email)
    EditText mEtEmail;

    @Bind(R.id.sign_up_et_password)
    EditText mEtPassword;

    private String mFullName;
    private String mEmail;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    @OnClick(R.id.sign_in_tv)
    public void onClick() {
        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
    }

    @OnClick(R.id.sign_up_btn)
    public void signUpClick() {
        mFullName = String.valueOf(mEtFullName.getText());
        mEmail = String.valueOf(mEtEmail.getText());
        mPassword = String.valueOf(mEtPassword.getText());
        if (mFullName.equals("") || mEmail.equals("") || mPassword.equals("")) {
            Toast.makeText(SignUpActivity.this,
                    R.string.toast_empty_fields,
                    Toast.LENGTH_SHORT).show();
        } else {
            signUp();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onClick();
    }

    @Override
    public void doAfterSignIn() {
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void signUp() {
        mFirebase.createUser(mEmail, mPassword, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                String uId = String.valueOf(result.get("uid"));
                AuthorizationUtil.saveNewUser(uId, mEmail, mFullName, "");
                Prefs.setLoggedType(Prefs.ACCOUNT_APP);
                Prefs.setUserId(uId);
                doAfterSignIn();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Log.i(LOG_TAG, "onError! Code: " + firebaseError.getCode() + "Message: "
                        + firebaseError.getMessage() + "Details: " + firebaseError.getDetails());
            }
        });
    }
}
