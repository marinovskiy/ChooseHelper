package com.geekhub.choosehelper.screens.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.User;
import com.geekhub.choosehelper.models.network.NetworkCompare;
import com.geekhub.choosehelper.models.network.NetworkVariant;
import com.geekhub.choosehelper.utils.AmazonUtils;
import com.geekhub.choosehelper.utils.ImageUtils;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.Utils;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class AddCompareFragment extends BaseFragment {

    public static final int RC_GALLERY_FIRST = 1;
    public static final int RC_CAMERA_FIRST = 2;
    public static final int RC_GALLERY_SECOND = 3;
    public static final int RC_CAMERA_SECOND = 4;

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

    private String mFirstImagePath;
    private String mSecondImagePath;

    public static AddCompareFragment newInstance() {
        return new AddCompareFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_compare, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFirstImagePath != null) {
            ImageUtils.loadImage(mAddCompareImgOne, mFirstImagePath);
        }
        if (mSecondImagePath != null) {
            ImageUtils.loadImage(mAddCompareImgTwo, mSecondImagePath);
        }
    }

    @OnClick({R.id.add_compare_first_img, R.id.add_compare_second_img})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_compare_first_img:
                Utils.showPhotoPickerDialog(getContext(), (dialog, which) -> {
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
                            if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivityForResult(cameraIntent, RC_CAMERA_FIRST);
                            }
                            break;
                    }
                });
                break;
            case R.id.add_compare_second_img:
                Utils.showPhotoPickerDialog(getContext(), (dialog, which) -> {
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
                            if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivityForResult(cameraIntent, RC_CAMERA_SECOND);
                            }
                            break;
                    }
                });
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri avatarUri;
            switch (requestCode) {
                case RC_GALLERY_FIRST:
                    avatarUri = data.getData();
                    try {
                        mFirstImagePath = ImageUtils.getFilePath(getContext(), avatarUri);
                        ImageUtils.loadImage(mAddCompareImgOne, mFirstImagePath);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        //TODO toast exception
                    }
                    break;
                case RC_CAMERA_FIRST:
                    avatarUri = ImageUtils.getPhotoUri(getContext(),
                            (Bitmap) data.getExtras().get("data"));
                    try {
                        mFirstImagePath = ImageUtils.getFilePath(getContext(), avatarUri);
                        ImageUtils.loadImage(mAddCompareImgOne, mFirstImagePath);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        //TODO toast exception
                    }
                    break;
                case RC_GALLERY_SECOND:
                    avatarUri = data.getData();
                    try {
                        mSecondImagePath = ImageUtils.getFilePath(getContext(), avatarUri);
                        ImageUtils.loadImage(mAddCompareImgTwo, mSecondImagePath);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        //TODO toast exception
                    }
                    break;
                case RC_CAMERA_SECOND:
                    avatarUri = ImageUtils.getPhotoUri(getContext(),
                            (Bitmap) data.getExtras().get("data"));
                    try {
                        mSecondImagePath = ImageUtils.getFilePath(getContext(), avatarUri);
                        ImageUtils.loadImage(mAddCompareImgTwo, mSecondImagePath);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        //TODO toast exception
                    }
                    break;
            }
        }
    }

    public NetworkCompare getNewCompare() {
        NetworkCompare networkCompare = new NetworkCompare();

        String question = mAddCompareEtQuestion.getText().toString();
        String category = mCategoriesSpinner.getSelectedItem().toString();
        String firstVariant = mAddCompareEtVariantOne.getText().toString();
        String secondVariant = mAddCompareEtVariantTwo.getText().toString();

        /** check if user pick images. If he didn't - will be using standard image **/
       /* if (mFirstImagePath != null) {
            mFirstImageUrl = getUrlAndStartUpload(mFirstImagePath);
        }
        if (mSecondImagePath != null) {
            mSecondImageUrl = getUrlAndStartUpload(mSecondImagePath);
        }*/

        /** create variants list of compare **/
        List<NetworkVariant> variants = new ArrayList<>();
        variants.add(new NetworkVariant(mFirstImagePath, firstVariant));
        variants.add(new NetworkVariant(mSecondImagePath, secondVariant));

        networkCompare.setOpen(true);
        networkCompare.setQuestion(question);
        networkCompare.setCategory(category);
        networkCompare.setVariants(variants);
        networkCompare.setUserId(Prefs.getUserId());
        networkCompare.setDate(-1 * System.currentTimeMillis());

        return networkCompare;
    }

    private String getUrlAndStartUpload(String filePath) {
        File file = new File(filePath);
        TransferObserver transferObserver = AmazonUtils
                .getTransferUtility(getContext())
                .upload(AmazonUtils.BUCKET_NAME + AmazonUtils.FOLDER_IMAGES + "/" + Prefs.getUserId(),
                        file.getName(),
                        file);
        AmazonUtils.uploadImage(transferObserver);
        return AmazonUtils.BASE_URL + AmazonUtils.FOLDER_IMAGES + "/"
                + Prefs.getUserId() + "/" + file.getName();
    }

}