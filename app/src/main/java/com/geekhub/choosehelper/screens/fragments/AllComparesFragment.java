package com.geekhub.choosehelper.screens.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.Compare;
import com.geekhub.choosehelper.models.network.NetworkCompare;
import com.geekhub.choosehelper.models.network.NetworkUser;
import com.geekhub.choosehelper.screens.activities.DetailsActivity;
import com.geekhub.choosehelper.ui.adapters.ComparesRecyclerViewAdapter;
import com.geekhub.choosehelper.utils.ModelConverter;
import com.geekhub.choosehelper.utils.Utils;
import com.geekhub.choosehelper.utils.db.DbComparesManager;
import com.geekhub.choosehelper.utils.firebase.FirebaseConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

public class AllComparesFragment extends BaseFragment {

    public static final String INTENT_KEY_COMPARE_ID = "intent_key_compare_id";

    @Bind(R.id.all_compares_swipe_to_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.recycler_view_all_compares)
    RecyclerView mRecyclerView;

    @Bind(R.id.all_compares_progress_bar)
    ProgressBar mProgressBar;

    private Query mFirebaseQuery;

    public static boolean sIsNeedToReload = false;

    private RealmResults<Compare> mCompares;

    private RealmChangeListener mComparesListener = () -> {
        Log.i("logtags", "mComparesListener");
        if (mCompares != null && mCompares.isLoaded()) {
            Log.i("logtags", "mCompares != null && mCompares.isLoaded()");
            for (Compare mCompare : mCompares) {
                Log.i("logtags", "mCompares.id=" + mCompare.getId());
            }
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Firebase firebase = new Firebase(FirebaseConstants.FB_REFERENCE_MAIN)
                .child(FirebaseConstants.FB_REFERENCE_COMPARES);
        mFirebaseQuery = firebase.orderByChild(FirebaseConstants.FB_REFERENCE_COMPARES_DATE)
                .limitToFirst(20);

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
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sIsNeedToReload) {
            sIsNeedToReload = false;
            fetchComparesFromNetwork();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompares.removeChangeListener(mComparesListener);
    }

    private void fetchComparesFromDb() {
        Log.i("logtags", "fetchComparesFromDb");
        mCompares = DbComparesManager.getCompares();
        if (mCompares.size() == 0) {
            setProgressVisibility(true);
        }
        Log.i("logtags", "fetchComparesFromDb mCompares.size=" + mCompares.size());
        mCompares.addChangeListener(mComparesListener);
    }

    private void fetchComparesFromNetwork() {
        Log.i("logtags", "fetchComparesFromNetwork");
        mFirebaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RealmList<Compare> compares = new RealmList<>();
                for (DataSnapshot compareSnapshot : dataSnapshot.getChildren()) {
                    NetworkCompare networkCompare = compareSnapshot.getValue(NetworkCompare.class);
                    new Firebase(FirebaseConstants.FB_REFERENCE_MAIN)
                            .child(FirebaseConstants.FB_REFERENCE_USERS)
                            .child(networkCompare.getUserId())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    compares.add(ModelConverter.convertToCompare(networkCompare,
                                            compareSnapshot.getKey(),
                                            dataSnapshot.getValue(NetworkUser.class),
                                            networkCompare.getUserId()));
                                    if (compares.size() == dataSnapshot.getChildrenCount()) {
                                        DbComparesManager.saveCompares(compares);
                                        updateUi(compares);
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                    if (mSwipeRefreshLayout.isRefreshing()) {
                                        mSwipeRefreshLayout.setRefreshing(false);
                                    }
                                    Log.i("logtags", "saveUserFromFb: firebase = error details:" + firebaseError.getDetails()
                                            + "message: " + firebaseError.getMessage() + " code: " + firebaseError.getCode());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                Log.i("logtags", "saveUserFromFb: firebase = error details:" + firebaseError.getDetails()
                        + "message: " + firebaseError.getMessage() + " code: " + firebaseError.getCode());
            }
        });
    }

    private void updateUi(List<Compare> compares) {
        setProgressVisibility(false);
        Log.i("logtags", "updateUi");
        Log.i("logtags", "compares.size=" + compares.size());
        ComparesRecyclerViewAdapter adapter = new ComparesRecyclerViewAdapter(compares.subList(0, compares.size() < 19 ? compares.size() : 19));
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((view, position) -> {
            Intent intent = new Intent(getActivity(), DetailsActivity.class);
            intent.putExtra(INTENT_KEY_COMPARE_ID, compares.get(position).getId());
            startActivity(intent);
        });
        adapter.setOnItemClickListenerPopup((view, position) -> {
            showPopupMenu(compares, view, position);
        });
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void showPopupMenu(List<Compare> compares, View view, int position) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.inflate(R.menu.menu_compare);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_details_compare:
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra(INTENT_KEY_COMPARE_ID, compares.get(position).getId());
                    startActivity(intent);
                    return true;
                case R.id.action_share_compare:
                    //TODO share compare
                    return true;
                case R.id.action_edit_compare:
                    //TODO edit compare
                    return true;
                case R.id.action_delete_compare:
                    //TODO delete compare
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show();
    }

    private void setProgressVisibility(boolean visible) {
        mProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

}