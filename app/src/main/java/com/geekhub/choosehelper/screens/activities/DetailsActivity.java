package com.geekhub.choosehelper.screens.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.Comment;
import com.geekhub.choosehelper.models.db.Compare;
import com.geekhub.choosehelper.models.network.NetworkComment;
import com.geekhub.choosehelper.models.network.NetworkCompare;
import com.geekhub.choosehelper.models.network.NetworkLike;
import com.geekhub.choosehelper.models.network.NetworkUser;
import com.geekhub.choosehelper.screens.fragments.AllComparesFragment;
import com.geekhub.choosehelper.ui.adapters.DetailsAdapter;
import com.geekhub.choosehelper.ui.dividers.CommentsDivider;
import com.geekhub.choosehelper.utils.ModelConverter;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.Utils;
import com.geekhub.choosehelper.utils.db.DbComparesManager;
import com.geekhub.choosehelper.utils.firebase.FirebaseConstants;
import com.geekhub.choosehelper.utils.firebase.FirebaseLikesManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import io.realm.RealmChangeListener;
import io.realm.RealmList;

public class DetailsActivity extends BaseSignInActivity {

    public static final String INTENT_KEY_COMPARE_ID = "intent_key_compare_id";

    @Bind(R.id.toolbar_details)
    Toolbar mToolbar;

    @Bind(R.id.toolbar_shadow_details)
    View mToolbarShadow;

