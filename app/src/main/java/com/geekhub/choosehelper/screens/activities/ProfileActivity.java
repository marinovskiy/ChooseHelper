package com.geekhub.choosehelper.screens.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.Compare;
import com.geekhub.choosehelper.models.db.Follower;
import com.geekhub.choosehelper.models.db.User;
import com.geekhub.choosehelper.models.network.NetworkCompare;
import com.geekhub.choosehelper.models.network.NetworkFollower;
import com.geekhub.choosehelper.models.network.NetworkFollowing;
import com.geekhub.choosehelper.models.network.NetworkLike;
import com.geekhub.choosehelper.models.network.NetworkUser;
import com.geekhub.choosehelper.models.ui.UserInfo;
import com.geekhub.choosehelper.ui.adapters.ComparesAdapter;
import com.geekhub.choosehelper.ui.adapters.ProfileAdapter;
import com.geekhub.choosehelper.utils.ImageUtils;
import com.geekhub.choosehelper.utils.ModelConverter;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.Utils;
import com.geekhub.choosehelper.utils.db.DbUsersManager;
import com.geekhub.choosehelper.utils.firebase.FirebaseConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;

public class ProfileActivity extends BaseSignInActivity {

    public static final String INTENT_KEY_USER_ID = "intent_key_user_id";
    public static final String INTENT_KEY_USER_NAME = "intent_key_user_name";

    private static final String TAG = ProfileActivity.class.getName();

