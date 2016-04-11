package com.geekhub.choosehelper.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.User;
import com.geekhub.choosehelper.ui.listeners.OnItemClickListener;
import com.geekhub.choosehelper.utils.ImageUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private List<User> mUserList;

    private OnItemClickListener mOnItemClickListener;

    public UsersAdapter(List<User> userList) {
        mUserList = userList;
    }

    public void updateList(List<User> userList) {
        mUserList = userList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindUser(mUserList.get(position));
    }

    @Override
    public int getItemCount() {
        return mUserList != null ? mUserList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.user_rv_iv_avatar)
        ImageView mIvUserAvatar;

        @Bind(R.id.user_rv_tv_name)
        TextView mTvUserName;

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

        private void bindUser(User user) {
            ImageUtils.loadCircleImage(mIvUserAvatar, user.getPhotoUrl());
            mTvUserName.setText(user.getFullName());
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

}
