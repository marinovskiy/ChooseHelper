package com.geekhub.choosehelper.screens.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.network.NetworkUser;
import com.geekhub.choosehelper.utils.ModelConverter;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.Utils;
import com.geekhub.choosehelper.utils.db.DbUsersManager;
import com.geekhub.choosehelper.utils.firebase.FirebaseUsersManager;

import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

public class SignUpActivity extends BaseSignInActivity {

    private static final String LOG_TAG = SignUpActivity.class.getSimpleName();

    @Bind(R.id.et_sign_up_full_name)
    EditText mEtSignUpFullName;

    @Bind(R.id.et_sign_up_email)
    EditText mEtSignUpEmail;

    @Bind(R.id.et_sign_up_password)
    EditText mEtSignUpPassword;

    @Bind(R.id.et_sign_up_repeat_password)
    EditText mEtSignUpRepeatPassword;

    private String mFullName;

    private String mEmail;

    private String mPassword;

    private String mRepeatPassword;

    private String mImage;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    @OnClick({R.id.iv_sign_up_photo, R.id.btn_sign_up_go_login, R.id.btn_sign_up, R.id.btn_sign_up_skip_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_sign_up_photo:
                //Utils.showPhotoPickerDialog(getApplicationContext());
                break;
            case R.id.btn_sign_up_go_login:
                onBackPressed();
                break;
            case R.id.btn_sign_up:
                mFullName = String.valueOf(mEtSignUpFullName.getText());
                mEmail = String.valueOf(mEtSignUpEmail.getText());
                mPassword = String.valueOf(mEtSignUpPassword.getText());
                mRepeatPassword = String.valueOf(mEtSignUpRepeatPassword.getText());
                if (mFullName.equals("") || mEmail.equals("") || mPassword.equals("")) {
                    Toast.makeText(SignUpActivity.this, "You did not fill all fields", Toast.LENGTH_SHORT).show();
                } else if (!mPassword.equals(mRepeatPassword)) {
                    Toast.makeText(SignUpActivity.this, "Passwords are different", Toast.LENGTH_SHORT).show();
                } else {
                    signUp();
                }
                break;
            case R.id.btn_sign_up_skip_login:
                Prefs.setLoggedType(Prefs.NOT_LOGIN);
                startMainActivity();
                break;
        }
    }

    private void signUp() {
        showProgressDialog();
        Firebase firebase = new Firebase("https://choosehelper.firebaseio.com");
        firebase.createUser(mEmail, mPassword, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> stringObjectMap) {
                Log.i(LOG_TAG, "onSuccess");
                Prefs.setUserId(String.valueOf(stringObjectMap.get("uid")));
                mImage = Utils.convertBitmapToString(BitmapFactory.decodeResource(getResources(),
                        R.drawable.test_img));
                NetworkUser networkUser = new NetworkUser(mEmail,
                        mFullName,
                        mImage);
                DbUsersManager.saveUser(ModelConverter.convertToUser(networkUser));
                FirebaseUsersManager.saveUserToFirebase(networkUser);
                hideProgressDialog();
                startMainActivity();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                hideProgressDialog();
                Utils.showErrorMessage(getApplicationContext(), firebaseError.getMessage());
                Log.i(LOG_TAG, "onError! Code: " + firebaseError.getCode() + "Message: "
                        + firebaseError.getMessage() + "Details: " + firebaseError.getDetails());
            }
        });
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.pd_msg_wait_please));
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

}
