package com.geekhub.choosehelper.screens.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.Compare;
import com.geekhub.choosehelper.ui.adapters.ComparesRecyclerViewAdapter;
import com.geekhub.choosehelper.utils.db.DbComparesManager;
import com.geekhub.choosehelper.utils.firebase.FirebaseComparesManager;

import butterknife.Bind;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class AllComparesFragment extends BaseFragment {

    private RealmResults<Compare> mCompares;

    private RealmChangeListener mComparesListener;

    @Bind(R.id.recycler_view_all_compares)
    RecyclerView mRecyclerView;

    public AllComparesFragment() {

    }

    public static AllComparesFragment newInstance() {
        return new AllComparesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_compares, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FirebaseComparesManager.getTwentyCompares();
        mComparesListener = () -> {
            if (mCompares != null && mCompares.isLoaded()) {
                updateUi();
            }
        };
        getComparesFromDb();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ComparesRecyclerViewAdapter adapter = new ComparesRecyclerViewAdapter(mCompares);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((view, position) ->
                Toast.makeText(getContext(), "Position = " + position, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Realm.getDefaultInstance().removeAllChangeListeners();
    }

    private void updateUi() {
        ComparesRecyclerViewAdapter adapter;
        if (mRecyclerView.getAdapter() == null) {
            adapter = new ComparesRecyclerViewAdapter(mCompares);
            mRecyclerView.setAdapter(adapter);
        } else {
            adapter = (ComparesRecyclerViewAdapter) mRecyclerView.getAdapter();
            adapter.updateList(mCompares);
        }
    }

    private void getComparesFromDb() {
        mCompares = DbComparesManager.getCompares();
        mCompares.addChangeListener(mComparesListener);
    }

}
