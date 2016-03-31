package com.geekhub.choosehelper.ui.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.Compare;
import com.geekhub.choosehelper.models.db.User;
import com.geekhub.choosehelper.ui.listeners.OnItemClickListener;
import com.geekhub.choosehelper.utils.DateUtil;
import com.geekhub.choosehelper.utils.ImageUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ComparesRecyclerViewAdapter extends RecyclerView.Adapter<ComparesRecyclerViewAdapter.ViewHolder> {

    private List<Compare> mCompares;

    private OnItemClickListener mOnItemClickListener;
    private OnItemClickListener mOnItemClickListenerPopup;

    public ComparesRecyclerViewAdapter(List<Compare> compares) {
        Log.i("logtags", "compares.size=" + compares.size());
        mCompares = compares;
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

        @Bind(R.id.compare_item_card_view)
        CardView mCardView;

        @Bind(R.id.rv_img_more)
        ImageView mImgMore;

        @Bind(R.id.rv_tv_title)
        TextView mTvTitle;

        @Bind(R.id.rv_iv_img_one)
        ImageView mIvOne;

        @Bind(R.id.rv_iv_img_two)
        ImageView mIvTwo;

        @Bind(R.id.rv_tv_author)
        TextView mTvAuthor;

        @Bind(R.id.rv_tv_date)
        TextView mTvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            mImgMore.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == itemView.getId()) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, getAdapterPosition());
                }
            } else if (id == mImgMore.getId()) {
                if (mOnItemClickListenerPopup != null) {
                    mOnItemClickListenerPopup.onItemClick(v, getAdapterPosition());
                }
            }
        }

        private void bindCompare(Compare compare) {
            // TODO: set view by layout inflater. Number depends on number of compares variants
            if (getAdapterPosition() == mCompares.size()) {
                RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, 0, 50);
                mCardView.setLayoutParams(layoutParams);
            }
            mTvTitle.setText(compare.getQuestion());
            if (compare.getVariants().get(0).getImageUrl() != null) {
                ImageUtil.loadImage(mIvOne, compare.getVariants().get(0).getImageUrl());
            } else {
                mIvOne.setImageDrawable(ContextCompat.getDrawable(mIvOne.getContext(),
                        R.drawable.icon_photo));
            }
            if (compare.getVariants().get(1).getImageUrl() != null) {
                ImageUtil.loadImage(mIvTwo, compare.getVariants().get(1).getImageUrl());
            } else {
                mIvTwo.setImageDrawable(ContextCompat.getDrawable(mIvOne.getContext(),
                        R.drawable.icon_photo));
            }
            mTvAuthor.setText(compare.getAuthor().getFullName());
            mTvDate.setText(DateUtil.convertDateTime(compare.getDate()));
        }
    }

    /**
     * click listener for item
     **/
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    /**
     * click listener for popup menu
     **/
    public void setOnItemClickListenerPopup(OnItemClickListener onItemClickListener) {
        mOnItemClickListenerPopup = onItemClickListener;
    }

}
