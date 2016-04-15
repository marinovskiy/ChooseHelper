package com.geekhub.choosehelper.screens.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
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
import com.geekhub.choosehelper.models.network.NetworkLike;
import com.geekhub.choosehelper.models.network.NetworkUser;
import com.geekhub.choosehelper.screens.activities.DetailsActivity;
import com.geekhub.choosehelper.screens.activities.ImageViewActivity;
import com.geekhub.choosehelper.screens.activities.MainActivity;
import com.geekhub.choosehelper.screens.activities.ProfileActivity;
import com.geekhub.choosehelper.ui.adapters.ComparesAdapter;
import com.geekhub.choosehelper.utils.ModelConverter;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.Utils;
import com.geekhub.choosehelper.utils.db.DbComparesManager;
import com.geekhub.choosehelper.utils.db.DbFields;
import com.geekhub.choosehelper.utils.firebase.FirebaseConstants;
import com.geekhub.choosehelper.utils.firebase.FirebaseLikesManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class AllComparesFragment extends BaseFragment {

    @Bind(R.id.swipe_to_refresh_all_compares)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.recycler_view_all_compares)
    RecyclerView mRecyclerView;

    @Bind(R.id.progress_bar_all_compares)
    ProgressBar mProgressBar;

    // firebase references
    private Query mQueryCompares;
    private Firebase mFirebaseLikes;

    // settings preferences
    private int mNumberOfCompares;
    private Set<String> mCategories;
    private SharedPreferences mSharedPreferences;

    // realm
    private RealmResults<Compare> mCompares;

    private RealmChangeListener mComparesListener = () -> {
        if (mCompares != null && mCompares.isLoaded()) {
            RealmQuery<Compare> query = mCompares.where().equalTo(DbFields.DB_CATEGORY, "");
            for (String category : mCategories) {
                query.or().equalTo(DbFields.DB_CATEGORY, category);
            }
            RealmResults<Compare> compares = query.findAll();
            updateUi(compares);
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

        // get shared preferences (settings)
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mNumberOfCompares = Integer.parseInt(mSharedPreferences
                .getString(getString(R.string.settings_numbers_of_compares), "5"));
        mCategories = mSharedPreferences
                .getStringSet(getString(R.string.settings_categories), new HashSet<>());
        if (mCategories.size() == 0) {
            mCategories.addAll(Arrays.asList(getResources().getStringArray(R.array.categories)));
        }

        // firebase references and queries
        mQueryCompares = new Firebase(FirebaseConstants.FB_REF_MAIN)
                .child(FirebaseConstants.FB_REF_COMPARES)
                .limitToLast(mNumberOfCompares * mCategories.size());

        mFirebaseLikes = new Firebase(FirebaseConstants.FB_REF_MAIN)
                .child(FirebaseConstants.FB_REF_LIKES);

        // requests
        fetchComparesFromDb();
        if (Utils.hasInternet(getContext())) {
            fetchComparesFromNetwork();
        }

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light); // TODO create colors array

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            if (Utils.hasInternet(getContext())) {
                fetchComparesFromNetwork();
            } else {
                hideRefreshing();
                Utils.showMessage(getContext(), getString(R.string.toast_no_internet));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainActivity.sIsNeedToAutoUpdate) {
            MainActivity.sIsNeedToAutoUpdate = false;
            mCategories = mSharedPreferences
                    .getStringSet(getString(R.string.settings_categories), new HashSet<>());
            mNumberOfCompares = Integer.parseInt(mSharedPreferences
                    .getString(getString(R.string.settings_numbers_of_compares), "5"));
            fetchComparesFromNetwork();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompares != null && mComparesListener != null) {
            mCompares.removeChangeListener(mComparesListener);
        }
    }

    // get information about compare from local database
    private void fetchComparesFromDb() {
        setProgressVisibility(true);
        mCompares = DbComparesManager.getCompares();
        mCompares.addChangeListener(mComparesListener);
    }

    // get information about compare from firebase
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
                    fetchDetailsFromNetwork(compares, networkCompare, compareSnapshot.getKey(), snapshotSize);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                hideRefreshing();
                Utils.showMessage(getContext(), getString(R.string.toast_error_message));
            }
        });
    }

    // get details information about compares from firebase
    private void fetchDetailsFromNetwork(List<Compare> compares, NetworkCompare networkCompare,
                                         String compareId, int size) {
        // liked variant
        Query queryDetails = mFirebaseLikes.orderByChild(FirebaseConstants.FB_REF_COMPARE_ID)
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
                // compare author
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
                                    hideRefreshing();
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                hideRefreshing();
                                Utils.showMessage(getContext(),
                                        getString(R.string.toast_error_message));
                            }
                        });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Utils.showMessage(getContext(), getString(R.string.toast_error_message));
            }
        });
    }

    // update UI method
    private void updateUi(List<Compare> compares) {
        setProgressVisibility(false);

        int end = mNumberOfCompares < compares.size() ? mNumberOfCompares : compares.size();
        ComparesAdapter adapter;
        if (mRecyclerView.getAdapter() == null) {
            adapter = new ComparesAdapter(compares.subList(0, end));
            mRecyclerView.setAdapter(adapter);

            // click listener for author
            adapter.setOnItemClickListenerAuthor((view, position) -> {
                try {
                    Intent userIntent = new Intent(getActivity(), ProfileActivity.class);
                    userIntent.putExtra(ProfileActivity.INTENT_KEY_USER_ID,
                            compares.get(position).getAuthor().getId());
                    userIntent.putExtra(ProfileActivity.INTENT_KEY_USER_NAME,
                            compares.get(position).getAuthor().getFullName());
                    startActivity(userIntent);
                } catch (IndexOutOfBoundsException e) {
                    Toast.makeText(getContext(), "IndexOutOfBoundsException | pos = " + position, Toast.LENGTH_SHORT).show();
                }
            });

            // click listener for details
            adapter.setOnItemClickListener((view, position) -> {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.INTENT_KEY_COMPARE_ID,
                        compares.get(position).getId());
                startActivity(intent);
            });

            // click listener for likes
            adapter.setOnLikeClickListener((mainView, clickedCheckBox, otherCheckBox,
                                            position, variantNumber) -> {
                boolean isNeedToUnCheck = false;
                if (!compares.get(position).isOpen()) { // if closed
                    isNeedToUnCheck = true;
                    Utils.showMessage(getContext(), getString(R.string.toast_cannot_like_closed));
                } else if (!Utils.hasInternet(getContext())) { // if no internet
                    isNeedToUnCheck = true;
                    Utils.showMessage(getContext(), getString(R.string.toast_no_internet));
                } else if (compares.get(position).getAuthor().getId().equals(Prefs.getUserId())) { // if user is owner
                    isNeedToUnCheck = true;
                    Utils.showMessage(getContext(), getString(R.string.toast_cannot_like_own));
                } else { // update like
                    //sIsNeedToAutoUpdate = true;
                    Utils.blockViews(mainView, clickedCheckBox, otherCheckBox);
                    FirebaseLikesManager.updateLike(compares.get(position).getId(), variantNumber,
                            mainView, clickedCheckBox, otherCheckBox);
                }
                // unCheck if need
                if (isNeedToUnCheck) {
                    clickedCheckBox.setChecked(false);
                    int newValue = Integer.parseInt(clickedCheckBox.getText().toString()) - 1;
                    clickedCheckBox.setText(String.valueOf(newValue));
                }
            });

            adapter.setOnImageClickListener((view, position, variantNumber) -> {

                Intent intent = new Intent(getActivity(), ImageViewActivity.class);

                ArrayList<String> imageUrls = new ArrayList<>();
                imageUrls.add(compares.get(position).getVariants().get(0).getImageUrl());
                imageUrls.add(compares.get(position).getVariants().get(1).getImageUrl());

                ArrayList<String> likes = new ArrayList<>();
                likes.add(String.valueOf(compares.get(position).getVariants().get(0).getLikes()));
                likes.add(String.valueOf(compares.get(position).getVariants().get(1).getLikes()));

                ArrayList<String> descriptions = new ArrayList<>();
                descriptions.add(compares.get(position).getVariants().get(0).getDescription());
                descriptions.add(compares.get(position).getVariants().get(1).getDescription());

                intent.putStringArrayListExtra(ImageViewActivity.INTENT_KEY_IMAGE_URLS, imageUrls);
                intent.putStringArrayListExtra(ImageViewActivity.INTENT_KEY_LIKES, likes);
                intent.putStringArrayListExtra(ImageViewActivity.INTENT_KEY_IMAGE_DESCRIPTIONS, descriptions);

                intent.putExtra(ImageViewActivity.INTENT_KEY_POSITION, variantNumber);
                startActivity(intent);
            });

        } else {
            adapter = (ComparesAdapter) mRecyclerView.getAdapter();
            adapter.updateList(compares.subList(0, end));
            adapter.notifyDataSetChanged();
        }
    }

    // methods for show progress
    private void hideRefreshing() {
        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setProgressVisibility(boolean visible) {
        mProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}