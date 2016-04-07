package com.geekhub.choosehelper.screens.activities;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Query;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.Comment;
import com.geekhub.choosehelper.models.db.Compare;
import com.geekhub.choosehelper.models.network.NetworkComment;
import com.geekhub.choosehelper.models.network.NetworkCompare;
import com.geekhub.choosehelper.models.network.NetworkLike;
import com.geekhub.choosehelper.models.network.NetworkUser;
import com.geekhub.choosehelper.screens.fragments.AllComparesFragment;
import com.geekhub.choosehelper.ui.adapters.DetailsRecyclerViewAdapter;
import com.geekhub.choosehelper.ui.adapters.SimpleDividerItemDecoration;
import com.geekhub.choosehelper.utils.ModelConverter;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.Utils;
import com.geekhub.choosehelper.utils.db.DbComparesManager;
import com.geekhub.choosehelper.utils.firebase.FirebaseConstants;

import java.util.Collections;

import butterknife.Bind;
import butterknife.OnClick;
import io.realm.RealmChangeListener;
import io.realm.RealmList;

public class DetailsActivity extends BaseSignInActivity {

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

    /**
     * freezing views when like variant
     **/
    private CheckBox mClickedCheckBox;
    private CheckBox mOtherCheckBox;

    /**
     * firebase references and queries
     **/
    private Firebase mFirebaseCompare;

    private Firebase mFirebaseLikes;
    private Query mQueryLikes;

    private Firebase mFirebaseComments;
    private Query mQueryComments;

    private Firebase mFirebaseCommentsAuthors;

    private Firebase mFirebaseCompares;

    /**
     * realm
     **/
    private String mCompareId;
    private Compare mCompare;

    private RealmChangeListener mComparesListener = () -> {
        if (mCompare != null && mCompare.isLoaded()) {
            updateUi(mCompare);
        }
    };

    private boolean mIsNeedScroll = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setupToolbar();

        /** recycler view **/
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        /** get current compare id and create firebase references **/
        if (getIntent() != null) {
            mCompareId = getIntent().getStringExtra(AllComparesFragment.INTENT_KEY_COMPARE_ID);

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

            /** requests **/
            fetchCompareFromDb();
            if (Utils.hasInternet(getApplicationContext())) {
                fetchCompareFromNetwork();
            }
        } else {
            // TODO show empty view
        }

