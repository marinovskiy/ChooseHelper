package com.geekhub.choosehelper.screens.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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
import com.geekhub.choosehelper.models.network.NetworkVariant;
import com.geekhub.choosehelper.screens.activities.DetailsActivity;
import com.geekhub.choosehelper.screens.activities.ImageViewActivity;
import com.geekhub.choosehelper.screens.activities.ProfileActivity;
import com.geekhub.choosehelper.ui.adapters.ComparesAdapter;
import com.geekhub.choosehelper.utils.ModelConverter;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.Utils;
import com.geekhub.choosehelper.utils.firebase.FirebaseConstants;
import com.geekhub.choosehelper.utils.firebase.FirebaseLikesManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class SearchComparesFragment extends BaseFragment {

    @Bind(R.id.recycler_view_search_fragment)
    RecyclerView mRecyclerView;

    @Bind(R.id.progress_bar_search_compares)
    ProgressBar mProgressBar;

    // firebase references and queries
    private Firebase mFirebaseCompares;
    private Firebase mFirebaseLikes;

    public static SearchComparesFragment newInstance() {
        return new SearchComparesFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // firebase references
        mFirebaseCompares = new Firebase(FirebaseConstants.FB_REF_MAIN)
                .child(FirebaseConstants.FB_REF_COMPARES);

        mFirebaseLikes = new Firebase(FirebaseConstants.FB_REF_MAIN)
                .child(FirebaseConstants.FB_REF_LIKES);
    }

    // get information about compares from firebase
    public void searchCompares(String query) {
        setProgressVisibility(true);

        mFirebaseCompares.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Compare> compares = new ArrayList<>();
                int snapshotSize = 0;
                for (DataSnapshot compareSnapshot : dataSnapshot.getChildren()) {
                    NetworkCompare networkCompare = compareSnapshot.getValue(NetworkCompare.class);
                    List<NetworkVariant> networkVariants = networkCompare.getVariants();
                    if (networkVariants.get(0).getDescription().contains(query) ||
                            networkVariants.get(1).getDescription().contains(query)) {
                        snapshotSize++;
                        fetchDetailsFromNetwork(compares, networkCompare, compareSnapshot.getKey(),
                                snapshotSize);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                setProgressVisibility(false);
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
                                    updateUi(compares);
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                setProgressVisibility(false);
                                Utils.showMessage(getContext(),
                                        getString(R.string.toast_error_message));
                            }
                        });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                setProgressVisibility(false);
                Utils.showMessage(getContext(), getString(R.string.toast_error_message));
            }
        });
    }

    // update UI method
    private void updateUi(List<Compare> compares) {
        setProgressVisibility(false);

        ComparesAdapter adapter;
        if (mRecyclerView.getAdapter() == null) {
            adapter = new ComparesAdapter(compares);
            mRecyclerView.setAdapter(adapter);
        } else {
            adapter = (ComparesAdapter) mRecyclerView.getAdapter();
            adapter.updateList(compares);
            adapter.notifyDataSetChanged();
        }

        // click listener for author
        adapter.setOnItemClickListenerAuthor((view, position) -> {
            Intent userIntent = new Intent(getActivity(), ProfileActivity.class);
            userIntent.putExtra(ProfileActivity.INTENT_KEY_USER_ID,
                    compares.get(position).getAuthor().getId());
            userIntent.putExtra(ProfileActivity.INTENT_KEY_USER_NAME,
                    compares.get(position).getAuthor().getFullName());
            startActivity(userIntent);
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
            boolean isNeedToUnCheck = true;
            if (!compares.get(position).isOpen()) { // if closed
                Utils.showMessage(getContext(), getString(R.string.toast_cannot_like_closed));
            } else if (!Utils.hasInternet(getContext())) { // if no internet
                Utils.showMessage(getContext(), getString(R.string.toast_no_internet));
            } else if (compares.get(position).getAuthor().getId().equals(Prefs.getUserId())) { // if user is owner
                Utils.showMessage(getContext(), getString(R.string.toast_cannot_like_own));
            } else { // update like
                isNeedToUnCheck = false;
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


        // click listener for full image size
        adapter.setOnImageClickListener((view, position, variantNumber) -> {
            Intent intent = new Intent(getActivity(), ImageViewActivity.class);

            ArrayList<String> imageUrls = new ArrayList<>();
            imageUrls.add(compares.get(position).getVariants().get(0).getImageUrl());
            imageUrls.add(compares.get(position).getVariants().get(1).getImageUrl());

            ArrayList<String> descriptions = new ArrayList<>();
            descriptions.add(compares.get(position).getVariants().get(0).getDescription());
            descriptions.add(compares.get(position).getVariants().get(1).getDescription());

            intent.putStringArrayListExtra(ImageViewActivity.INTENT_KEY_IMAGE_URLS, imageUrls);
            intent.putStringArrayListExtra(ImageViewActivity.INTENT_KEY_IMAGE_DESCRIPTIONS,
                    descriptions);
            intent.putExtra(ImageViewActivity.INTENT_KEY_POSITION, variantNumber);
            startActivity(intent);
        });
    }

    private void setProgressVisibility(boolean visible) {
        mProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}