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
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.network.NetworkUser;
import com.geekhub.choosehelper.utils.AmazonUtil;
import com.geekhub.choosehelper.utils.ImageUtil;
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
                                    "Select picture for avatar"), RC_GALLERY);
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
                    Toast.makeText(SignUpActivity.this, R.string.toast_empty_fields, Toast.LENGTH_SHORT).show();
                } else if (!mPassword.equals(mRepeatPassword)) {
                    Toast.makeText(SignUpActivity.this, "Passwords are different", Toast.LENGTH_SHORT).show();
                } else {
                    signUp();
                }
                break;
            case R.id.tv_already_have_an_account:
                onBackPressed();
                break;
        }
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
                        mFilePath = ImageUtil.getFilePath(getApplicationContext(), avatarUri);
                        ImageUtil.loadCircleImage(mIvAvatar, mFilePath);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        //TODO toast exception
                    }
                    break;
                case RC_CAMERA:
                    avatarUri = ImageUtil.getPhotoUri(getApplicationContext(),
                            (Bitmap) data.getExtras().get("data"));
                    try {
                        mFilePath = ImageUtil.getFilePath(getApplicationContext(), avatarUri);
                        ImageUtil.loadCircleImage(mIvAvatar, mFilePath);
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
        Firebase firebase = new Firebase(FirebaseConstants.FB_REFERENCE_MAIN);
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
                    TransferObserver transferObserver = AmazonUtil
                            .getTransferUtility(getApplicationContext())
                            .upload(AmazonUtil.BUCKET_NAME + AmazonUtil.FOLDER_AVATARS,
                                    uid + "-" + fileName,
                                    file);
                    mAvatarUrl = AmazonUtil.BASE_URL + AmazonUtil.FOLDER_AVATARS + "/"
                            + uid + "-" + fileName;
                    AmazonUtil.uploadImage(transferObserver);
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
                Utils.showErrorMessage(getApplicationContext(), firebaseError.getMessage());
                Log.i(LOG_TAG, "onError! Code: " + firebaseError.getCode() + "Message: "
                        + firebaseError.getMessage() + "Details: " + firebaseError.getDetails());
            }
        });
    }

    /**
     * Progress dialog
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