    @Bind(R.id.swipe_to_refresh_profile)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.toolbar_profile)
    Toolbar mToolbar;

    @Bind(R.id.toolbar_shadow_profile)
    View mToolbarShadow;

    @Bind(R.id.profile_iv_avatar)
    ImageView mIvUserAvatar;

    @Bind(R.id.profile_btn_follow)
    Button mProfileBtnFollow;

    @Bind(R.id.recycler_view_profile)
    RecyclerView mRecyclerViewProfile;

    @Bind(R.id.recycler_view_profile_compares)
    RecyclerView mRecyclerViewCompares;

    /**
     * firebase references and quieries
     **/
    private Firebase mFirebaseUser;
    private Firebase mFirebaseFollowings;
    private Firebase mFirebaseFollowers;
    private Query mQueryFollowers;
    private Query mQueryIsFollow;

    private Firebase mFirebaseCompares;
    private Query mQueryCompares;
    private Firebase mFirebaseLikes;

    /**
     * other
     **/
    private List<UserInfo> mUserInfoList = new ArrayList<>();

    /**
     * realm
     **/
    private String mUserId;
    private String mUserName;

    private User mUser;
    private RealmChangeListener mUserListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        /** get user id and name from intent **/
        mUserId = getIntent().getStringExtra(INTENT_KEY_USER_ID);
        mUserName = getIntent().getStringExtra(INTENT_KEY_USER_NAME);

        /** setup toolbar **/
        setupToolbar();

        if (!mUserId.equals(Prefs.getUserId())) {
            mProfileBtnFollow.setVisibility(View.VISIBLE);
        }

        /** realm change listener **/
        mUserListener = () -> {
            if (mUser != null && mUser.isLoaded() && mUser.getId().equals(mUserId)) {
                updateUi(mUser);
            }
        };

        /** firebase references **/
        // user, following and followers
        mFirebaseUser = new Firebase(FirebaseConstants.FB_REF_MAIN)
                .child(FirebaseConstants.FB_REF_USERS)
                .child(mUserId);

        mFirebaseFollowings = new Firebase(FirebaseConstants.FB_REF_MAIN)
                .child(FirebaseConstants.FB_REF_USERS)
                .child(Prefs.getUserId())
                .child(FirebaseConstants.FB_REF_FOLLOWINGS);

        mFirebaseFollowers = new Firebase(FirebaseConstants.FB_REF_MAIN)
                .child(FirebaseConstants.FB_REF_FOLLOWERS);

        mQueryFollowers = mFirebaseFollowers.orderByChild(FirebaseConstants.FB_REF_USER_ID)
                .equalTo(mUserId);

        mQueryIsFollow = mFirebaseFollowings.orderByChild(FirebaseConstants.FB_REF_USER_ID)
                .equalTo(mUserId);

        // compares
        mFirebaseCompares = new Firebase(FirebaseConstants.FB_REF_MAIN)
                .child(FirebaseConstants.FB_REF_COMPARES);

        mQueryCompares = mFirebaseCompares.orderByChild(FirebaseConstants.FB_REF_USER_ID)
                .equalTo(mUserId);

        // likes
        mFirebaseLikes = new Firebase(FirebaseConstants.FB_REF_MAIN)
                .child(FirebaseConstants.FB_REF_LIKES);

        /** check is authenticated user following this user **/
        if (!mUserId.equals(Prefs.getUserId()))
            isFollow();

        /** requests **/
        fetchProfileFromDb();
        if (Utils.hasInternet(getApplicationContext())) {
            fetchProfileFromNetwork();
        }

        /** recycler view **/
        mRecyclerViewProfile.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewCompares.setLayoutManager(new LinearLayoutManager(this));

        /** swipe refresh layout **/
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            if (Utils.hasInternet(getApplicationContext())) {
                fetchProfileFromNetwork();
            } else {
                Toast.makeText(getApplicationContext(), R.string.toast_no_internet, Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @OnClick(R.id.profile_btn_follow)
    public void onClick() {
        mProfileBtnFollow.setClickable(false);
        updateFollow();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchProfileFromNetwork();
    }

    private boolean isFollow() {
        final boolean[] result = new boolean[1];
        mQueryIsFollow.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot isFollowSnapshot) {
                for (DataSnapshot snapshot : isFollowSnapshot.getChildren()) {
                    NetworkFollowing networkFollowing = snapshot.getValue(NetworkFollowing.class);
                    if (networkFollowing.getUserId().equals(mUserId)) {
                        result[0] = true;
                        mProfileBtnFollow.setText(R.string.btn_label_unfollow);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                result[0] = false;
            }
        });
        return result[0];
    }

    private void updateFollow() {
        mQueryIsFollow.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot isFollowSnapshot) {
                boolean isNeed = true;
                for (DataSnapshot snapshot : isFollowSnapshot.getChildren()) {
                    NetworkFollowing networkFollowing = snapshot.getValue(NetworkFollowing.class);
                    if (networkFollowing.getUserId().equals(Prefs.getUserId())) {
                        isNeed = false;
                        unFollowUser();
                        break;
                    }
                }
                if (isNeed)
                    followUser();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // TODO toast exception
            }
        });
    }

    private void followUser() {
        mFirebaseFollowings.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int number = 0;
                if (dataSnapshot != null) {
                    number = (int) dataSnapshot.getChildrenCount();
                }
                /** add following to authenticated user **/
                Map<String, Object> newFollowing = new HashMap<>();
                newFollowing.put(FirebaseConstants.FB_REF_USER_ID, mUserId);
                mFirebaseFollowings.child(String.valueOf(number)).setValue(newFollowing);
                /** add follower to current user **/
                NetworkFollower networkFollower = new NetworkFollower();
                networkFollower.setUserId(mUserId);
                networkFollower.setFollowerId(Prefs.getUserId());
                mFirebaseFollowers.push().setValue(networkFollower);
                /** unfreeze button and change text **/
                mProfileBtnFollow.setText(R.string.btn_label_unfollow);
                mProfileBtnFollow.setClickable(true);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.i(TAG + "321", "onDataChange: error");
            }
        });
    }

    private void unFollowUser() {
        mQueryFollowers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot followersSnapshot) {
                /** delete from followers **/
                for (DataSnapshot snapshot : followersSnapshot.getChildren()) {
                    NetworkFollower networkFollower = snapshot.getValue(NetworkFollower.class);
                    if (networkFollower.getFollowerId().equals(Prefs.getUserId())) {
                        mFirebaseFollowers.child(snapshot.getKey()).setValue(null);
                    }
                }
                /** delete from following **/
                mFirebaseFollowings.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            NetworkFollowing networkFollowing = snapshot.getValue(NetworkFollowing.class);
                            if (networkFollowing.getUserId().equals(mUserId)) {
                                snapshot.getRef().setValue(null);
                                fetchProfileFromNetwork();
                                mProfileBtnFollow.setText(R.string.btn_label_follow);
                                mProfileBtnFollow.setClickable(true);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        // TODO toast exception
                    }
                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // TODO toast exception
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUser.removeChangeListener(mUserListener);
    }

    /**
     * get information about user from local database
     **/
    private void fetchProfileFromDb() {
        mUser = DbUsersManager.getUserById(mUserId);
        mUser.addChangeListener(mUserListener);
    }

    /**
     * get information about user from firebase
     **/
    private void fetchProfileFromNetwork() {
        mFirebaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userSnapshot) {
                NetworkUser networkUser = userSnapshot.getValue(NetworkUser.class);
                User user = ModelConverter.convertToUser(networkUser, mUserId);
                mQueryFollowers.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot followerSnapshot) {
                        RealmList<Follower> followers = new RealmList<>();
                        for (DataSnapshot dataSnapshot : followerSnapshot.getChildren()) {
                            followers.add(ModelConverter.convertToFollower(dataSnapshot.getValue(NetworkFollower.class)));
                        }
                        if (!followers.isEmpty()) {
                            user.setFollowers(followers);
                        }
                        fetchUserComparesFromNetwork(user);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        hideRefreshing();
                    }
                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                hideRefreshing();
                Toast.makeText(getApplicationContext(), "Error = " + firebaseError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * get information about user compares from firebase
     **/
    private void fetchUserComparesFromNetwork(User user) {
        mQueryCompares.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RealmList<Compare> compares = new RealmList<>();
                int snapshotSize = (int) dataSnapshot.getChildrenCount();
                if (snapshotSize == 0) {
                    hideRefreshing();
                }
                for (DataSnapshot compareSnapshot : dataSnapshot.getChildren()) {
                    NetworkCompare networkCompare = compareSnapshot.getValue(NetworkCompare.class);
                    fetchDetailsFromNetwork(user, compares, networkCompare,
                            compareSnapshot.getKey(), snapshotSize);
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
     * get details information about user compares from firebase
     **/
    private void fetchDetailsFromNetwork(User user, RealmList<Compare> compares,
                                         NetworkCompare networkCompare,
                                         String compareId, int size) {
        /** likes **/
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
                /** compare author **/
                compares.add(ModelConverter.convertToCompare(networkCompare, compareId, user, likedVariant));
                if (compares.size() == size) {
                    user.setCompares(compares);
                    DbUsersManager.saveUser(user);
                    hideRefreshing();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                hideRefreshing();
                Toast.makeText(getApplicationContext(), "Error! Please, try later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle(mUserName);
            mToolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.icon_back));
            mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbarShadow.setVisibility(View.GONE);
        }
    }

    private void updateUi(User user) {
        try {
            ImageUtils.loadImage(mIvUserAvatar, user.getPhotoUrl());
            mUserInfoList.clear();
            mUserInfoList.add(new UserInfo(user.getFollowers() != null ? user.getFollowers().size() : 0, getString(R.string.followers)));
            mUserInfoList.add(new UserInfo(user.getFollowings() != null ? user.getFollowings().size() : 0, getString(R.string.followings)));
            mUserInfoList.add(new UserInfo(user.getCompares() != null ? user.getCompares().size() : 0, getString(R.string.compares)));
            ProfileAdapter adapter;
            if (mRecyclerViewProfile.getAdapter() == null) {
                adapter = new ProfileAdapter(mUserInfoList);
                mRecyclerViewProfile.setAdapter(adapter);
                adapter.setOnItemClickListener((view, position) -> {
                    Intent intent = new Intent(this, FollowersActivity.class);
                    intent.putExtra(INTENT_KEY_USER_ID, mUserId);
                    if (position == 0) {
                        intent.putExtra(FollowersActivity.INTENT_KEY_FOLLOWERS_TITLE, getString(R.string.followers));
                        startActivity(intent);
                    } else if (position == 1) {
                        intent.putExtra(FollowersActivity.INTENT_KEY_FOLLOWERS_TITLE, getString(R.string.followings));
                        startActivity(intent);
                    }
                });
            } else {
                adapter = (ProfileAdapter) mRecyclerViewProfile.getAdapter();
                adapter.updateList(mUserInfoList);
                adapter.notifyDataSetChanged();
            }
            updateComparesList(Realm.getDefaultInstance().copyFromRealm(user.getCompares()));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void updateComparesList(List<Compare> compares) {
        Collections.sort(compares, (lhs, rhs) -> {
            if (lhs.getDate() < rhs.getDate()) return 1;
            if (lhs.getDate() > rhs.getDate()) return -1;
            return 0;
        });
        ComparesAdapter adapter;
        if (mRecyclerViewCompares.getAdapter() == null) {
            adapter = new ComparesAdapter(compares);
            mRecyclerViewCompares.setAdapter(adapter);

            /** click listener for details **/
            adapter.setOnItemClickListener((view, position) -> {
                Intent intent = new Intent(this, DetailsActivity.class);
                intent.putExtra(DetailsActivity.INTENT_KEY_COMPARE_ID, compares.get(position).getId());
                startActivity(intent);
            });

            /** click listener for likes **/
            adapter.setOnLikeClickListener((mainView, clickedCheckBox, otherCheckBox,
                                            position, variantNumber) -> {
                /*mMainView = mainView;
                mClickedCheckBox = clickedCheckBox;
                mOtherCheckBox = otherCheckBox;
                Utils.blockViews(mMainView, mClickedCheckBox, mOtherCheckBox);
                updateLike(compares.get(position).getId(), variantNumber);*/
            });

            /** click listener for popup menu **/
            adapter.setOnItemClickListenerPopup((view, position) -> {
                String compareId = compares.get(position).getId();
                if (compares.get(position).getAuthor().getId().equals(Prefs.getUserId())) {
                    //showOwnerPopupMenu(view, compareId);
                } else {
                    //showUserPopupMenu(view, compareId);
                }
            });

            /** click listener for author **/
            adapter.setOnItemClickListenerAuthor((view, position) -> {
                Intent userIntent = new Intent(this, ProfileActivity.class);
                userIntent.putExtra(ProfileActivity.INTENT_KEY_USER_ID,
                        compares.get(position).getAuthor().getId());
                userIntent.putExtra(ProfileActivity.INTENT_KEY_USER_NAME,
                        compares.get(position).getAuthor().getFullName());
                startActivity(userIntent);
            });
        } else {
            adapter = (ComparesAdapter) mRecyclerViewCompares.getAdapter();
            adapter.updateList(compares);
            adapter.notifyDataSetChanged();
        }
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