    @Bind(R.id.swipe_to_refresh_details)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.details_rv)
    RecyclerView mRecyclerView;

    @Bind(R.id.details_et_comment_text)
    EditText mDetailsEtCommentText;

    @Bind(R.id.progress_bar_details)
    ProgressBar mCommentsProgressBar;

    // firebase references and queries
    private Firebase mFirebaseCompare;

    private Firebase mFirebaseLikes;
    private Query mQueryLikes;

    private Firebase mFirebaseComments;
    private Query mQueryComments;

    private Firebase mFirebaseCommentsAuthors;

    private Firebase mFirebaseCompares;

    private boolean mStatus;

    // realm
    private String mCompareId;
    private Compare mCompare;

    private RealmChangeListener mComparesListener = () -> {
        if (mCompare != null && mCompare.isLoaded()) {
            mStatus = mCompare.isOpen();
            updateUi(mCompare);
        }
    };

    private boolean mIsNeedScroll = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setupToolbar();

        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new CommentsDivider(this));

        if (getIntent() != null) {
            mCompareId = getIntent().getStringExtra(INTENT_KEY_COMPARE_ID);

            mFirebaseCompare = new Firebase(FirebaseConstants.FB_REF_MAIN)
                    .child(FirebaseConstants.FB_REF_COMPARES)
                    .child(mCompareId);

            mFirebaseLikes = new Firebase(FirebaseConstants.FB_REF_MAIN)
                    .child(FirebaseConstants.FB_REF_LIKES);
            mQueryLikes = mFirebaseLikes.orderByChild(FirebaseConstants.FB_REF_COMPARE_ID)
                    .equalTo(mCompareId);

            mFirebaseComments = new Firebase(FirebaseConstants.FB_REF_MAIN)
                    .child(FirebaseConstants.FB_REF_COMMENTS);
            mQueryComments = mFirebaseComments.orderByChild(FirebaseConstants.FB_REF_COMPARE_ID)
                    .equalTo(mCompareId);

            mFirebaseCommentsAuthors = new Firebase(FirebaseConstants.FB_REF_MAIN)
                    .child(FirebaseConstants.FB_REF_USERS);

            mFirebaseCompares = new Firebase(FirebaseConstants.FB_REF_MAIN)
                    .child(FirebaseConstants.FB_REF_COMPARES);
        }

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            if (Utils.hasInternet(getApplicationContext())) {
                if (mCompare.isValid() && mStatus != mCompare.isOpen()) {
                    updateCompareStatus(mStatus);
                }
                fetchCompareFromNetwork();
            } else {
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getApplicationContext(), R.string.toast_no_internet,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.details_btn_add_comment)
    public void onClick() {
        String commentText = mDetailsEtCommentText.getText().toString();
        if (!mCompare.isOpen()) {
            Toast.makeText(getApplicationContext(), R.string.toast_cannot_comment_closed,
                    Toast.LENGTH_SHORT).show();
        } else if (!commentText.equals("")) {
            addComment(commentText);
        } else {
            Toast.makeText(getApplicationContext(), R.string.toast_empty_comment,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchCompareFromDb();
        if (Utils.hasInternet(getApplicationContext())) {
            fetchCompareFromNetwork();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCompare.isValid() && mStatus != mCompare.isOpen()) {
            updateCompareStatus(mStatus);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompare != null && mComparesListener != null) {
            mCompare.removeChangeListener(mComparesListener);
        }
    }

    private void setupToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.icon_back));
            mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbarShadow.setVisibility(View.GONE);
        }
    }

    // get information about compare from local database
    private void fetchCompareFromDb() {
        setProgressVisibility(true);
        mCompare = DbComparesManager.getCompareById(mCompareId);
        mCompare.addChangeListener(mComparesListener);
    }

    // get information about compare from firebase
    private void fetchCompareFromNetwork() {
        mFirebaseCompare.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot compareSnapshot) {
                NetworkCompare netCompare = compareSnapshot.getValue(NetworkCompare.class);

                // likes
                new Firebase(FirebaseConstants.FB_REF_MAIN)
                        .child(FirebaseConstants.FB_REF_USERS)
                        .child(netCompare.getUserId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot userSnapshot) {
                                mQueryLikes.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot likeSnapshot) {
                                        int likedVariant = -1;
                                        for (DataSnapshot snapshot : likeSnapshot.getChildren()) {
                                            if (snapshot.getValue(NetworkLike.class).getUserId()
                                                    .equals(Prefs.getUserId())) {
                                                likedVariant = snapshot.getValue(NetworkLike.class)
                                                        .getVariantNumber();
                                            }
                                        }
                                        Compare compare = ModelConverter.convertToCompare(netCompare,
                                                compareSnapshot.getKey(),
                                                userSnapshot.getValue(NetworkUser.class),
                                                netCompare.getUserId(),
                                                likedVariant);
                                        fetchCommentsFromNetwork(compare);
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {
                                        hideRefreshing();
                                        Toast.makeText(getApplicationContext(),
                                                R.string.toast_error_try_later,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                hideRefreshing();
                                Toast.makeText(getApplicationContext(),
                                        R.string.toast_error_try_later,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                hideRefreshing();
                Toast.makeText(getApplicationContext(),
                        R.string.toast_error_try_later,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // get comment and their authors for current compare
    private void fetchCommentsFromNetwork(Compare compare) {

        // comments
        mQueryComments.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RealmList<Comment> comments = new RealmList<>();
                if (dataSnapshot.getChildrenCount() == 0) {
                    hideRefreshing();
                }
                for (DataSnapshot commentsSnapshot : dataSnapshot.getChildren()) {
                    NetworkComment networkComment = commentsSnapshot.getValue(NetworkComment.class);

                    // their authors
                    Firebase fLikes = mFirebaseCommentsAuthors.child(networkComment.getUserId());
                    fLikes.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot userSnapshot) {
                            comments.add(ModelConverter.convertToComment(networkComment,
                                    commentsSnapshot.getKey(),
                                    userSnapshot.getValue(NetworkUser.class),
                                    networkComment.getUserId()));
                            if (comments.size() == dataSnapshot.getChildrenCount()) {
                                compare.setComments(comments);
                                DbComparesManager.saveCompare(compare);
                                hideRefreshing();
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            hideRefreshing();
                            Utils.showMessage(getApplicationContext(),
                                    getString(R.string.toast_error_try_later));
                        }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                hideRefreshing();
                Toast.makeText(getApplicationContext(),
                        R.string.toast_error_try_later,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // update UI method
    private void updateUi(Compare compare) {
        setProgressVisibility(false);

        DetailsAdapter adapter;
        if (mRecyclerView != null && mRecyclerView.getAdapter() == null) {
            adapter = new DetailsAdapter(compare);
            mRecyclerView.setAdapter(adapter);

            // click listener for compare's author
            adapter.setOnHeaderClickListener(() -> {
                if (compare.isValid()) {
                    Intent userIntent = new Intent(this, ProfileActivity.class);
                    userIntent.putExtra(ProfileActivity.INTENT_KEY_USER_ID,
                            compare.getAuthor().getId());
                    userIntent.putExtra(ProfileActivity.INTENT_KEY_USER_NAME,
                            compare.getAuthor().getFullName());
                    startActivity(userIntent);
                }
            });

            // click listener for comment's author
            adapter.setOnCommentClickListener((view, position) -> {
                if (compare.isValid()) {
                    Intent userIntent = new Intent(this, ProfileActivity.class);
                    userIntent.putExtra(ProfileActivity.INTENT_KEY_USER_ID,
                            compare.getComments().get(position - 1).getAuthor().getId());
                    userIntent.putExtra(ProfileActivity.INTENT_KEY_USER_NAME,
                            compare.getComments().get(position - 1).getAuthor().getFullName());
                    startActivity(userIntent);
                }
            });

            // click listener for likes
            adapter.setOnLikeDetailsListener((clickedCheckBox, otherCheckBox,
                                              position, variantNumber) -> {
                if (!compare.isOpen()) {
                    clickedCheckBox.setChecked(false);
                    int newValue = Integer.parseInt(clickedCheckBox.getText().toString()) - 1;
                    clickedCheckBox.setText(String.valueOf(newValue));
                    Toast.makeText(getApplicationContext(), R.string.toast_cannot_like_closed,
                            Toast.LENGTH_SHORT).show();
                } else if (!Utils.hasInternet(getApplicationContext())) {
                    clickedCheckBox.setChecked(false);
                    int newValue = Integer.parseInt(clickedCheckBox.getText().toString()) - 1;
                    clickedCheckBox.setText(String.valueOf(newValue));
                    Toast.makeText(getApplicationContext(), R.string.toast_no_internet,
                            Toast.LENGTH_SHORT).show();
                } else if (compare.getAuthor().getId().equals(Prefs.getUserId())) {
                    clickedCheckBox.setChecked(false);
                    int newValue = Integer.parseInt(clickedCheckBox.getText().toString()) - 1;
                    clickedCheckBox.setText(String.valueOf(newValue));
                    Toast.makeText(getApplicationContext(), R.string.toast_cannot_like_own,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Utils.blockViews(clickedCheckBox, otherCheckBox);
                    MainActivity.sIsNeedToAutoUpdate = true;
                    FirebaseLikesManager.updateLike(mCompare.getId(), variantNumber,
                            null, clickedCheckBox, otherCheckBox);
                }
            });

            // click listener for compare's status
            adapter.setOnSwitchChangeListener((switchCompat, isChecked, tvStatus) -> {
                if (Utils.hasInternet(this)) {
                    tvStatus.setText(isChecked ? getString(R.string.status_open)
                            : getString(R.string.status_closed));
                    mStatus = isChecked;
                } else {
                    Utils.showMessage(this, getString(R.string.toast_no_internet));
                    switchCompat.setChecked(!isChecked);
                }
            });

            // click listener for full image view
            adapter.setOnImageClickListener((view, position, variantNumber) -> {
                Intent intent = new Intent(this, ImageViewActivity.class);

                ArrayList<String> imageUrls = new ArrayList<>();
                imageUrls.add(compare.getVariants().get(0).getImageUrl());
                imageUrls.add(compare.getVariants().get(1).getImageUrl());

                ArrayList<String> likes = new ArrayList<>();
                likes.add(String.valueOf(compare.getVariants().get(0).getLikes()));
                likes.add(String.valueOf(compare.getVariants().get(1).getLikes()));

                ArrayList<String> descriptions = new ArrayList<>();
                descriptions.add(compare.getVariants().get(0).getDescription());
                descriptions.add(compare.getVariants().get(1).getDescription());

                intent.putStringArrayListExtra(ImageViewActivity.INTENT_KEY_IMAGE_URLS, imageUrls);
                intent.putStringArrayListExtra(ImageViewActivity.INTENT_KEY_LIKES, likes);
                intent.putStringArrayListExtra(ImageViewActivity.INTENT_KEY_IMAGE_DESCRIPTIONS, descriptions);

                intent.putExtra(ImageViewActivity.INTENT_KEY_POSITION, variantNumber);
                startActivity(intent);
            });
        } else if (mRecyclerView != null) {
            adapter = (DetailsAdapter) mRecyclerView.getAdapter();
            adapter.updateCompare(compare);
            adapter.notifyDataSetChanged();
            if (mIsNeedScroll) {
                mIsNeedScroll = false;
                mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount());
            }
        }
    }

    private void addComment(String commentText) {
        NetworkComment networkComment = new NetworkComment();
        networkComment.setCompareId(mCompareId);
        networkComment.setUserId(Prefs.getUserId());
        networkComment.setDate(-1 * System.currentTimeMillis());
        networkComment.setCommentText(commentText);
        mFirebaseComments.push().setValue(networkComment, (firebaseError, firebase) -> {
            if (firebaseError != null) {
                Utils.showMessage(getApplicationContext(),
                        getString(R.string.toast_error_try_later));
            } else {
                mDetailsEtCommentText.setText("");
                mDetailsEtCommentText.clearFocus();
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                mIsNeedScroll = true;
                fetchCompareFromNetwork();
            }
        });
    }

    private void updateCompareStatus(boolean status) {
        Map<String, Object> map = new HashMap<>();
        map.put(FirebaseConstants.FB_REF_STATUS, status);
        mFirebaseCompare.updateChildren(map);
        fetchCompareFromNetwork();
    }

    // methods for show progress
    private void hideRefreshing() {
        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setProgressVisibility(boolean visible) {
//        mCommentsProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}