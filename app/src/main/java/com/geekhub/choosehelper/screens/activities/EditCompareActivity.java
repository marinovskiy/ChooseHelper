package com.geekhub.choosehelper.screens.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.network.NetworkCompare;
import com.geekhub.choosehelper.screens.fragments.AllComparesFragment;
import com.geekhub.choosehelper.utils.AmazonUtils;
import com.geekhub.choosehelper.utils.ImageUtils;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.Utils;
import com.geekhub.choosehelper.utils.firebase.FirebaseConstants;

import java.io.File;
import java.net.URISyntaxException;

import butterknife.Bind;
import butterknife.OnClick;

public class EditCompareActivity extends BaseSignInActivity {

    /**
     * constants for activity result
     **/
    public static final int RC_GALLERY_FIRST = 1;
    public static final int RC_CAMERA_FIRST = 2;
    public static final int RC_GALLERY_SECOND = 3;
    public static final int RC_CAMERA_SECOND = 4;

    @Bind(R.id.toolbar_edit_compare)
    Toolbar mToolbar;

    @Bind(R.id.toolbar_shadow_edit_compare)
    View mToolbarShadow;

    @Bind(R.id.edit_compare_et_question)
    EditText mEditCompareEtQuestion;

    @Bind(R.id.edit_compare_et_first_variant)
    EditText mEditCompareEtV1;

    @Bind(R.id.edit_compare_first_img)
    ImageView mEditCompareIvV1;

    @Bind(R.id.edit_compare_et_second_variant)
    EditText mEditCompareEtV2;

    @Bind(R.id.edit_compare_second_img)
    ImageView mEditCompareIvV2;

    private String mQuestion;
    private String mFirstVariant;
    private String mSecondVariant;
    private String mFirstImageUrl;
    private String mSecondImageUrl;

    private String mFirstImagePath;
    private String mSecondImagePath;

    private String mCompareId;
    private NetworkCompare mNetworkCompare = new NetworkCompare();

