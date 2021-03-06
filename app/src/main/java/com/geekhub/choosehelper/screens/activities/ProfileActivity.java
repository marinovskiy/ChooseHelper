package com.geekhub.choosehelper.screens.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
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
import com.geekhub.choosehelper.models.db.Following;
import com.geekhub.choosehelper.models.db.User;
import com.geekhub.choosehelper.models.network.NetworkCompare;
import com.geekhub.choosehelper.models.network.NetworkFollower;
import com.geekhub.choosehelper.models.network.NetworkFollowing;
import com.geekhub.choosehelper.models.network.NetworkLike;
import com.geekhub.choosehelper.models.network.NetworkUser;
import com.geekhub.choosehelper.models.ui.UserInfo;
import com.geekhub.choosehelper.screens.fragments.SearchComparesFragment;
import com.geekhub.choosehelper.screens.fragments.SearchUserComparesFragment;
import com.geekhub.choosehelper.ui.adapters.ComparesAdapter;
import com.geekhub.choosehelper.ui.adapters.ProfileAdapter;
import com.geekhub.choosehelper.ui.dialogs.DialogChangePassword;
import com.geekhub.choosehelper.ui.dividers.ProfileDivider;
import com.geekhub.choosehelper.utils.ImageUtils;
import com.geekhub.choosehelper.utils.ModelConverter;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.Utils;
import com.geekhub.choosehelper.utils.db.DbFields;
import com.geekhub.choosehelper.utils.db.DbUsersManager;
import com.geekhub.choosehelper.utils.firebase.FirebaseConstants;
import com.geekhub.choosehelper.utils.firebase.FirebaseLikesManager;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.Sort;

public class ProfileActivity extends BaseSignInActivity {

    public static final String INTENT_KEY_USER_ID = "intent_key_user_id";
    public static final String INTENT_KEY_USER_NAME = "intent_key_user_name";

    private static final int RC_GALLERY = 1;
    private static final int RC_CAMERA = 2;

