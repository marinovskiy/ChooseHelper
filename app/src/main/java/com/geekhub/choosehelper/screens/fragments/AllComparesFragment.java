package com.geekhub.choosehelper.screens.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Query;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.Compare;
import com.geekhub.choosehelper.models.network.NetworkCompare;
import com.geekhub.choosehelper.models.network.NetworkLike;
import com.geekhub.choosehelper.models.network.NetworkUser;
import com.geekhub.choosehelper.screens.activities.DetailsActivity;
import com.geekhub.choosehelper.ui.adapters.ComparesRecyclerViewAdapter;
import com.geekhub.choosehelper.ui.listeners.OnLikeClickListener;
import com.geekhub.choosehelper.utils.ModelConverter;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.Utils;
import com.geekhub.choosehelper.utils.db.DbComparesManager;
import com.geekhub.choosehelper.utils.firebase.FirebaseConstants;

import java.util.List;

import butterknife.Bind;
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
        if (mCompares != null && mCompares.isLoaded()) {
            updateUi(mCompares);
        }
    };

    private CardView mMaivView;
    private CheckBox mClickedCheckBox;
    private CheckBox mOtherCheckBox;

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
        mCompares = DbComparesManager.getCompares();
        if (mCompares.size() == 0) {
            setProgressVisibility(true);
        }
        mCompares.addChangeListener(mComparesListener);
    }

    private void fetchComparesFromNetwork() {
        /** get information about compare **/
        mFirebaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int snapshotSize = (int) dataSnapshot.getChildrenCount();
                for (DataSnapshot compareSnapshot : dataSnapshot.getChildren()) {
                    NetworkCompare networkCompare = compareSnapshot.getValue(NetworkCompare.class);
                    fetchDetailsFromNetwork(networkCompare, compareSnapshot.getKey(), snapshotSize);
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

    private void fetchDetailsFromNetwork(NetworkCompare networkCompare, String compareId, int size) {
        RealmList<Compare> compares = new RealmList<>();
        /** get information about likes **/
        new Firebase(FirebaseConstants.FB_REFERENCE_MAIN)
                .child(FirebaseConstants.FB_REFERENCE_LIKES)
                .orderByChild("compareId").equalTo(compareId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot likeSnapshot) {
                        NetworkLike networkLike = null;
                        for (DataSnapshot snapshot : likeSnapshot.getChildren()) {
                            if (snapshot.getValue(NetworkLike.class).getUserId().equals(Prefs.getUserId())/* && snapshot.getValue(NetworkLike.class).isLike()*/) {
                                networkLike = snapshot.getValue(NetworkLike.class);
                                Log.i("!!!LIKES!!!", "networkLike=" + networkLike);
                            }
                        }
                        /** get information about author **/
                        final NetworkLike finalNetworkLike = networkLike;
                        new Firebase(FirebaseConstants.FB_REFERENCE_MAIN)
                                .child(FirebaseConstants.FB_REFERENCE_USERS)
                                .child(networkCompare.getUserId())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot authorSnapshot) {
                                        int likedVariant = -1;
                                        if (finalNetworkLike != null) {
                                            likedVariant = finalNetworkLike.getVariantNumber();
                                        }
                                        compares.add(ModelConverter.convertToCompare(networkCompare,
                                                compareId,
                                                authorSnapshot.getValue(NetworkUser.class),
                                                networkCompare.getUserId(),
                                                likedVariant));
                                        if (compares.size() == size) {
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

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Log.i("!!!likes!!!", "likes error");
                    }
                });
    }

    private void likeVariant(String compareId, int variantNumber) {
        Firebase firebaseLike = new Firebase(FirebaseConstants.FB_REFERENCE_MAIN)
                .child(FirebaseConstants.FB_REFERENCE_COMPARES)
                .child(compareId)
                .child(FirebaseConstants.FB_REFERENCE_VARIANTS)
                .child(String.valueOf(variantNumber))
                .child(FirebaseConstants.FB_REFERENCE_LIKES);
        firebaseLike.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue((Long) currentData.getValue() + 1);
                }

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                Firebase firebase = new Firebase(FirebaseConstants.FB_REFERENCE_MAIN)
                        .child(FirebaseConstants.FB_REFERENCE_LIKES);
                NetworkLike networkLike = new NetworkLike();
                networkLike.setCompareId(compareId);
                networkLike.setVariantNumber(variantNumber);
                networkLike.setUserId(Prefs.getUserId());
                networkLike.setIsLike(true);
                firebase.push().setValue(networkLike);
                mMaivView.setClickable(true);
                mClickedCheckBox.setClickable(true);
                mOtherCheckBox.setClickable(true);
            }
        });
    }

    private void unLikeVariant(String compareId, int variantNumber, String likeId) {
        Firebase firebaseLike = new Firebase(FirebaseConstants.FB_REFERENCE_MAIN)
                .child(FirebaseConstants.FB_REFERENCE_COMPARES)
                .child(compareId)
                .child(FirebaseConstants.FB_REFERENCE_VARIANTS)
                .child(String.valueOf(variantNumber))
                .child(FirebaseConstants.FB_REFERENCE_LIKES);
        Log.i("!!!UNLIKE!!!", "fref=" + firebaseLike.getRef());
        firebaseLike.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                if (currentData.getValue() != null) {
                    Log.i("!!!UNLIKE!!!", "before currentData.getValue()=" + currentData.getValue());
                    currentData.setValue((Long) currentData.getValue() - 1);
                    Log.i("!!!UNLIKE!!!", "after currentData.getValue()=" + currentData.getValue());
                }

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                Firebase firebase = new Firebase(FirebaseConstants.FB_REFERENCE_MAIN)
                        .child(FirebaseConstants.FB_REFERENCE_LIKES)
                        .child(likeId);
                firebase.setValue(null);
                mMaivView.setClickable(true);
                mClickedCheckBox.setClickable(true);
                mOtherCheckBox.setClickable(true);
            }
        });
    }

    private void updateUi(List<Compare> compares) {
        setProgressVisibility(false);
        ComparesRecyclerViewAdapter adapter = new ComparesRecyclerViewAdapter(compares.subList(0, compares.size() < 19 ? compares.size() : 19));
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((view, position) -> {
            Intent intent = new Intent(getActivity(), DetailsActivity.class);
            intent.putExtra(INTENT_KEY_COMPARE_ID, compares.get(position).getId());
            startActivity(intent);
        });
        adapter.setOnLikeClickListener((mainView, clickedCheckBox, otherCheckBox, position, variantNumber) -> {
            mMaivView = mainView;
            mClickedCheckBox = clickedCheckBox;
            mOtherCheckBox = otherCheckBox;
            mMaivView.setClickable(false);
            mClickedCheckBox.setClickable(false);
            mOtherCheckBox.setClickable(false);
            updateLike(compares.get(position).getId(), variantNumber, otherCheckBox);
        });
//        adapter.setOnLikeClickListener((view, position, variantNumber) -> {
//            CheckBox checkBox = (CheckBox) view;
//            Log.i("LIKES!!!!", "updateUi: " + checkBox.isChecked());
//            updateLike(compares.get(position).getId(), variantNumber);
//            /*if (!checkBox.isChecked()) {
//                unLikeVariant(compares.get(position).getId(), variantNumber);
//            } else {
//                likeVariant(compares.get(position).getId(), variantNumber);
//            }*/
//        });
        adapter.setOnItemClickListenerPopup((view, position) -> {
            showPopupMenu(compares, view, position);
        });
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void updateLike(String compareId, int variantNumber, CheckBox otherCheckBox) {
        Query firebase = new Firebase(FirebaseConstants.FB_REFERENCE_MAIN)
                .child(FirebaseConstants.FB_REFERENCE_LIKES)
                .orderByChild("compareId").equalTo(compareId);
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot likeSnapshot) {
                String likeId = null;
                NetworkLike networkLike = null;
                for (DataSnapshot snapshot : likeSnapshot.getChildren()) {
                    if (snapshot.getValue(NetworkLike.class).getUserId().equals(Prefs.getUserId())) {
                        networkLike = snapshot.getValue(NetworkLike.class);
                        likeId = snapshot.getKey();
                        Log.i("!!!LIKES!!!", "inupdatelikenetworkLike=" + networkLike);
                    }
                }
                /** did not liked before - just like this variant **/
                if (networkLike == null) {
                    likeVariant(compareId, variantNumber);
                }
                /** liked before (true) - just unlike this variant **/
                else if (networkLike.getVariantNumber() == variantNumber && networkLike.isLike()) {
                    unLikeVariant(compareId, variantNumber, likeId);
                }
//                /** liked before but unliked (false) - just like this variant again **/
//                else if (networkLike.getVariantNumber() == variantNumber && !networkLike.isLike()) {
//                    likeVariant(compareId, variantNumber);
//                }
                /** other variant liked before - just like this variant and unlike other **/
                else {
                    likeVariant(compareId, variantNumber);
                    unLikeVariant(compareId, Utils.getOtherVariantNumber(variantNumber), likeId);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
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