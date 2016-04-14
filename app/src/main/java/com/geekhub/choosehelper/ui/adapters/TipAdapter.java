package com.geekhub.choosehelper.ui.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.ui.Tip;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TipAdapter extends RecyclerView.Adapter<TipAdapter.ViewHolder> {

    private List<Tip> mTips;
    private Context mContext;

    private static boolean isReversLayout;

    public TipAdapter(List<Tip> tips, Context context) {
        this.mTips = tips;
        this.mContext = context;
        isReversLayout = false;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        isReversLayout = !isReversLayout;
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                ((!isReversLayout) ? R.layout.tip_item_revers_layout : R.layout.tip_item_layout),
                parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        vh.mTvTitle.setText(mTips.get(position).getTitle());
        vh.mTvTip.setText(mTips.get(position).getDescription());
        vh.mIvPicture.setBackground(mContext.getResources().getDrawable(
                mTips.get(position).getImage()));
    }

    @Override
    public int getItemCount() {
        return mTips.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.help_tv_title)
        TextView mTvTitle;

        @Bind(R.id.help_tv_tip)
        TextView mTvTip;

        @Bind(R.id.help_iv_picture)
        ImageView mIvPicture;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