    @Bind(R.id.swipe_to_refresh_profile)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.app_bar_profile)
    AppBarLayout mAppBarLayout;

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

    @Bind(R.id.profile_search_container)
    FrameLayout mSearchContainer;

    @Bind(R.id.nested_scroll_profile)
    NestedScrollView mNestedScrollView;

    private ProgressDialog mProgressDialog;

    // firebase references and queries
    private Firebase mFirebaseUser;
    private Firebase mFirebaseFollowings;
    private Firebase mFirebaseFollowers;
    private Query mQueryFollowers;
    private Query mQueryIsFollow;
    private Query mQueryCompares;
    private Firebase mFirebaseLikes;

    // is need to reload information
    private boolean mIsNeedToUpdate = false;

    // is need to expand app bar layout
    private boolean mIsNeedToExpand = false;

    private MenuItem mMenuItemChangePass;

    private List<UserInfo> mUserInfoList = new ArrayList<>();

    private String mFilePath;

    // realm
    private String mUserId;
    private String mUserName;

    private User mUser;
    private RealmChangeListener mUserListener = () -> {
        if (mUser != null && mUser.isLoaded() && mUser.getId().equals(mUserId)) {
            updateUi(mUser);
        }
    };

    private SearchUserComparesFragment mSearchUserComparesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        // get user id and name from intent
        mUserId = getIntent().getStringExtra(INTENT_KEY_USER_ID);
        mUserName = getIntent().getStringExtra(INTENT_KEY_USER_NAME);

        setupToolbar();

        mSearchUserComparesFragment = SearchUserComparesFragment.newInstance(mUserId);

        if (!mUserId.equals(Prefs.getUserId())) {
            mProfileBtnFollow.setVisibility(View.VISIBLE);
        }

        // firebase references
        // user, followings and followers
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
        mQueryCompares = new Firebase(FirebaseConstants.FB_REF_MAIN)
                .child(FirebaseConstants.FB_REF_COMPARES)
                .orderByChild(FirebaseConstants.FB_REF_USER_ID)
                .equalTo(mUserId);

        // likes
        mFirebaseLikes = new Firebase(FirebaseConstants.FB_REF_MAIN)
                .child(FirebaseConstants.FB_REF_LIKES);

        // check is authenticated user following this user
        if (!mUserId.equals(Prefs.getUserId()))
            isFollow();

        // recycler views
        mRecyclerViewProfile.setNestedScrollingEnabled(false);
        mRecyclerViewProfile.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewProfile.addItemDecoration(new ProfileDivider(this));

        mRecyclerViewCompares.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewCompares.setNestedScrollingEnabled(false);

        // requests
        fetchProfileFromDb();
        if (Utils.hasInternet(getApplicationContext())) {
            fetchProfileFromNetwork();
        }

        // swipe refresh layout
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            if (Utils.hasInternet(getApplicationContext())) {
                fetchProfileFromNetwork();
            } else {
                mSwipeRefreshLayout.setRefreshing(false);
                Utils.showMessage(getApplicationContext(), getString(R.string.toast_no_internet));
            }
        });
    }

    @OnClick({R.id.profile_iv_avatar, R.id.profile_btn_follow})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profile_iv_avatar:
                if (Prefs.getLoggedType() == Prefs.FIREBASE_LOGIN && mUserId.equals(Prefs.getUserId())) {
                    Utils.showPhotoPickerDialog(ProfileActivity.this, (dialog, which) -> {
                        switch (which) {
                            case 0:
                                Intent galleryIntent = new Intent();
                                galleryIntent.setType("image/*");
                                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(galleryIntent,
                                        getString(R.string.dialog_photo_avatar)), RC_GALLERY);
                                break;
                            case 1:
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivityForResult(cameraIntent, RC_CAMERA);
                                }
                                break;
                        }
                    });
                }
                break;
            case R.id.profile_btn_follow:
                if (Utils.hasInternet(this)) {
                    mProfileBtnFollow.setClickable(false);
                    showProgressDialog();
                    updateFollow();
                    MainActivity.sIsNeedToAutoUpdate = true;
                } else {
                    Utils.showMessage(this, getString(R.string.toast_no_internet));
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri avatarUri;
            switch (requestCode) {
                case RC_GALLERY:
                    avatarUri = data.getData();
                    try {
                        mFilePath = ImageUtils.getFilePath(getApplicationContext(), avatarUri);
                        ImageUtils.loadCircleImage(mIvUserAvatar, mFilePath);
                        new Firebase(FirebaseConstants.FB_REF_MAIN)
                                .child(FirebaseConstants.FB_REF_USERS)
                                .child(Prefs.getUserId())
                                .child(FirebaseConstants.FB_REF_PHOTO_URL)
                                .setValue(ImageUtils.getUrlAndStartUpload(mFilePath, this));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), R.string.toast_error_try_later,
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case RC_CAMERA:
                    avatarUri = ImageUtils.getPhotoUri(getApplicationContext(),
                            (Bitmap) data.getExtras().get("data"));
                    try {
                        mFilePath = ImageUtils.getFilePath(getApplicationContext(), avatarUri);
                        ImageUtils.loadCircleImage(mIvUserAvatar, mFilePath);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), R.string.toast_error_try_later,
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsNeedToUpdate) {
            mIsNeedToUpdate = false;
            fetchProfileFromNetwork();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!mIsNeedToUpdate) {
            mIsNeedToUpdate = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUser != null && mUserListener != null) {
            mUser.removeChangeListener(mUserListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (mUserId.equals(Prefs.getUserId()) && (Prefs.getLoggedType() == Prefs.FIREBASE_LOGIN)) {
            inflater.inflate(R.menu.menu_profile_owner, menu);
            mMenuItemChangePass = menu.findItem(R.id.action_change_pass);
            mMenuItemChangePass.setVisible(true);
        } else {
            inflater.inflate(R.menu.menu_profile, menu);
        }

        SearchView searchView = (SearchView)
                MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setQueryHint(getString(R.string.search_hint_user_compares));

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search),
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        getSupportFragmentManager().beginTransaction()
                                .remove(mSearchUserComparesFragment)
                                .commit();
                        mSearchContainer.setVisibility(View.GONE);
                        mIvUserAvatar.setVisibility(View.VISIBLE);
                        if (mIsNeedToExpand) {
                            mAppBarLayout.setExpanded(true);
                        }
                        if (mMenuItemChangePass != null) {
                            mMenuItemChangePass.setVisible(true);
                        }
                        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                        return true;
                    }
                });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                mSearchUserComparesFragment.searchCompares(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_change_pass:
                DialogChangePassword dialog = DialogChangePassword.newInstance(mUser.getEmail());
                dialog.show(getSupportFragmentManager(), DialogChangePassword.class.getSimpleName());
                return true;
            case R.id.action_search:
                if (Utils.hasInternet(getApplicationContext())) {
                    if (mMenuItemChangePass != null) {
                        mMenuItemChangePass.setVisible(false);
                    }
                    mIvUserAvatar.setVisibility(View.GONE);
                    mAppBarLayout.setExpanded(false);
                    if ((mAppBarLayout.getHeight() - mAppBarLayout.getBottom()) == 0) {
                        mIsNeedToExpand = true;
                    }
                    mSwipeRefreshLayout.setVisibility(View.GONE);
                    mSearchContainer.setVisibility(View.VISIBLE);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.profile_search_container, mSearchUserComparesFragment,
                                    SearchComparesFragment.class.getSimpleName())
                            .addToBackStack(SearchComparesFragment.class.getSimpleName())
                            .commit();
                } else {
                    Utils.showMessage(getApplicationContext(), getString(R.string.toast_cannot_search));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mUserName);
            }
            mToolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.icon_back));
            mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbarShadow.setVisibility(View.GONE);
        }
    }

    // get information about user from local database
    private void fetchProfileFromDb() {
        mUser = DbUsersManager.getUserById(mUserId);
        mUser.addChangeListener(mUserListener);
    }

    // get information about user from firebase
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
                            followers.add(ModelConverter.convertToFollower(dataSnapshot
                                    .getValue(NetworkFollower.class)));
                        }
                        if (!followers.isEmpty()) {
                            user.setFollowers(followers);
                        }
                        fetchUserComparesFromNetwork(user);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        hideRefreshing();
                        Utils.showMessage(getApplicationContext(),
                                getString(R.string.toast_error_message));
                    }
                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                hideRefreshing();
                Utils.showMessage(getApplicationContext(), getString(R.string.toast_error_message));
            }
        });
    }

    // get information about user compares from firebase
    private void fetchUserComparesFromNetwork(User user) {
        mQueryCompares.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RealmList<Compare> compares = new RealmList<>();
                int snapshotSize = (int) dataSnapshot.getChildrenCount();
                if (snapshotSize == 0) {
                    DbUsersManager.saveUser(user);
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
                Utils.showMessage(getApplicationContext(), getString(R.string.toast_error_message));
            }
        });
    }

    // get details information about user compares from firebase
    private void fetchDetailsFromNetwork(User user, RealmList<Compare> compares,
                                         NetworkCompare networkCompare,
                                         String compareId, int size) {

        // likes
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
                compares.add(ModelConverter.convertToCompare(networkCompare, compareId,
                        user, likedVariant));
                if (compares.size() == size) {
                    user.setCompares(compares);
                    DbUsersManager.saveUser(user);
                    hideRefreshing();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                hideRefreshing();
                Utils.showMessage(getApplicationContext(), getString(R.string.toast_error_message));
            }
        });
    }

    // update ui methods
    private void updateUi(User user) {
        ImageUtils.loadImage(mIvUserAvatar, user.getPhotoUrl());

        // clear and fill user info list
        mUserInfoList.clear();
        mUserInfoList.add(new UserInfo(user.getFollowers() != null ? user.getFollowers().size() : 0,
                getString(R.string.label_followers)));
        mUserInfoList.add(new UserInfo(user.getFollowings() != null ? user.getFollowings().size() : 0,
                getString(R.string.label_followings)));
        mUserInfoList.add(new UserInfo(user.getCompares() != null ? user.getCompares().size() : 0,
                getString(R.string.label_compares)));

        // recycler view adapter
        ProfileAdapter adapter;
        if (mRecyclerViewProfile.getAdapter() == null) {
            adapter = new ProfileAdapter(mUserInfoList);
            mRecyclerViewProfile.setAdapter(adapter);
        } else {
            adapter = (ProfileAdapter) mRecyclerViewProfile.getAdapter();
            adapter.updateList(mUserInfoList);
            adapter.notifyDataSetChanged();
        }

        adapter.setOnItemClickListener((view, position) -> {
            Intent intent = new Intent(this, FollowersActivity.class);
            intent.putExtra(INTENT_KEY_USER_ID, mUserId);
            if (position == 0) { // followers
                intent.putExtra(FollowersActivity.INTENT_KEY_FOLLOWERS_TITLE,
                        getString(R.string.label_followers));
                intent.putStringArrayListExtra(FollowersActivity.INTENT_KEY_FOLLOWERS_LIST,
                        getFollowersIds(user.getFollowers()));
                startActivity(intent);
            } else if (position == 1) { // followings
                intent.putExtra(FollowersActivity.INTENT_KEY_FOLLOWERS_TITLE,
                        getString(R.string.label_followings));
                intent.putStringArrayListExtra(FollowersActivity.INTENT_KEY_FOLLOWERS_LIST,
                        getFollowingsIds(user.getFollowings()));
                startActivity(intent);
            } else if (position == 2) {
                mNestedScrollView.scrollTo(0, mRecyclerViewProfile.getHeight());
            }
        });

        updateComparesList(user.getCompares().where().findAllSorted(DbFields.DB_COMPARES_DATE,
                Sort.DESCENDING));
    }

    private void updateComparesList(List<Compare> compares) {
        ComparesAdapter adapter;
        if (mRecyclerViewCompares.getAdapter() == null) {
            adapter = new ComparesAdapter(compares);
            mRecyclerViewCompares.setAdapter(adapter);
        } else {
            adapter = (ComparesAdapter) mRecyclerViewCompares.getAdapter();
            adapter.updateList(compares);
            adapter.notifyDataSetChanged();
        }

        // click listener for author
        adapter.setOnItemClickListenerAuthor((view, position) -> {
            Intent userIntent = new Intent(this, ProfileActivity.class);
            userIntent.putExtra(ProfileActivity.INTENT_KEY_USER_ID,
                    compares.get(position).getAuthor().getId());
            userIntent.putExtra(ProfileActivity.INTENT_KEY_USER_NAME,
                    compares.get(position).getAuthor().getFullName());
            startActivity(userIntent);
        });

        // click listener for details
        adapter.setOnItemClickListener((view, position) -> {
            Intent intent = new Intent(this, DetailsActivity.class);
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
                Utils.showMessage(getApplicationContext(),
                        getString(R.string.toast_cannot_like_closed));
            } else if (!Utils.hasInternet(getApplicationContext())) { // if no internet
                isNeedToUnCheck = true;
                Utils.showMessage(getApplicationContext(),
                        getString(R.string.toast_no_internet));
            } else if (compares.get(position).getAuthor().getId().equals(Prefs.getUserId())) { // if user is owner
                isNeedToUnCheck = true;
                Utils.showMessage(getApplicationContext(),
                        getString(R.string.toast_cannot_like_own));
            } else { // update like
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
    }

    // methods for get list of userIds
    private ArrayList<String> getFollowersIds(List<Follower> followers) {
        ArrayList<String> usersIds = new ArrayList<>();
        for (Follower follower : followers) {
            usersIds.add(follower.getFollowerId());
        }
        return usersIds;
    }

    private ArrayList<String> getFollowingsIds(List<Following> followings) {
        ArrayList<String> usersIds = new ArrayList<>();
        for (Following following : followings) {
            usersIds.add(following.getUserId());
        }
        return usersIds;
    }

    // check is authenticated user is already follow current user
    private void isFollow() {
        if (Utils.hasInternet(getApplicationContext())) {
            mQueryIsFollow.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot isFollowSnapshot) {
                    if (isFollowSnapshot.hasChildren()) {
                        mProfileBtnFollow.setText(R.string.btn_label_un_follow);
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
        } else {
            List<Following> authUserFollowings = DbUsersManager.getCurrentUser().getFollowings();
            for (Following authUserFollowing : authUserFollowings) {
                if (authUserFollowing.getUserId().equals(mUserId)) {
                    mProfileBtnFollow.setText(R.string.btn_label_un_follow);
                    break;
                }
            }
        }
    }

    // update follow
    private void updateFollow() {
        mQueryIsFollow.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot isFollowSnapshot) {
                if (isFollowSnapshot.hasChildren()) {
                    unFollowUser();
                } else {
                    followUser();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                hideProgressDialog();
                Utils.showMessage(getApplicationContext(), getString(R.string.toast_error_message));
            }
        });
    }

    // follow current user
    private void followUser() {
        mFirebaseFollowings.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int number = 0;
                if (dataSnapshot != null) {
                    number = (int) dataSnapshot.getChildrenCount();
                }
                // add following to authenticated user
                Map<String, Object> newFollowing = new HashMap<>();
                newFollowing.put(FirebaseConstants.FB_REF_USER_ID, mUserId);
                mFirebaseFollowings.child(String.valueOf(number)).setValue(newFollowing);
                // add follower to current user
                NetworkFollower networkFollower = new NetworkFollower();
                networkFollower.setUserId(mUserId);
                networkFollower.setFollowerId(Prefs.getUserId());
                mFirebaseFollowers.push().setValue(networkFollower, (firebaseError, firebase) -> {
                    // unFreeze button and change text
                    if (firebaseError == null) {
                        mProfileBtnFollow.setText(R.string.btn_label_un_follow);
                    } else {
                        Utils.showMessage(getApplicationContext(),
                                getString(R.string.toast_error_message));
                    }
                    hideProgressDialog();
                    mProfileBtnFollow.setClickable(true);
                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                hideProgressDialog();
                Utils.showMessage(getApplicationContext(), getString(R.string.toast_error_message));
            }
        });
    }

    // unFollow current user
    private void unFollowUser() {
        mQueryFollowers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot followersSnapshot) {

                // delete from followers
                for (DataSnapshot snapshot : followersSnapshot.getChildren()) {
                    NetworkFollower networkFollower = snapshot.getValue(NetworkFollower.class);
                    if (networkFollower.getFollowerId().equals(Prefs.getUserId())) {
                        mFirebaseFollowers.child(snapshot.getKey()).setValue(null);

                        // delete from followings
                        mFirebaseFollowings.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    NetworkFollowing networkFollowing = snapshot
                                            .getValue(NetworkFollowing.class);
                                    if (networkFollowing.getUserId().equals(mUserId)) {
                                        snapshot.getRef().setValue(null, (firebaseError, firebase) -> {

                                            // unFreeze button and change text
                                            if (firebaseError == null) {
                                                mProfileBtnFollow.setText(R.string.btn_label_follow);
                                                fetchProfileFromNetwork();
                                            } else {
                                                Utils.showMessage(getApplicationContext(),
                                                        getString(R.string.toast_error_message));
                                            }
                                            hideProgressDialog();
                                            mProfileBtnFollow.setClickable(true);
                                        });
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                hideProgressDialog();
                                Utils.showMessage(getApplicationContext(),
                                        getString(R.string.toast_error_message));
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                hideProgressDialog();
                Utils.showMessage(getApplicationContext(), getString(R.string.toast_error_message));
            }
        });
    }

    // methods for show and hide progress
    private void hideRefreshing() {
        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.pd_msg_wait_please));
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}