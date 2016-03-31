package com.geekhub.choosehelper.screens.fragments;


import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geekhub.choosehelper.R;

import butterknife.Bind;

public class MyComparesFragment extends BaseFragment {

    @Bind(R.id.recycler_view_my_compares)
    RecyclerView mRecyclerView;

    public MyComparesFragment() {

    }

    public static MyComparesFragment newInstance() {
        return new MyComparesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_compares, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}