        /** swipe refresh layout **/
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            if (Utils.hasInternet(getApplicationContext())) {
                fetchCompareFromNetwork();
            } else {
                Toast.makeText(this, R.string.toast_no_internet, Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @OnClick(R.id.details_btn_add_comment)
    public void onClick() {
        String commentText = mDetailsEtCommentText.getText().toString();
        if (!commentText.equals("")) {
            addComment(commentText);
        } else {
            Toast.makeText(this, "You can't post an empty comment", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompare.removeChangeListener(mComparesListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_more, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_more:
                if (mCompare.getId().equals(Prefs.getUserId())) {
                    //Utils.showOwnerPopupMenu(getApplicationContext(), mCompare, );
                } else {

                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.icon_arrow_back_white));
            mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbarShadow.setVisibility(View.GONE);
        }
    }

    /**
     * update UI method
     **/
    private void updateUi(Compare compare) {
        setProgressVisibility(false);
        hideRefreshing();

        DetailsRecyclerViewAdapter adapter;
        if (mRecyclerView.getAdapter() == null) {
            adapter = new DetailsRecyclerViewAdapter(compare);
            mRecyclerView.setAdapter(adapter);

            /** click listener for compare's author **/
            adapter.setOnHeaderClickListener(() -> {
                Intent userIntent = new Intent(this, ProfileActivity.class);
                userIntent.putExtra(ProfileActivity.INTENT_KEY_USER_ID, compare.getAuthor().getId());
                startActivity(userIntent);
            });

            /** click listener for comment's author **/
            adapter.setOnItemClickListener((view, position) -> {
                Intent userIntent = new Intent(this, ProfileActivity.class);
                userIntent.putExtra(ProfileActivity.INTENT_KEY_USER_ID,
                        compare.getComments().get(position).getAuthor().getId());
                startActivity(userIntent);
            });

            /** click listener for likes **/
            adapter.setOnLikeDetailsListener((clickedCheckBox, otherCheckBox, position, variantNumber) -> {
                mClickedCheckBox = clickedCheckBox;
                mOtherCheckBox = otherCheckBox;
                Utils.blockViews(mClickedCheckBox, mOtherCheckBox);
                updateLike(mCompare.getId(), variantNumber);
            });
        } else {
            adapter = (DetailsRecyclerViewAdapter) mRecyclerView.getAdapter();
            adapter.updateCompare(compare);
            adapter.notifyDataSetChanged();
            if (mIsNeedScroll) {
                mIsNeedScroll = false;
                mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount());
            }
        }
    }

    /**
     * get information about compare from local database
     **/
    private void fetchCompareFromDb() {
        mCompare = DbComparesManager.getCompareById(mCompareId);
        mCompare.addChangeListener(mComparesListener);
    }

    /**
     * get information about compare from firebase
     **/
    private void fetchCompareFromNetwork() {
        mFirebaseCompare.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot compareSnapshot) {
                NetworkCompare networkCompare = compareSnapshot.getValue(NetworkCompare.class);
                new Firebase(FirebaseConstants.FB_REF_MAIN)
                        .child(FirebaseConstants.FB_REF_USERS)
                        .child(networkCompare.getUserId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot userSnapshot) {
                                mQueryLikes.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot likeSnapshot) {
                                        int likedVariant = -1;
                                        for (DataSnapshot snapshot : likeSnapshot.getChildren()) {
                                            if (snapshot.getValue(NetworkLike.class).getUserId().equals(Prefs.getUserId())) {
                                                likedVariant = snapshot.getValue(NetworkLike.class).getVariantNumber();
                                            }
                                        }
                                        Compare compare = ModelConverter.convertToCompare(networkCompare,
                                                compareSnapshot.getKey(),
                                                userSnapshot.getValue(NetworkUser.class),
                                                networkCompare.getUserId(),
                                                likedVariant);
                                        fetchCommentsFromNetwork(compare);
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {
                                        hideRefreshing();
                                        Toast.makeText(getApplicationContext(), "Error! Please, try later", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                hideRefreshing();
                                Toast.makeText(getApplicationContext(), "Error! Please, try later", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                hideRefreshing();
                Toast.makeText(getApplicationContext(), "Error! Please, try later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * get comment and their authors for current compare
     **/
    private void fetchCommentsFromNetwork(Compare compare) {
        /** comments **/
        mQueryComments.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RealmList<Comment> comments = new RealmList<>();
                for (DataSnapshot commentsSnapshot : dataSnapshot.getChildren()) {
                    NetworkComment networkComment = commentsSnapshot.getValue(NetworkComment.class);
                    /** their authors **/
                    Firebase firebaseLikes = mFirebaseCommentsAuthors.child(networkComment.getUserId());
                    firebaseLikes.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot userSnapshot) {
                            comments.add(ModelConverter.convertToComment(networkComment,
                                    commentsSnapshot.getKey(),
                                    userSnapshot.getValue(NetworkUser.class),
                                    networkComment.getUserId()));
                            if (comments.size() == dataSnapshot.getChildrenCount()) {
                                Collections.sort(comments, (lhs, rhs) -> {
                                    if (lhs.getDate() < rhs.getDate()) return -1;
                                    if (lhs.getDate() > rhs.getDate()) return 1;
                                    return 0;
                                });
                                compare.setComments(comments);
                                DbComparesManager.saveCompare(compare);
                                updateUi(compare);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            hideRefreshing();
                            Toast.makeText(getApplicationContext(), "Error! Please, try later", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                hideRefreshing();
                Toast.makeText(getApplicationContext(), "Error! Please, try later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * methods for update likes
     **/
    private void updateLike(String compareId, int variantNumber) {
        Query queryLike = mFirebaseLikes.orderByChild(FirebaseConstants.FB_REF_COMPARE_ID)
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
                Toast.makeText(getApplicationContext(), "Error! Please, try later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void likeVariant(String compareId, int variantNumber) {
        Firebase firebaseLike = mFirebaseCompares.child(compareId)
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
            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot snapshot) {
                if (firebaseError == null) {
                    NetworkLike networkLike = new NetworkLike();
                    networkLike.setCompareId(compareId);
                    networkLike.setVariantNumber(variantNumber);
                    networkLike.setUserId(Prefs.getUserId());
                    networkLike.setIsLike(true);
                    mFirebaseLikes.push().setValue(networkLike);
                }
                Utils.unBlockViews(mClickedCheckBox, mOtherCheckBox);
            }
        });
    }

    private void unLikeVariant(String compareId, int variantNumber, String likeId) {
        Firebase firebaseLike = mFirebaseCompares.child(compareId)
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
            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot snapshot) {
                if (firebaseError == null) {
                    mFirebaseLikes.child(likeId).setValue(null);
                }
                Utils.unBlockViews(mClickedCheckBox, mOtherCheckBox);
            }
        });
    }

    /**
     * methods for add comment
     **/
    private void addComment(String commentText) {
        NetworkComment networkComment = new NetworkComment();
        networkComment.setCompareId(mCompareId);
        networkComment.setUserId(Prefs.getUserId());
        networkComment.setDate(-1 * System.currentTimeMillis());
        networkComment.setCommentText(commentText);
        mFirebaseComments.push().setValue(networkComment, (firebaseError, firebase) -> {
            if (firebaseError != null) {
                Toast.makeText(this, "Error! Please, try later", Toast.LENGTH_SHORT).show();
            } else {
                mDetailsEtCommentText.setText("");
                mDetailsEtCommentText.clearFocus();
                mIsNeedScroll = true;
                fetchCompareFromNetwork();
            }
        });
    }

    /**
     * methods for show progress
     **/
    private void hideRefreshing() {
        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setProgressVisibility(boolean visible) {
        //mProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}