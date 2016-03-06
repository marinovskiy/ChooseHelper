package com.geekhub.choosehelper.screens.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geekhub.choosehelper.R;

public class MyComparesFragment extends Fragment {


    public MyComparesFragment() {
        // Required empty public constructor
    }

    public static MyComparesFragment newInstance() {
        return new MyComparesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_compares, container, false);
    }

}
