package com.geekhub.choosehelper.screens.fragments;


import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geekhub.choosehelper.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class FriendsComparesFragment extends BaseFragment {

    @Bind(R.id.recycler_view_friends_compares)
    RecyclerView mRecyclerView;

    public FriendsComparesFragment() {

    }

    public static FriendsComparesFragment newInstance() {
        return new FriendsComparesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends_compares, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}
