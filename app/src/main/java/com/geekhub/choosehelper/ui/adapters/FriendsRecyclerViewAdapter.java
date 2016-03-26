package com.geekhub.choosehelper.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.Friend;
import com.geekhub.choosehelper.ui.listeners.OnItemClickListener;
import com.geekhub.choosehelper.utils.ImageUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class FriendsRecyclerViewAdapter
        extends RecyclerView.Adapter<FriendsRecyclerViewAdapter.ViewHolder> {

    private OnItemClickListener mOnItemClickListener;
    private List<Friend> mFriendsList;

    public FriendsRecyclerViewAdapter(List<Friend> list) {
        this.mFriendsList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rv_friend, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindFriend(mFriendsList.get(position));
    }

    @Override
    public int getItemCount() {
        return mFriendsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.profile_tv_friend_name)
        TextView mTvFriendName;

        @Bind(R.id.profile_iv_friend_avatar)
        ImageView mIvFriendAvatar;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }

        private void bindFriend(Friend friend) {
            mTvFriendName.setText(friend.getFriendName());
            ImageUtil.loadImage(mIvFriendAvatar, friend.getFriendAvatarUrl());
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
