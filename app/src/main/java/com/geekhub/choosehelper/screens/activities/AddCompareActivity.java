package com.geekhub.choosehelper.screens.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.network.NetworkVariant;
import com.geekhub.choosehelper.screens.fragments.AllComparesFragment;
import com.geekhub.choosehelper.utils.AmazonUtils;
import com.geekhub.choosehelper.utils.ImageUtils;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.Utils;
import com.geekhub.choosehelper.utils.firebase.FirebaseComparesManager;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class AddCompareActivity extends BaseSignInActivity {

    public static final int RC_GALLERY_FIRST = 1;
    public static final int RC_CAMERA_FIRST = 2;
    public static final int RC_GALLERY_SECOND = 3;
    public static final int RC_CAMERA_SECOND = 4;

    @Bind(R.id.toolbar_add_compare)
    Toolbar mToolbar;

    @Bind(R.id.toolbar_shadow_add_compare)
    View mToolbarShadow;

    @Bind(R.id.add_compare_et_question)
    EditText mAddCompareEtQuestion;

    @Bind(R.id.add_compare_et_first_variant)
    EditText mAddCompareEtVariantOne;

    @Bind(R.id.add_compare_first_img)
    ImageView mAddCompareImgOne;

    @Bind(R.id.add_compare_et_second_variant)
    EditText mAddCompareEtVariantTwo;

    @Bind(R.id.add_compare_second_img)
    ImageView mAddCompareImgTwo;

    @Bind(R.id.add_compare_category_spinner)
    AppCompatSpinner mCategoriesSpinner;

    private String mQuestion;
    private String mCategory;
    private String mFirstVariant;
    private String mSecondVariant;
    private String mFirstImageUrl;
    private String mSecondImageUrl;

    private String mFirstImagePath;
    private String mSecondImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_compare);
        setupToolbar();
    }

    @OnClick({R.id.add_compare_first_img, R.id.add_compare_second_img})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_compare_first_img:
                Utils.showPhotoPickerDialog(AddCompareActivity.this, (dialog, which) -> {
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
            case R.id.add_compare_second_img:
                Utils.showPhotoPickerDialog(AddCompareActivity.this, (dialog, which) -> {
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
                        ImageUtils.loadImage(mAddCompareImgOne, mFirstImagePath);
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
                        ImageUtils.loadImage(mAddCompareImgOne, mFirstImagePath);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        //TODO toast exception
                    }
                    break;
                case RC_GALLERY_SECOND:
                    avatarUri = data.getData();
                    try {
                        mSecondImagePath = ImageUtils.getFilePath(getApplicationContext(), avatarUri);
                        ImageUtils.loadImage(mAddCompareImgTwo, mSecondImagePath);
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
                        ImageUtils.loadImage(mAddCompareImgTwo, mSecondImagePath);
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
                mQuestion = mAddCompareEtQuestion.getText().toString();
                mFirstVariant = mAddCompareEtVariantOne.getText().toString();
                mSecondVariant = mAddCompareEtVariantTwo.getText().toString();
                if (mQuestion.equals("") || mFirstVariant.equals("") || mSecondVariant.equals("")) {
                    Toast.makeText(this, R.string.toast_empty_fields, Toast.LENGTH_SHORT).show();
                } else {
                    addCompare();
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
                    R.drawable.icon_back));
            mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbarShadow.setVisibility(View.GONE);
        }
    }

    private void addCompare() {

        mCategory = mCategoriesSpinner.getSelectedItem().toString();

        /** check if user pick images. If he didn't - will be using standard image **/
        if (mFirstImagePath != null) {
            mFirstImageUrl = getUrlAndStartUpload(mFirstImagePath);
        }
        if (mSecondImagePath != null) {
            mSecondImageUrl = getUrlAndStartUpload(mSecondImagePath);
        }

        /** create variants list of compare **/
        List<NetworkVariant> variants = new ArrayList<>();
        variants.add(new NetworkVariant(mFirstImageUrl, mFirstVariant));
        variants.add(new NetworkVariant(mSecondImageUrl, mSecondVariant));

        /** save new compare to firebase **/
        FirebaseComparesManager.addCompare(
                Prefs.getUserId(),
                mQuestion,
                mCategory,
                variants,
                -1 * System.currentTimeMillis());

        /** when return to all compares auto load new **/
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