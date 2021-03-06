package com.geekhub.choosehelper.ui.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.Compare;
import com.geekhub.choosehelper.ui.listeners.OnImageClickListener;
import com.geekhub.choosehelper.ui.listeners.OnItemClickListener;
import com.geekhub.choosehelper.ui.listeners.OnLikeListListener;
import com.geekhub.choosehelper.utils.DateUtils;
import com.geekhub.choosehelper.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;

public class ComparesAdapter extends RecyclerView.Adapter<ComparesAdapter.ViewHolder> {

    private List<Compare> mCompares = new ArrayList<>();

    private OnItemClickListener mOnItemClickListenerAuthor;
    private OnItemClickListener mOnItemClickListener;
    private OnImageClickListener mOnImageClickListener;
    private OnLikeListListener mOnLikeListListener;

    public ComparesAdapter(List<Compare> compares) {
        mCompares = Realm.getDefaultInstance().copyFromRealm(compares);
    }

    public void updateList(List<Compare> compares) {
        mCompares = Realm.getDefaultInstance().copyFromRealm(compares);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.compare_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindCompare(mCompares.get(position));
    }

    @Override
    public int getItemCount() {
        return mCompares != null ? mCompares.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.compare_card_view)
        CardView mCardView;

        @Bind(R.id.compare_ll_author)
        LinearLayout mLlAuthor;

        @Bind(R.id.compare_iv_avatar)
        ImageView mIvAvatar;

        @Bind(R.id.compare_tv_author)
        TextView mTvAuthor;

        @Bind(R.id.compare_tv_date)
        TextView mTvDate;

        @Bind(R.id.compare_tv_question)
        TextView mTvQuestion;

        @Bind(R.id.compare_iv_first)
        ImageView mIvFirst;

        @Bind(R.id.compare_tv_first)
        TextView mTvFirst;

        @Bind(R.id.compare_ch_like_first)
        CheckBox mChLikeFirst;

        @Bind(R.id.compare_iv_second)
        ImageView mIvSecond;

        @Bind(R.id.compare_tv_second)
        TextView mTvSecond;

        @Bind(R.id.compare_ch_like_second)
        CheckBox mChLikeSecond;

        @Bind(R.id.compare_tv_category)
        TextView mTvCategory;

        @Bind(R.id.compare_tv_status)
        TextView mTvStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            mLlAuthor.setOnClickListener(this);
            mIvFirst.setOnClickListener(this);
            mIvSecond.setOnClickListener(this);
            mChLikeFirst.setOnClickListener(this);
            mChLikeSecond.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == itemView.getId()) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, getAdapterPosition());
                }
            } else if (id == mIvFirst.getId()) {
                if (mOnImageClickListener != null) {
                    mOnImageClickListener.onImageClick(v, getAdapterPosition(), 0);
                }
            } else if (id == mIvSecond.getId()) {
                if (mOnImageClickListener != null) {
                    mOnImageClickListener.onImageClick(v, getAdapterPosition(), 1);
                }
            } else if (id == mChLikeFirst.getId()) {
                if (mOnLikeListListener != null) {
                    updateLikeView(mChLikeFirst, mChLikeSecond);
                    mOnLikeListListener.onLike(mCardView, mChLikeFirst, mChLikeSecond, getAdapterPosition(), 0);
                }
            } else if (id == mChLikeSecond.getId()) {
                if (mOnLikeListListener != null) {
                    updateLikeView(mChLikeSecond, mChLikeFirst);
                    mOnLikeListListener.onLike(mCardView, mChLikeSecond, mChLikeFirst, getAdapterPosition(), 1);
                }
            } else if (id == mLlAuthor.getId()) {
                if (mOnItemClickListenerAuthor != null) {
                    mOnItemClickListenerAuthor.onItemClick(v, getAdapterPosition());
                }
            }
        }

        private void bindCompare(Compare compare) {
            if (compare.getAuthor().getPhotoUrl() != null) {
                ImageUtils.loadCircleImage(mIvAvatar, compare.getAuthor().getPhotoUrl());
            }
            mTvAuthor.setText(compare.getAuthor().getFullName());

            mTvQuestion.setText(compare.getQuestion());

            if (compare.getVariants().get(0).getImageUrl() != null) {
                ImageUtils.loadImage(mIvFirst, compare.getVariants().get(0).getImageUrl());
            } else {
                mIvFirst.setImageDrawable(ContextCompat.getDrawable(mIvFirst.getContext(),
                        R.drawable.standard_photo));
            }
            mTvFirst.setText(compare.getVariants().get(0).getDescription());
            mChLikeFirst.setText(String.valueOf(compare.getVariants().get(0).getLikes()));

            if (compare.getVariants().get(1).getImageUrl() != null) {
                ImageUtils.loadImage(mIvSecond, compare.getVariants().get(1).getImageUrl());
            } else {
                mIvSecond.setImageDrawable(ContextCompat.getDrawable(mIvSecond.getContext(),
                        R.drawable.standard_photo));
            }
            mTvSecond.setText(compare.getVariants().get(1).getDescription());
            mChLikeSecond.setText(String.valueOf(compare.getVariants().get(1).getLikes()));

            if (compare.getLikedVariant() == 0) {
                mChLikeFirst.setChecked(true);
                mChLikeSecond.setChecked(false);
            } else if (compare.getLikedVariant() == 1) {
                mChLikeFirst.setChecked(false);
                mChLikeSecond.setChecked(true);
            } else {
                mChLikeFirst.setChecked(false);
                mChLikeSecond.setChecked(false);
            }

            mTvDate.setText(DateUtils.convertDateTime(compare.getDate()));

            String[] categories = mTvCategory.getContext()
                    .getResources()
                    .getStringArray(R.array.categories);
            mTvCategory.setText(categories[Integer.parseInt(compare.getCategory())]);

            if (compare.isOpen())
                mTvStatus.setText(mTvStatus.getContext().getString(R.string.status_open));
            else
                mTvStatus.setText(mTvStatus.getContext().getString(R.string.status_closed));
        }
    }

    private void updateLikeView(CheckBox clickedCheckBox, CheckBox otherCheckBox) {
        if (otherCheckBox.isChecked()) {
            otherCheckBox.setChecked(false);
            int newValue = Integer.parseInt(otherCheckBox.getText().toString()) - 1;
            otherCheckBox.setText(String.valueOf(newValue));
        }
        if (clickedCheckBox.isChecked()) {
            int newValue = Integer.parseInt(clickedCheckBox.getText().toString()) + 1;
            clickedCheckBox.setText(String.valueOf(newValue));
        } else {
            int newValue = Integer.parseInt(clickedCheckBox.getText().toString()) - 1;
            clickedCheckBox.setText(String.valueOf(newValue));
        }
    }

    // click listener for details
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    // click listener for images
    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        mOnImageClickListener = onImageClickListener;
    }

    // click listener for likes
    public void setOnLikeClickListener(OnLikeListListener onLikeListListener) {
        mOnLikeListListener = onLikeListListener;
    }

    // click listener for author
    public void setOnItemClickListenerAuthor(OnItemClickListener onItemClickListenerAuthor) {
        mOnItemClickListenerAuthor = onItemClickListenerAuthor;
    }
}