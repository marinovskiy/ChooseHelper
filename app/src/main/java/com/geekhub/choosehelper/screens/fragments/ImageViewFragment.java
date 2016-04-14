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

    private static final String IMAGE_VIEW_BUNDLE_KEY_IMAGE_URL = "bundle_key_image_view_url";
    private static final String IMAGE_VIEW_BUNDLE_KEY_LIKES = "bundle_key_image_view_likes";
    private static final String IMAGE_VIEW_BUNDLE_KEY_LIKED_VARIANT = "bundle_key_image_view_liked_variant";

    @Bind(R.id.view_image_iv_image)
    ImageView mImageView;

    @Bind(R.id.view_image_ch_likes)
    CheckBox mChLikes;

    private String mImageUrl;
    private int mLikes;
    private int mLikedVariant;

    public static ImageViewFragment newInstance(String imageUrl, String likes, int likedVariant) {
        ImageViewFragment imageViewFragment = new ImageViewFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE_VIEW_BUNDLE_KEY_IMAGE_URL, imageUrl);
        args.putInt(IMAGE_VIEW_BUNDLE_KEY_LIKES, Integer.parseInt(likes));
        args.putInt(IMAGE_VIEW_BUNDLE_KEY_LIKED_VARIANT, likedVariant);
        imageViewFragment.setArguments(args);
        return imageViewFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageUrl = getArguments().getString(IMAGE_VIEW_BUNDLE_KEY_IMAGE_URL);
            mLikes = getArguments().getInt(IMAGE_VIEW_BUNDLE_KEY_LIKES);
            mLikedVariant = getArguments().getInt(IMAGE_VIEW_BUNDLE_KEY_LIKED_VARIANT);
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
