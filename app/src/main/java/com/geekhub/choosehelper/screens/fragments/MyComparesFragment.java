package com.geekhub.choosehelper.screens.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.Compare;
import com.geekhub.choosehelper.ui.adapters.ComparesRecyclerViewAdapter;
import com.geekhub.choosehelper.ui.listeners.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ComparesRecyclerViewAdapter adapter = new ComparesRecyclerViewAdapter(generateCompares());
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Compare> generateCompares() {
        List<Compare> compares = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Compare compare = new Compare();
            compare.setTitle("Here must be some title. But it was at 1:00 am, I vety wanted to sleep and didn't have enough imagination. So here just this text");
            compare.setAuthor("James Jonhs");
            compare.setDate("5 Mar 2016");
            compares.add(compare);
        }
        return compares;
    }

}
