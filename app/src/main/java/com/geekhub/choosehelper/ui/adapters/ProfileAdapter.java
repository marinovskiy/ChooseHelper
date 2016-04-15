package com.geekhub.choosehelper.ui.adapters;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.ui.UserInfo;
import com.geekhub.choosehelper.ui.listeners.OnItemClickListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    private List<UserInfo> mList;

    private OnItemClickListener mOnItemClickListener;

    public ProfileAdapter(List<UserInfo> list) {
        mList = list;
    }

    public void updateList(List<UserInfo> list) {
        mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindItem(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.profile_rv_tv_count)
        TextView mTvCount;

        @Bind(R.id.profile_rv_tv_title)
        TextView mTvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }

        private void bindItem(UserInfo userInfo) {
            if (userInfo.getTitle().equals(mTvCount.getContext().getString(R.string.label_compares))) {
                Drawable drawable = ContextCompat.getDrawable(mTvCount.getContext(), R.drawable.icon_forum);
                drawable.setBounds(0, 0, 50, 50);
                mTvCount.setCompoundDrawables(drawable, null, null, null);
            }
            mTvCount.setText(String.valueOf(userInfo.getCount()));
            mTvTitle.setText(userInfo.getTitle());
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}