    private Firebase mFirebaseCompare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_compare);
        setupToolbar();

        /** get current compare id and use for take current compare **/
        if (getIntent() != null) {
            mCompareId = getIntent().getStringExtra(DetailsActivity.INTENT_KEY_COMPARE_ID);
            mFirebaseCompare = new Firebase(FirebaseConstants.FB_REF_MAIN)
                    .child(FirebaseConstants.FB_REF_COMPARES)
                    .child(mCompareId);
            getCurrentCompare();
        }
    }

    @OnClick({R.id.edit_compare_first_img, R.id.edit_compare_second_img})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_compare_first_img:
                Utils.showPhotoPickerDialog(EditCompareActivity.this, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Intent galleryIntent = new Intent();
                            galleryIntent.setType("image/*");
                            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(galleryIntent,
                                    getString(R.string.compare_dialog_photo_title)),
                                    RC_GALLERY_FIRST);
                            break;
                        case 1:
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(cameraIntent, RC_CAMERA_FIRST);
                            }
                            break;
                    }
                });
                break;
            case R.id.edit_compare_second_img:
                Utils.showPhotoPickerDialog(EditCompareActivity.this, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Intent galleryIntent = new Intent();
                            galleryIntent.setType("image/*");
                            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(galleryIntent,
                                    getString(R.string.compare_dialog_photo_title)),
                                    RC_GALLERY_SECOND);
                            break;
                        case 1:
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(cameraIntent, RC_CAMERA_SECOND);
                            }
                            break;
                    }
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri avatarUri;
            switch (requestCode) {
                case RC_GALLERY_FIRST:
                    avatarUri = data.getData();
                    try {
                        mFirstImagePath = ImageUtils.getFilePath(getApplicationContext(), avatarUri);
                        ImageUtils.loadImage(mEditCompareIvV1, mFirstImagePath);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        //TODO toast exception
                    }
                    break;
                case RC_CAMERA_FIRST:
                    avatarUri = ImageUtils.getPhotoUri(getApplicationContext(),
                            (Bitmap) data.getExtras().get("data"));
                    try {
                        mFirstImagePath = ImageUtils.getFilePath(getApplicationContext(), avatarUri);
                        ImageUtils.loadImage(mEditCompareIvV1, mFirstImagePath);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        //TODO toast exception
                    }
                    break;
                case RC_GALLERY_SECOND:
                    avatarUri = data.getData();
                    try {
                        mSecondImagePath = ImageUtils.getFilePath(getApplicationContext(), avatarUri);
                        ImageUtils.loadImage(mEditCompareIvV2, mSecondImagePath);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        //TODO toast exception
                    }
                    break;
                case RC_CAMERA_SECOND:
                    avatarUri = ImageUtils.getPhotoUri(getApplicationContext(),
                            (Bitmap) data.getExtras().get("data"));
                    try {
                        mSecondImagePath = ImageUtils.getFilePath(getApplicationContext(), avatarUri);
                        ImageUtils.loadImage(mEditCompareIvV2, mSecondImagePath);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        //TODO toast exception
                    }
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                mQuestion = mEditCompareEtQuestion.getText().toString();
                mFirstVariant = mEditCompareEtV1.getText().toString();
                mSecondVariant = mEditCompareEtV2.getText().toString();
                if (mQuestion.equals("") || mFirstVariant.equals("") || mSecondVariant.equals("")) {
                    Toast.makeText(this, R.string.toast_empty_fields, Toast.LENGTH_SHORT).show();
                } else {
                    updateCompare();
                    finish();
                }
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.icon_cancel));
            mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbarShadow.setVisibility(View.GONE);
        }
    }

    private void getCurrentCompare() {
        mFirebaseCompare.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot compareSnapshot) {
                mNetworkCompare = compareSnapshot.getValue(NetworkCompare.class);
                mFirstImageUrl = mNetworkCompare.getVariants().get(0).getImageUrl();
                mSecondImageUrl = mNetworkCompare.getVariants().get(1).getImageUrl();
                updateUi(mNetworkCompare);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //hideRefreshing(); TODO change to progress bar
                Toast.makeText(getApplicationContext(), "Error! Please, try later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUi(NetworkCompare networkCompare) {
        mEditCompareEtQuestion.setText(networkCompare.getQuestion());
        mEditCompareEtV1.setText(networkCompare.getVariants().get(0).getDescription());
        mEditCompareEtV2.setText(networkCompare.getVariants().get(1).getDescription());

        if (networkCompare.getVariants().get(0).getImageUrl() != null) {
            ImageUtils.loadImage(mEditCompareIvV1, networkCompare.getVariants().get(0).getImageUrl());
        } else {
            mEditCompareIvV1.setImageDrawable(ContextCompat.getDrawable(mEditCompareIvV1.getContext(),
                    R.drawable.standard_photo));
        }
        if (networkCompare.getVariants().get(1).getImageUrl() != null) {
            ImageUtils.loadImage(mEditCompareIvV2, networkCompare.getVariants().get(1).getImageUrl());
        } else {
            mEditCompareIvV2.setImageDrawable(ContextCompat.getDrawable(mEditCompareIvV1.getContext(),
                    R.drawable.standard_photo));
        }
    }

    private void updateCompare() {
        /** check if user pick images. If he didn't - will be using standard image **/
        if (mFirstImagePath != null) {
            mFirstImageUrl = getUrlAndStartUpload(mFirstImagePath);
        }
        if (mSecondImagePath != null) {
            mSecondImageUrl = getUrlAndStartUpload(mSecondImagePath);
        }

        /** update information about compare **/
        mNetworkCompare.setQuestion(mQuestion);
        mNetworkCompare.getVariants().get(0).setDescription(mFirstVariant);
        mNetworkCompare.getVariants().get(1).setDescription(mSecondVariant);
        mNetworkCompare.getVariants().get(0).setImageUrl(mFirstImageUrl);
        mNetworkCompare.getVariants().get(1).setImageUrl(mSecondImageUrl);

        /** update compare in firebase **/
        mFirebaseCompare.setValue(mNetworkCompare);

        /** when return to all compares - auto load updates **/
        AllComparesFragment.sIsNeedToAutoUpdate = true;
    }

    private String getUrlAndStartUpload(String filePath) {
        File file = new File(filePath);
        TransferObserver transferObserver = AmazonUtils
                .getTransferUtility(getApplicationContext())
                .upload(AmazonUtils.BUCKET_NAME + AmazonUtils.FOLDER_IMAGES + "/" + Prefs.getUserId(),
                        file.getName(),
                        file);
        AmazonUtils.uploadImage(transferObserver);
        return AmazonUtils.BASE_URL + AmazonUtils.FOLDER_IMAGES + "/"
                + Prefs.getUserId() + "/" + file.getName();
    }
}
