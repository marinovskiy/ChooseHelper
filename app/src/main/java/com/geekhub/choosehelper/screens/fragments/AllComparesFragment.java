package com.geekhub.choosehelper.screens.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
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
import com.geekhub.choosehelper.screens.activities.EditCompareActivity;
import com.geekhub.choosehelper.screens.activities.ProfileActivity;
import com.geekhub.choosehelper.ui.adapters.ComparesRecyclerViewAdapter;
import com.geekhub.choosehelper.utils.ModelConverter;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.Utils;
import com.geekhub.choosehelper.utils.db.DbComparesManager;
import com.geekhub.choosehelper.utils.firebase.FirebaseComparesManager;
import com.geekhub.choosehelper.utils.firebase.FirebaseConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class AllComparesFragment extends BaseFragment {

    public static final String INTENT_KEY_COMPARE_ID = "intent_key_compare_id";

    @Bind(R.id.swipe_to_refresh_all_compares)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.recycler_view_all_compares)
    RecyclerView mRecyclerView;

    @Bind(R.id.progress_bar_all_compares)
    ProgressBar mProgressBar;

    private RealmResults<Compare> mCompares;

    private RealmChangeListener mComparesListener = () -> {
        if (mCompares != null && mCompares.isLoaded()) {
            updateUi(mCompares);
        }
    };

    private Query mQueryCompares;
    private Firebase mFirebaseRefCompares;
    private Firebase mFirebaseRefDetails;
    private Firebase mFirebaseRefLikes;

    private CardView mMainView;
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

        /** firebase references **/
        mFirebaseRefCompares = new Firebase(FirebaseConstants.FB_REF_MAIN)
                .child(FirebaseConstants.FB_REF_COMPARES);
        mQueryCompares = mFirebaseRefCompares.orderByChild(FirebaseConstants.FB_REF_DATE)
                .limitToFirst(20);

        mFirebaseRefDetails = new Firebase(FirebaseConstants.FB_REF_MAIN)
                .child(FirebaseConstants.FB_REF_LIKES);

        mFirebaseRefLikes = new Firebase(FirebaseConstants.FB_REF_MAIN)
                .child(FirebaseConstants.FB_REF_LIKES);


        /** requests **/
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
        fetchComparesFromNetwork();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompares.removeChangeListener(mComparesListener);
    }

    /**
     * get information about compare from local database
     **/
    private void fetchComparesFromDb() {
        mCompares = DbComparesManager.getCompares();
        if (mCompares.size() == 0) {
            setProgressVisibility(true);
        }
        mCompares.addChangeListener(mComparesListener);
    }

    /**
     * get information about compare from firebase
     **/
    private void fetchComparesFromNetwork() {
        mQueryCompares.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Compare> compares = new ArrayList<>();
                int snapshotSize = (int) dataSnapshot.getChildrenCount();
                if (snapshotSize == 0) {
                    hideRefreshing();
                }
                for (DataSnapshot compareSnapshot : dataSnapshot.getChildren()) {
                    NetworkCompare networkCompare = compareSnapshot.getValue(NetworkCompare.class);
                    fetchDetailsFromNetwork(compares, networkCompare,
                            compareSnapshot.getKey(), snapshotSize);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                hideRefreshing();
                Toast.makeText(getContext(), "Error! Please, try later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * get details information from firebase
     **/
    private void fetchDetailsFromNetwork(List<Compare> compares, NetworkCompare networkCompare,
                                         String compareId, int size) {
        /** likes **/
        Query queryDetails = mFirebaseRefDetails.orderByChild(FirebaseConstants.FB_REF_COMPARE_ID)
                .equalTo(compareId);
        queryDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot likeSnapshot) {
                int likedVariant = -1;
                for (DataSnapshot snapshot : likeSnapshot.getChildren()) {
                    if (snapshot.getValue(NetworkLike.class).getUserId().equals(Prefs.getUserId())) {
                        likedVariant = snapshot.getValue(NetworkLike.class).getVariantNumber();
                    }
                }
                /** compare author **/
                final int tempLikedVariant = likedVariant;
                new Firebase(FirebaseConstants.FB_REF_MAIN)
                        .child(FirebaseConstants.FB_REF_USERS)
                        .child(networkCompare.getUserId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot authorSnapshot) {
                                compares.add(ModelConverter.convertToCompare(networkCompare,
                                        compareId,
                                        authorSnapshot.getValue(NetworkUser.class),
                                        networkCompare.getUserId(),
                                        tempLikedVariant));
                                if (compares.size() == size) {
                                    DbComparesManager.saveCompares(compares);
                                    updateUi(compares);
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                hideRefreshing();
                                Toast.makeText(getContext(), "Error! Please, try later", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getContext(), "Error! Please, try later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUi(List<Compare> compares) {
        setProgressVisibility(false);

        ComparesRecyclerViewAdapter adapter = new ComparesRecyclerViewAdapter(compares.subList(0,
                compares.size() < 19 ? compares.size() : 19));
        mRecyclerView.setAdapter(adapter);

        /** click listener for details **/
        adapter.setOnItemClickListener((view, position) -> {
            Intent intent = new Intent(getActivity(), DetailsActivity.class);
            intent.putExtra(INTENT_KEY_COMPARE_ID, compares.get(position).getId());
            startActivity(intent);
        });

        /** click listener for likes **/
        adapter.setOnLikeClickListener((mainView, clickedCheckBox, otherCheckBox,
                                        position, variantNumber) -> {
            mMainView = mainView;
            mClickedCheckBox = clickedCheckBox;
            mOtherCheckBox = otherCheckBox;
            blockViews(mMainView, mClickedCheckBox, mOtherCheckBox);
            updateLike(compares.get(position).getId(), variantNumber);
        });

        /** click listener for popup menu **/
        adapter.setOnItemClickListenerPopup((view, position) -> {
            if (compares.get(position).getAuthor().getId().equals(Prefs.getUserId())) {
                showOwnerPopupMenu(compares, view, position);
            } else {
                showUsualPopupMenu(compares, view, position);
            }
        });

        adapter.setOnItemClickListenerAuthor((view, position) -> {
            Intent userIntent = new Intent(getActivity(), ProfileActivity.class);
            userIntent.putExtra(ProfileActivity.INTENT_KEY_USER_ID, compares.get(position).getAuthor().getId());
            startActivity(userIntent);
        });

        hideRefreshing();

    }

    private void updateLike(String compareId, int variantNumber) {
        Query queryLike = mFirebaseRefLikes.orderByChild(FirebaseConstants.FB_REF_COMPARE_ID)
                .equalTo(compareId);
        queryLike.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot likeSnapshot) {
                String likeId = null;
                NetworkLike networkLike = null;
                for (DataSnapshot snapshot : likeSnapshot.getChildren()) {
                    if (snapshot.getValue(NetworkLike.class).getUserId().equals(Prefs.getUserId())) {
                        networkLike = snapshot.getValue(NetworkLike.class);
                        likeId = snapshot.getKey();
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
                /** other variant liked before - just like this variant and unlike other **/
                else {
                    likeVariant(compareId, variantNumber);
                    unLikeVariant(compareId, Utils.getOtherVariantNumber(variantNumber), likeId);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getContext(), "Error! Please, try later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void likeVariant(String compareId, int variantNumber) {
        Firebase firebaseLike = mFirebaseRefCompares.child(compareId)
                .child(FirebaseConstants.FB_REF_VARIANTS)
                .child(String.valueOf(variantNumber))
                .child(FirebaseConstants.FB_REF_LIKES);
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
            public void onComplete(FirebaseError firebaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                NetworkLike networkLike = new NetworkLike();
                networkLike.setCompareId(compareId);
                networkLike.setVariantNumber(variantNumber);
                networkLike.setUserId(Prefs.getUserId());
                networkLike.setIsLike(true);
                mFirebaseRefLikes.push().setValue(networkLike);
                unBlockViews(mMainView, mClickedCheckBox, mOtherCheckBox);
            }
        });
    }

    private void unLikeVariant(String compareId, int variantNumber, String likeId) {
        Firebase firebaseLike = mFirebaseRefCompares.child(compareId)
                .child(FirebaseConstants.FB_REF_VARIANTS)
                .child(String.valueOf(variantNumber))
                .child(FirebaseConstants.FB_REF_LIKES);
        firebaseLike.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                if (currentData.getValue() != null && (Long) currentData.getValue() != 0) {
                    currentData.setValue((Long) currentData.getValue() - 1);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                mFirebaseRefLikes.child(likeId).setValue(null);
                unBlockViews(mMainView, mClickedCheckBox, mOtherCheckBox);
            }
        });
    }

    private void showUsualPopupMenu(List<Compare> compares, View view, int position) {
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
                default:
                    return false;
            }
        });
        popupMenu.show();
    }

    private void showOwnerPopupMenu(List<Compare> compares, View view, int position) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.inflate(R.menu.menu_compare_owner);
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
                    Intent intentEdit = new Intent(getActivity(), EditCompareActivity.class);
                    intentEdit.putExtra(INTENT_KEY_COMPARE_ID, compares.get(position).getId());
                    startActivity(intentEdit);
                    return true;
                case R.id.action_delete_compare:
                    Utils.showCompareDeleteDialog(getContext(), (dialog, which) -> {
                        Toast.makeText(getContext(), "which = " + which, Toast.LENGTH_SHORT).show();
                        switch (which) {
                            case -2:
                                dialog.cancel();
                                break;
                            case -1:
                                FirebaseComparesManager.deleteCompare(compares.get(position).getId());
                                break;
                        }
                    });
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show();
    }

    /**
     * methods for block and unblock views when like is transacting to server
     **/
    private void blockViews(CardView cardView, CheckBox checkBox1, CheckBox checkBox2) {
        cardView.setClickable(false);
        checkBox1.setClickable(false);
        checkBox2.setClickable(false);
    }

    private void unBlockViews(CardView cardView, CheckBox checkBox1, CheckBox checkBox2) {
        cardView.setClickable(true);
        checkBox1.setClickable(true);
        checkBox2.setClickable(true);
    }

    /**
     * methods for show and hide progress
     **/
    private void hideRefreshing() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setProgressVisibility(boolean visible) {
        mProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}