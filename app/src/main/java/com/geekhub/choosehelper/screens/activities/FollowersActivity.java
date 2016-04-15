package com.geekhub.choosehelper.screens.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.User;
import com.geekhub.choosehelper.models.network.NetworkUser;
import com.geekhub.choosehelper.ui.adapters.UsersAdapter;
import com.geekhub.choosehelper.utils.ModelConverter;
import com.geekhub.choosehelper.utils.Utils;
import com.geekhub.choosehelper.utils.db.DbUsersManager;
import com.geekhub.choosehelper.utils.firebase.FirebaseConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class FollowersActivity extends BaseSignInActivity {

    public static final String INTENT_KEY_FOLLOWERS_TITLE = "intent_key_followers_title";
    public static final String INTENT_KEY_FOLLOWERS_LIST = "intent_key_followers_list";

    @Bind(R.id.toolbar_followers)
    Toolbar mToolbar;

    @Bind(R.id.toolbar_shadow_followers)
    View mToolbarShadow;

    @Bind(R.id.recycler_view_followers)
    RecyclerView mRecyclerView;

    @Bind(R.id.swipe_to_refresh_followers)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.tv_no_followers)
    TextView mTextViewEmpty;

    // firebase
    Firebase mFirebaseUsers;

    private String mToolbarTitle;

    // realm
    private List<String> mUserIds = new ArrayList<>();

    private RealmResults<User> mUsers;

    private RealmChangeListener mUserListener = () -> {
        if (mUsers != null && mUsers.isLoaded()) {
            updateUi(mUsers);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        setupToolbar();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (getIntent() != null) {
            mToolbarTitle = getIntent().getStringExtra(INTENT_KEY_FOLLOWERS_TITLE);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mToolbarTitle);
            }
            mUserIds = getIntent().getStringArrayListExtra(INTENT_KEY_FOLLOWERS_LIST);
        }

        mFirebaseUsers = new Firebase(FirebaseConstants.FB_REF_MAIN)
                .child(FirebaseConstants.FB_REF_USERS);

        if (mUserIds.size() != 0) {
            fetchUsersFromDb();
            if (Utils.hasInternet(this)) {
                fetchUsersFromNetwork();
            }
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mTextViewEmpty.setVisibility(View.VISIBLE);
            if (mToolbarTitle.equals(getString(R.string.label_followings))) {
                mTextViewEmpty.setText(R.string.no_followings);
            }
        }

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            if (Utils.hasInternet(getApplicationContext())) {
                mTextViewEmpty.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                fetchUsersFromNetwork();
            } else {
                mSwipeRefreshLayout.setRefreshing(false);
                Utils.showMessage(this, getString(R.string.toast_no_internet));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUsers != null && mUserListener != null) {
            mUsers.removeChangeListener(mUserListener);
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

    private void fetchUsersFromDb() {
        for (String userId : mUserIds) {
            mUsers = DbUsersManager.getUserByIdSync(userId);
        }
        mUsers.addChangeListener(mUserListener);
    }

    private void fetchUsersFromNetwork() {
        new Firebase(FirebaseConstants.FB_REF_MAIN)
                .child(FirebaseConstants.FB_REF_USERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<User> users = new ArrayList<>();
                        if (dataSnapshot.getChildrenCount() == 0) {
                            hideRefreshing();
                        }
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String userId = userSnapshot.getKey();
                            if (mUserIds.contains(userId)) {
                                NetworkUser networkUser = userSnapshot.getValue(NetworkUser.class);
                                users.add(ModelConverter.convertToUser(networkUser, userId));
                            }
                        }
                        if (!users.isEmpty()) {
                            updateUi(users);
                        } else {
                            mRecyclerView.setVisibility(View.GONE);
                            mTextViewEmpty.setVisibility(View.VISIBLE);
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

    private void updateUi(List<User> users) {
        UsersAdapter adapter;

        if (mRecyclerView.getAdapter() == null) {
            adapter = new UsersAdapter(users);
            mRecyclerView.setAdapter(adapter);
        } else {
            adapter = (UsersAdapter) mRecyclerView.getAdapter();
            adapter.updateList(users);
            adapter.notifyDataSetChanged();
        }

        // click listener for users
        adapter.setOnItemClickListener((view, position) -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra(ProfileActivity.INTENT_KEY_USER_ID, users.get(position).getId());
            intent.putExtra(ProfileActivity.INTENT_KEY_USER_NAME, users.get(position).getFullName());
            startActivity(intent);
        });
    }

    private void hideRefreshing() {
        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}