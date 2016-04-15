package com.geekhub.choosehelper.screens.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.network.NetworkUser;
import com.geekhub.choosehelper.utils.AmazonUtils;
import com.geekhub.choosehelper.utils.ImageUtils;
import com.geekhub.choosehelper.utils.ModelConverter;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.Utils;
import com.geekhub.choosehelper.utils.db.DbUsersManager;
import com.geekhub.choosehelper.utils.firebase.FirebaseConstants;
import com.geekhub.choosehelper.utils.firebase.FirebaseUsersManager;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

public class SignUpActivity extends BaseSignInActivity {

    public static final String LOG_TAG = SignUpActivity.class.getSimpleName() + "logs";

    private static final int RC_GALLERY = 1;
    private static final int RC_CAMERA = 2;

    @Bind(R.id.iv_sign_up_photo)
    ImageView mIvAvatar;

    @Bind(R.id.iv_sign_up_photo_load)
    ImageView mIvAvatarLoad;

    @Bind(R.id.et_sign_up_full_name)
    EditText mEtSignUpFullName;

    @Bind(R.id.et_sign_up_email)
    EditText mEtSignUpEmail;

    @Bind(R.id.et_sign_up_password)
    EditText mEtSignUpPassword;

    @Bind(R.id.et_sign_up_repeat_password)
    EditText mEtSignUpRepeatPassword;

    @Bind(R.id.tv_sign_up_photo_label)
    TextView mTvPhotoLabel;

    private String mFullName;
    private String mEmail;
    private String mPassword;
    private String mRepeatPassword;

    private String mAvatarUrl;
    private String mFilePath;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    @OnClick({R.id.iv_sign_up_photo, R.id.btn_sign_up, R.id.tv_already_have_an_account})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_sign_up_photo:
                Utils.showPhotoPickerDialog(SignUpActivity.this, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Intent galleryIntent = new Intent();
                            galleryIntent.setType("image/*");
                            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(galleryIntent,
                                    getString(R.string.dialog_photo_avatar)), RC_GALLERY);
                            break;
                        case 1:
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(cameraIntent, RC_CAMERA);
                            }
                            break;
                    }
                });
                break;
            case R.id.btn_sign_up:
                mFullName = String.valueOf(mEtSignUpFullName.getText());
                mEmail = String.valueOf(mEtSignUpEmail.getText());
                mPassword = String.valueOf(mEtSignUpPassword.getText());
                mRepeatPassword = String.valueOf(mEtSignUpRepeatPassword.getText());
                if (mFullName.equals("") || mEmail.equals("") || mPassword.equals("") || mRepeatPassword.equals("")) {
                    Utils.showMessage(getApplicationContext(), getString(R.string.toast_empty_fields));
                } else if (!mPassword.equals(mRepeatPassword)) {
                    Utils.showMessage(getApplicationContext(), getString(R.string.toast_different_passwords));
                } else {
                    signUp();
                }
                break;
            case R.id.tv_already_have_an_account:
                onBackPressed();
                overridePendingTransition(R.anim.do_nothing, R.anim.hide_to_right);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        SignUpActivity.this.overridePendingTransition(R.anim.slide_in_to_right, R.anim.do_nothing);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri avatarUri;
            switch (requestCode) {
                case RC_GALLERY:
                    avatarUri = data.getData();
                    try {
                        mFilePath = ImageUtils.getFilePath(getApplicationContext(), avatarUri);
                        mIvAvatarLoad.setVisibility(View.VISIBLE);
                        mTvPhotoLabel.setVisibility(View.GONE);
                        ImageUtils.loadCircleImage(mIvAvatarLoad, mFilePath);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        //TODO toast exception
                    }
                    break;
                case RC_CAMERA:
                    avatarUri = ImageUtils.getPhotoUri(getApplicationContext(),
                            (Bitmap) data.getExtras().get("data"));
                    try {
                        mFilePath = ImageUtils.getFilePath(getApplicationContext(), avatarUri);
                        mIvAvatarLoad.setVisibility(View.VISIBLE);
                        mTvPhotoLabel.setVisibility(View.GONE);
                        ImageUtils.loadCircleImage(mIvAvatarLoad, mFilePath);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        //TODO toast exception
                    }
                    break;
            }
        }
    }

    private void signUp() {
        showProgressDialog();
        Firebase firebase = new Firebase(FirebaseConstants.FB_REF_MAIN);
        firebase.createUser(mEmail, mPassword, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> stringObjectMap) {

                /** save user's id to prefs **/
                String uid = String.valueOf(stringObjectMap.get("uid"));
                Prefs.setLoggedType(Prefs.FIREBASE_LOGIN);
                Prefs.setUserId(uid);

                /** save user avatar if user pick it **/
                if (mFilePath != null) {
                    File file = new File(mFilePath);
                    String fileName = file.getName();
                    TransferObserver transferObserver = AmazonUtils
                            .getTransferUtility(getApplicationContext())
                            .upload(AmazonUtils.BUCKET_NAME + AmazonUtils.FOLDER_AVATARS,
                                    uid + "-" + fileName,
                                    file);
                    mAvatarUrl = AmazonUtils.BASE_URL + AmazonUtils.FOLDER_AVATARS + "/"
                            + uid + "-" + fileName;
                    AmazonUtils.uploadImage(transferObserver);
                }

                /** save user to firebase and database **/
                NetworkUser networkUser = new NetworkUser(mEmail,
                        mFullName,
                        mAvatarUrl);
                DbUsersManager.saveUser(ModelConverter.convertToUser(networkUser, uid));
                FirebaseUsersManager.saveUserToFirebase(networkUser);

                /** hide progress dialog and start main activity **/
                hideProgressDialog();
                startMainActivity();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                hideProgressDialog();
                Utils.showMessage(getApplicationContext(), firebaseError.getMessage());
                Log.i(LOG_TAG, "onError! Code: " + firebaseError.getCode() + "Message: "
                        + firebaseError.getMessage() + "Details: " + firebaseError.getDetails());
            }
        });
    }

    /**
     * methods for show progress
     **/
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

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}