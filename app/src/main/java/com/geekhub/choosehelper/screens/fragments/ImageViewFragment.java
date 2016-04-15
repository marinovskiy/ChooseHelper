package com.geekhub.choosehelper.screens.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.utils.ImageUtils;

import butterknife.Bind;

public class ImageViewFragment extends BaseFragment {

    private static final String IMAGE_VIEW_BUNDLE_KEY_IMAGE_URL = "bundle_key_image_view_url";

    @Bind(R.id.view_image_iv_image)
    ImageView mImageView;

    @Bind(R.id.view_image_tv_none)
    TextView mTextView;

    private String mImageUrl;

    public static ImageViewFragment newInstance(String imageUrl) {
        ImageViewFragment imageViewFragment = new ImageViewFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE_VIEW_BUNDLE_KEY_IMAGE_URL, imageUrl);
        imageViewFragment.setArguments(args);
        return imageViewFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageUrl = getArguments().getString(IMAGE_VIEW_BUNDLE_KEY_IMAGE_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mImageUrl != null) {
            ImageUtils.loadImage(mImageView, mImageUrl);
        } else {
            mImageView.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
        }
    }
}