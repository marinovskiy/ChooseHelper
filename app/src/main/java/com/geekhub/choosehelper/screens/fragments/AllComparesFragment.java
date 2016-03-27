package com.geekhub.choosehelper.screens.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.Compare;
import com.geekhub.choosehelper.ui.adapters.ComparesRecyclerViewAdapter;
import com.geekhub.choosehelper.utils.Utils;
import com.geekhub.choosehelper.utils.db.DbComparesManager;
import com.geekhub.choosehelper.utils.firebase.FirebaseComparesManager;

import java.util.List;

import butterknife.Bind;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class AllComparesFragment extends BaseFragment {

    @Bind(R.id.all_compares_swipe_to_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.recycler_view_all_compares)
    RecyclerView mRecyclerView;

    private boolean mIsNeedToReload = false;

    private RealmResults<Compare> mCompares;

    private RealmChangeListener mComparesListener = () -> {
        if (mCompares != null && mCompares.isLoaded()) {
            updateUi(mCompares);
        }
    };

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fetchComparesFromDb();

        if (Utils.hasInternet(getContext())) {
            fetchComparesFromNetwork();
        }

        /** swipe refresh layout **/
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            if (Utils.hasInternet(getContext())) {
                fetchComparesFromNetwork();
            } else {
                Toast.makeText(getContext(), R.string.toast_no_internet, Toast.LENGTH_SHORT).show();
            }
            mSwipeRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mIsNeedToReload) {
            mIsNeedToReload = true;
        } else {
            fetchComparesFromDb();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompares.removeChangeListener(mComparesListener);
    }

    private void fetchComparesFromDb() {
        mCompares = DbComparesManager.getCompares();
        mCompares.addChangeListener(mComparesListener);
    }

    private void fetchComparesFromNetwork() {
        FirebaseComparesManager.getLastTwentyCompares();
    }

    private void updateUi(List<Compare> compares) {
        ComparesRecyclerViewAdapter adapter = new ComparesRecyclerViewAdapter(compares);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((view, position) ->
                Toast.makeText(getContext(), "Position = " + position, Toast.LENGTH_SHORT).show());
    }
}
