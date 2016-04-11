package com.geekhub.choosehelper.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.ui.Settings;
import com.geekhub.choosehelper.ui.listeners.OnItemClickListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {

    private List<Settings> mSettingsList;

    private OnItemClickListener mOnItemClickListener;

    public SettingsAdapter(List<Settings> settingsList) {
        mSettingsList = settingsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.settings_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindItem(mSettingsList.get(position));
    }

    @Override
    public int getItemCount() {
        return mSettingsList != null ? mSettingsList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.settings_tv_title)
        TextView mTvTitle;

        @Bind(R.id.settings_tv_value)
        TextView mTvValue;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (getAdapterPosition() != mSettingsList.size() - 1)
                itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }

        private void bindItem(Settings setting) {
            mTvTitle.setText(setting.getTitle());
            mTvValue.setText(String.valueOf(setting.getValue()));
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

}
