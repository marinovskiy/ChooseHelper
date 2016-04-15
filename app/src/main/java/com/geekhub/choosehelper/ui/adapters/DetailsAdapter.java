package com.geekhub.choosehelper.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.Comment;
import com.geekhub.choosehelper.models.db.Compare;
import com.geekhub.choosehelper.ui.listeners.OnHeaderClickListener;
import com.geekhub.choosehelper.ui.listeners.OnImageClickListener;
import com.geekhub.choosehelper.ui.listeners.OnItemClickListener;
import com.geekhub.choosehelper.ui.listeners.OnLikeDetailsListener;
import com.geekhub.choosehelper.ui.listeners.OnSwitchChangeListener;
import com.geekhub.choosehelper.utils.DateUtils;
import com.geekhub.choosehelper.utils.ImageUtils;
import com.geekhub.choosehelper.utils.Prefs;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;

public class DetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private Compare mCompare = new Compare();

    private OnHeaderClickListener mOnHeaderClickListener;
    private OnLikeDetailsListener mOnLikeDetailsListener;
    private OnImageClickListener mOnImageClickListener;
    private OnSwitchChangeListener mOnSwitchChangeListener;
    private OnItemClickListener mOnCommentClickListener;

    public DetailsAdapter(Compare compare) {
        mCompare = Realm.getDefaultInstance().copyFromRealm(compare);
    }

    public void updateCompare(Compare compare) {
        mCompare = Realm.getDefaultInstance().copyFromRealm(compare);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            return new ItemViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.details_item_layout, parent, false));
        } else if (viewType == TYPE_HEADER) {
            return new HeaderViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.details_header_layout, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).bindComment(mCompare.getComments().get(position - 1));
        } else if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bindHeader(mCompare);
        }
    }

    @Override
    public int getItemCount() {
        return mCompare.getComments() != null ? mCompare.getComments().size() + 1 : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        LinearLayout mLlAuthor;

        ImageView mIvAvatar;

        TextView mTvAuthor;

        TextView mTvDate;

        TextView mTvStatus;

        TextView mTvQuestion;

        ImageView mIvFirstImage;

        TextView mTvFirstVariant;

        CheckBox mChLikeFirst;

        ImageView mIvSecondImage;

        TextView mTvSecondVariant;

        CheckBox mChLikeSecond;

        SwitchCompat mSwitchStatus;

        TextView mTvCategory;

        TextView mTvCommentsCount;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mLlAuthor = (LinearLayout) itemView.findViewById(R.id.details_ll_author);
            mIvAvatar = (ImageView) itemView.findViewById(R.id.details_iv_avatar);
            mTvAuthor = (TextView) itemView.findViewById(R.id.details_tv_author);
            mTvDate = (TextView) itemView.findViewById(R.id.details_tv_date);
            mTvStatus = (TextView) itemView.findViewById(R.id.details_tv_status);
            mTvQuestion = (TextView) itemView.findViewById(R.id.details_tv_question);
            mIvFirstImage = (ImageView) itemView.findViewById(R.id.details_iv_first_image);
            mTvFirstVariant = (TextView) itemView.findViewById(R.id.details_tv_first_variant);
            mChLikeFirst = (CheckBox) itemView.findViewById(R.id.details_ch_like_first);
            mIvSecondImage = (ImageView) itemView.findViewById(R.id.details_iv_second_image);
            mTvSecondVariant = (TextView) itemView.findViewById(R.id.details_tv_second_variant);
            mChLikeSecond = (CheckBox) itemView.findViewById(R.id.details_ch_like_second);
            mSwitchStatus = (SwitchCompat) itemView.findViewById(R.id.details_switch_status);
            mTvCategory = (TextView) itemView.findViewById(R.id.details_tv_category);
            mTvCommentsCount = (TextView) itemView.findViewById(R.id.details_tv_comments_count);

            mLlAuthor.setOnClickListener(this);
            mChLikeFirst.setOnClickListener(this);
            mChLikeSecond.setOnClickListener(this);
            mIvFirstImage.setOnClickListener(this);
            mIvSecondImage.setOnClickListener(this);
            mSwitchStatus.setOnCheckedChangeListener(this);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == mLlAuthor.getId()) {
                if (mOnHeaderClickListener != null) {
                    mOnHeaderClickListener.onHeaderClick();
                }
            } else if (id == mChLikeFirst.getId()) {
                if (mOnLikeDetailsListener != null) {
                    updateLikeView(mChLikeFirst, mChLikeSecond);
                    mOnLikeDetailsListener.onLike(mChLikeFirst, mChLikeSecond,
                            getAdapterPosition(), 0);
                }
            } else if (id == mChLikeSecond.getId()) {
                if (mOnLikeDetailsListener != null) {
                    updateLikeView(mChLikeSecond, mChLikeFirst);
                    mOnLikeDetailsListener.onLike(mChLikeSecond, mChLikeFirst,
                            getAdapterPosition(), 1);
                }
            } else if (id == mIvFirstImage.getId()) {
                if (mOnImageClickListener != null) {
                    mOnImageClickListener.onImageClick(v, getAdapterPosition(), 0);
                }
            } else if (id == mIvSecondImage.getId()) {
                if (mOnImageClickListener != null) {
                    mOnImageClickListener.onImageClick(v, getAdapterPosition(), 1);
                }
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mOnSwitchChangeListener != null) {
                mOnSwitchChangeListener.onSwitchChanged(mSwitchStatus, isChecked, mTvStatus);
            }
        }

        private void bindHeader(Compare compare) {
            // author's info and date
            if (compare.getAuthor().getPhotoUrl() != null) {
                ImageUtils.loadCircleImage(mIvAvatar, compare.getAuthor().getPhotoUrl());
            }
            mTvAuthor.setText(compare.getAuthor().getFullName());
            mTvDate.setText(DateUtils.convertDateTime(compare.getDate()));

            // question and variants
            mTvQuestion.setText(compare.getQuestion());
            mTvFirstVariant.setText(compare.getVariants().get(0).getDescription());
            mTvSecondVariant.setText(compare.getVariants().get(1).getDescription());
            if (compare.getVariants().get(0).getImageUrl() != null) {
                ImageUtils.loadImage(mIvFirstImage, compare.getVariants().get(0).getImageUrl());
            }
            if (compare.getVariants().get(1).getImageUrl() != null) {
                ImageUtils.loadImage(mIvSecondImage, compare.getVariants().get(1).getImageUrl());
            }

            // likes
            mChLikeFirst.setText(String.valueOf(compare.getVariants().get(0).getLikes()));
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

            // status
            if (compare.getAuthor().getId().equals(Prefs.getUserId())) {
                mSwitchStatus.setVisibility(View.VISIBLE);
            }
            if (compare.isOpen()) {
                if (mSwitchStatus.getVisibility() == View.VISIBLE) {
                    mSwitchStatus.setChecked(true);
                }
                mTvStatus.setText(mTvStatus.getContext().getString(R.string.status_open));
            } else {
                if (mSwitchStatus.getVisibility() == View.VISIBLE) {
                    mSwitchStatus.setChecked(false);
                }
                mTvStatus.setText(mTvStatus.getContext().getString(R.string.status_closed));
            }

            // category and comments count
            String[] categories = mTvCategory.getContext()
                    .getResources()
                    .getStringArray(R.array.categories);
            mTvCategory.setText(categories[Integer.parseInt(compare.getCategory())]);
            mTvCommentsCount.setText(String.valueOf(compare.getComments().size()));
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
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.details_comments_iv_avatar)
        ImageView mIvAvatar;

        @Bind(R.id.details_comments_tv_author)
        TextView mTvAuthor;

        @Bind(R.id.details_comments_tv_comment)
        TextView mTvCommentText;

        @Bind(R.id.details_comments_tv_date)
        TextView mTvDate;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mIvAvatar.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnCommentClickListener != null) {
                mOnCommentClickListener.onItemClick(v, getAdapterPosition());
            }
        }

        private void bindComment(Comment comment) {
            ImageUtils.loadCircleImage(mIvAvatar, comment.getAuthor().getPhotoUrl());
            mTvAuthor.setText(comment.getAuthor().getFullName());
            mTvCommentText.setText(comment.getCommentText());
            mTvDate.setText(DateUtils.convertDateTime(comment.getDate()));
        }
    }

    // click listener for compare's author
    public void setOnHeaderClickListener(OnHeaderClickListener onHeaderClickListener) {
        mOnHeaderClickListener = onHeaderClickListener;
    }

    // click listener for images
    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        mOnImageClickListener = onImageClickListener;
    }

    // click listener for likes
    public void setOnLikeDetailsListener(OnLikeDetailsListener onLikeDetailsListener) {
        mOnLikeDetailsListener = onLikeDetailsListener;
    }

    // change listener for compare's status
    public void setOnSwitchChangeListener(OnSwitchChangeListener onSwitchChangeListener) {
        mOnSwitchChangeListener = onSwitchChangeListener;
    }

    // click listener for comments
    public void setOnCommentClickListener(OnItemClickListener onCommentClickListener) {
        mOnCommentClickListener = onCommentClickListener;
    }
}