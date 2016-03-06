package com.geekhub.choosehelper.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.Compare;
import com.geekhub.choosehelper.ui.listeners.OnItemClickListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Alex on 05.03.2016.
 */
public class ComparesRecyclerViewAdapter extends RecyclerView.Adapter<ComparesRecyclerViewAdapter.ViewHolder> {

    private List<Compare> mCompares;

    private OnItemClickListener mOnItemClickListener;

    public ComparesRecyclerViewAdapter(List<Compare> compares) {
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

        @Bind(R.id.rv_tv_title)
        TextView mTvTitle;

        @Bind(R.id.rv_ll_variants)
        LinearLayout mLlVariants;

        @Bind(R.id.rv_tv_author)
        TextView mTvAuthor;

        @Bind(R.id.rv_tv_date)
        TextView mTvDate;

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

        private void bindCompare(Compare compare) {
            mTvTitle.setText(compare.getTitle());
            // TODO: set view by layout inflater. Number depends on number of compare's variants
            mTvAuthor.setText(compare.getAuthor());
            mTvDate.setText(compare.getDate());
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

}
