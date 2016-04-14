package com.geekhub.choosehelper.screens.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.utils.ImageUtils;

import butterknife.Bind;

public class ImageViewFragment extends BaseFragment {

    private static final String BUNDLE_KEY_IMAGE_URL = "bundle_key_imamge_url";
    private static final String BUNDLE_KEY_LIKES = "bundle_key_likes";

    @Bind(R.id.view_image_iv_image)
    ImageView mImageView;

    @Bind(R.id.view_image_ch_likes)
    CheckBox mChLikes;

    private String mImageUrl;
    private int mLikes;

    public static ImageViewFragment newInstance(String imageUrl, String likes) {
        ImageViewFragment imageViewFragment = new ImageViewFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_KEY_IMAGE_URL, imageUrl);
        args.putInt(BUNDLE_KEY_LIKES, Integer.parseInt(likes));
        imageViewFragment.setArguments(args);
        return imageViewFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageUrl = getArguments().getString(BUNDLE_KEY_IMAGE_URL);
            mLikes = getArguments().getInt(BUNDLE_KEY_LIKES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ImageUtils.loadImage(mImageView, mImageUrl);
        mChLikes.setText(String.valueOf(mLikes));
    }
}
