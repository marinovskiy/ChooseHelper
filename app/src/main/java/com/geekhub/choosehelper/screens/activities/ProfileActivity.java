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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.Follower;
import com.geekhub.choosehelper.models.db.User;
import com.geekhub.choosehelper.models.network.NetworkFollower;
import com.geekhub.choosehelper.models.network.NetworkUser;
import com.geekhub.choosehelper.models.ui.UserInfo;
import com.geekhub.choosehelper.ui.adapters.ProfileRecyclerViewAdapter;
import com.geekhub.choosehelper.utils.ImageUtil;
import com.geekhub.choosehelper.utils.ModelConverter;
import com.geekhub.choosehelper.utils.Utils;
import com.geekhub.choosehelper.utils.db.DbUsersManager;
import com.geekhub.choosehelper.utils.firebase.FirebaseConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;

public class ProfileActivity extends BaseSignInActivity {

    public static final String INTENT_KEY_USER_ID = "intent_key_user_id";

    private static final String TAG = ProfileActivity.class.getName();

    @Bind(R.id.swipe_to_refresh_profile)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.toolbar_profile)
    Toolbar mToolbar;

    /*@Bind(R.id.toolbar_shadow_profile)
    View mToolbarShadow;*/

    @Bind(R.id.profile_iv_avatar)
    ImageView mIvUserAvatar;

    /*@Bind(R.id.profile_tv_username)
    TextView mTvUsername;

    @Bind(R.id.profile_tv_email)
    TextView mTvEmail;*/

    @Bind(R.id.profile_btn_follow)
    Button mProfileBtnFollow;

    @Bind(R.id.recycler_view_profile)
    RecyclerView mRecyclerView;

    /**
     * firebase references and quieries
     **/
    private Firebase mFirebaseUsers;
    private Query mQueryFollowers;

    /**
     * realm
     **/
    private String mUserId;

    private User mUser;

    private RealmChangeListener mUserListener = () -> {
        if (mUser != null && mUser.isLoaded()) {
            Toast.makeText(getApplicationContext(), "yeah!!!", Toast.LENGTH_SHORT).show();
            updateUi(mUser);
        }
    };

    /**
     * other
     **/
    private List<UserInfo> mUserInfoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_new);
        setupToolbar();

        /** get user id from intent **/
        if (getIntent() != null) {
            mUserId = getIntent().getStringExtra(INTENT_KEY_USER_ID);

            /** firebase references **/
            mFirebaseUsers = new Firebase(FirebaseConstants.FB_REF_MAIN)
                    .child(FirebaseConstants.FB_REF_USERS)
                    .child(mUserId);

            mQueryFollowers = new Firebase(FirebaseConstants.FB_REF_MAIN)
                    .child(FirebaseConstants.FB_REF_FOLLOWERS)
                    .orderByChild(FirebaseConstants.FB_REF_USER_ID).equalTo(mUserId);

            /** requests **/
            fetchProfileFromDb();
            if (Utils.hasInternet(getApplicationContext())) {
                fetchProfileFromNetwork();
            }
        } else {
            // TODO show empty or error view
        }

        /** recycler view **/
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        /** swipe refresh layout **/
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light); // TODO create colors array

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
        
    }

    @Override
    protected void onPause() {
        super.onPause();
        Realm.getDefaultInstance().removeAllChangeListeners();
    }

    private void fetchProfileFromDb() {
        mUser = DbUsersManager.getUserById(mUserId);
        mUser.addChangeListener(mUserListener);
    }

    private void fetchProfileFromNetwork() {
        mFirebaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userSnapshot) {
                NetworkUser networkUser = userSnapshot.getValue(NetworkUser.class);
                User user = ModelConverter.convertToUser(networkUser, mUserId);
                //updateUi(user);
                mQueryFollowers.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot followerSnapshot) {
                        RealmList<Follower> followers = new RealmList<>();
                        for (DataSnapshot dataSnapshot : followerSnapshot.getChildren()) {
                            followers.add(ModelConverter.convertToFollower(dataSnapshot.getValue(NetworkFollower.class)));
                        }
                        user.setFollowers(followers);
                        DbUsersManager.saveUser(user);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(getApplicationContext(), "Error = " + firebaseError, Toast.LENGTH_SHORT).show();
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
            mToolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.icon_arrow_back_white));
            mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //mToolbarShadow.setVisibility(View.GONE);
        }
    }

    private void updateUi(User user) {
        try {
            ImageUtil.loadImage(mIvUserAvatar, user.getPhotoUrl());
            mToolbar.setTitle(user.getFullName());
            //mTvUsername.setText(user.getFullName());
            //mTvEmail.setText(user.getEmail());
            mUserInfoList.add(new UserInfo(user.getFollowings().size(), "Followings"));
            mUserInfoList.add(new UserInfo(user.getFollowers().size(), "Followers"));
            mUserInfoList.add(new UserInfo(143, "Compares"));
            ProfileRecyclerViewAdapter adapter;
            if (mRecyclerView.getAdapter() == null) {
                adapter = new ProfileRecyclerViewAdapter(mUserInfoList);
                mRecyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener((view, position) -> {
                    Intent intent = new Intent(this, FollowersActivity.class);
                    if (position == 0) {
                        intent.putExtra(FollowersActivity.INTENT_KEY_FOLLOWERS_TITLE, "Followings");
                    } else if (position == 1) {
                        intent.putExtra(FollowersActivity.INTENT_KEY_FOLLOWERS_TITLE, "Followers");
                    }
                    startActivity(intent);
                    // TODO set clickable false for compares item
                });
            } else {
                adapter = (ProfileRecyclerViewAdapter) mRecyclerView.getAdapter();
                adapter.updateList(mUserInfoList);
                adapter.notifyDataSetChanged();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
