package com.geekhub.choosehelper.screens.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.User;
import com.geekhub.choosehelper.models.network.NetworkCompare;
import com.geekhub.choosehelper.utils.DateUtils;
import com.geekhub.choosehelper.utils.ImageUtils;

import butterknife.Bind;

public class PreviewFragment extends BaseFragment {

    @Bind(R.id.preview_iv_avatar)
    ImageView mIvAvatar;

    @Bind(R.id.preview_tv_author)
    TextView mTvAuthor;

    @Bind(R.id.preview_tv_date)
    TextView mTvDate;

    @Bind(R.id.preview_tv_status)
    TextView mTvStatus;

    @Bind(R.id.preview_tv_question)
    TextView mTvQuestion;

    @Bind(R.id.preview_iv_first_image)
    ImageView mIvFirstImage;

    @Bind(R.id.preview_tv_first_variant)
    TextView mTvFirstVariant;

    @Bind(R.id.preview_iv_second_image)
    ImageView mIvSecondImage;

    @Bind(R.id.preview_tv_second_variant)
    TextView mTvSecondVariant;

    @Bind(R.id.preview_tv_category)
    TextView mTvCategory;

    private static User sAuthor;
    private static NetworkCompare sNewCompare;

    public static PreviewFragment newInstance(User author, NetworkCompare newCompare) {
        sAuthor = author;
        sNewCompare = newCompare;
        return new PreviewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview_compare, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (sAuthor.getPhotoUrl() != null) {
            ImageUtils.loadCircleImage(mIvAvatar, sAuthor.getPhotoUrl());
        } else {
            mIvAvatar.setImageDrawable(ContextCompat.getDrawable(getContext(),
                    R.drawable.icon_no_avatar));
        }

        if (sNewCompare.getVariants().get(0).getImageUrl() != null) {
            ImageUtils.loadImage(mIvFirstImage, sNewCompare.getVariants().get(0).getImageUrl());
        }

        if (sNewCompare.getVariants().get(1).getImageUrl() != null) {
            ImageUtils.loadImage(mIvSecondImage, sNewCompare.getVariants().get(1).getImageUrl());
        }

        mTvAuthor.setText(sAuthor.getFullName());
        mTvDate.setText(DateUtils.convertDateTime(sNewCompare.getDate()));
        mTvStatus.setText(getString(R.string.status_open));
        mTvQuestion.setText(sNewCompare.getQuestion());
        mTvFirstVariant.setText(sNewCompare.getVariants().get(0).getDescription());
        mTvSecondVariant.setText(sNewCompare.getVariants().get(1).getDescription());
        mTvCategory.setText(sNewCompare.getCategory());
    }
}