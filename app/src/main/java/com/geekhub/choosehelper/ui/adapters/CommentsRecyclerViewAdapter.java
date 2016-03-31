package com.geekhub.choosehelper.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.Comment;
import com.geekhub.choosehelper.models.db.Compare;
import com.geekhub.choosehelper.utils.DateUtil;
import com.geekhub.choosehelper.utils.ImageUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CommentsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private Compare mCompare;
    private List<Comment> mComments;

    public CommentsRecyclerViewAdapter(Compare compare/*, List<Comment> comments*/) {
        mCompare = compare;
        mComments = compare.getComments();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            return new ItemViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.comment_item_layout, parent, false));
        } else if (viewType == TYPE_HEADER) {
            return new HeaderViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.comment_header_layout, parent, false));
        }
        return null;
        //throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).bindItem(mComments.get(position - 1));
        } else if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bindHeader(mCompare);
        }
    }

    @Override
    public int getItemCount() {
        return mComments != null ? mComments.size() + 1 : 1;
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

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView mTvAuthor;

        TextView mTvStatusAndDate;

        TextView mTvQuestion;

        ImageView mIvFirstImage;

        TextView mTvFirstVariant;

        ImageView mIvSecondImage;

        TextView mTvSecondVariant;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            //ButterKnife.bind(this, itemView);
            mTvAuthor = (TextView) itemView.findViewById(R.id.details_tv_author);
            mTvStatusAndDate = (TextView) itemView.findViewById(R.id.details_tv_status_and_date);
            mTvQuestion = (TextView) itemView.findViewById(R.id.details_tv_question);
            mIvFirstImage = (ImageView) itemView.findViewById(R.id.details_iv_first_image);
            mTvFirstVariant = (TextView) itemView.findViewById(R.id.details_tv_first_variant);
            mIvSecondImage = (ImageView) itemView.findViewById(R.id.details_iv_second_image);
            mTvSecondVariant = (TextView) itemView.findViewById(R.id.details_tv_second_variant);
        }

        private void bindHeader(Compare compare) {
            //mTvAuthor.setText(compare.getAuthor().getFullName());
            mTvStatusAndDate.setText("Open\n" + DateUtil.convertDateTime(compare.getDate()));
            mTvQuestion.setText(compare.getQuestion());
            mTvFirstVariant.setText(compare.getVariants().get(0).getDescription());
            mTvSecondVariant.setText(compare.getVariants().get(1).getDescription());
            ImageUtil.loadImage(mIvFirstImage, compare.getVariants().get(0).getImageUrl());
            ImageUtil.loadImage(mIvSecondImage, compare.getVariants().get(1).getImageUrl());
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

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
        }

        private void bindItem(Comment comment) {
            ImageUtil.loadCircleImage(mIvAvatar, comment.getAuthor().getPhotoUrl());
            mTvAuthor.setText(comment.getAuthor().getFullName());
            mTvCommentText.setText(comment.getCommentText());
            mTvDate.setText(DateUtil.convertDateTime(comment.getDate()));
        }
    }

}
