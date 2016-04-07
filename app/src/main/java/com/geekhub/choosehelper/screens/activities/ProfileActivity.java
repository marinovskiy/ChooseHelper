package com.geekhub.choosehelper.screens.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.Friend;
import com.geekhub.choosehelper.models.db.User;
import com.geekhub.choosehelper.models.network.NetworkUser;
import com.geekhub.choosehelper.ui.adapters.FriendsRecyclerViewAdapter;
import com.geekhub.choosehelper.utils.ImageUtil;
import com.geekhub.choosehelper.utils.ModelConverter;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.Utils;
import com.geekhub.choosehelper.utils.db.DbUsersManager;
import com.geekhub.choosehelper.utils.firebase.FirebaseConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import io.realm.Realm;
import io.realm.RealmChangeListener;

public class ProfileActivity extends BaseSignInActivity {

    public static final String INTENT_KEY_USER_ID = "intent_key_user_id";

    private static final String TAG = ProfileActivity.class.getName();

    @Bind(R.id.profile_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.toolbar_shadow_profile)
    View mToolbarShadow;

    @Bind(R.id.profile_iv_avatar)
    ImageView mIvUserAvatar;

    @Bind(R.id.profile_tv_username)
    TextView mTvUsername;

    @Bind(R.id.profile_tv_email)
    TextView mTvEmail;

    @Bind(R.id.profile_rv_friends)
    RecyclerView mRvFriends;

    @Bind(R.id.profile_sv)
    ScrollView mSvContent;

    private String mUserId;

    private User mUser;

    private RealmChangeListener mUserListener = () -> {
        if (mUser != null && mUser.isLoaded()) {
            updateUi(mUser);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupToolbar();

        if (getIntent() != null) {
            mUserId = getIntent().getStringExtra(INTENT_KEY_USER_ID);
        }

        //mUser = DbUsersManager.getUserNotAsync(Prefs.getUserId());

        fetchProfileFromDb();
        if (Utils.hasInternet(getApplicationContext())) {
            fetchProfileFromNetwork();
        }

        /*Glide.with(getApplicationContext())
                .load(mUser.getPhotoUrl())
                .into(mIvUserAvatar);
        mTvUsername.setText(mUser.getFullName());
        mTvEmail.setText(mUser.getEmail());*/

        setFriendsRecyclerView();
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
        Firebase firebase = new Firebase(FirebaseConstants.FB_REF_MAIN)
                .child(FirebaseConstants.FB_REF_USERS)
                .child(mUserId);
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                NetworkUser networkUser = dataSnapshot.getValue(NetworkUser.class);
                User user = ModelConverter.convertToUser(networkUser, mUserId);
                DbUsersManager.saveUser(user);
                updateUi(user);
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
            mToolbarShadow.setVisibility(View.GONE);
        }
    }

    private void updateUi(User user) {
        ImageUtil.loadImage(mIvUserAvatar, user.getPhotoUrl());
        mTvUsername.setText(user.getFullName());
        mTvEmail.setText(user.getEmail());
    }

    private void setFriendsRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getBaseContext());
        mRvFriends.setLayoutManager(mLayoutManager);

        // Temp
        String link = "http://www.ogo.ua/images/articles/1567/big/1330531172.jpg";
        List<Friend> list = new ArrayList<>();
        list.add(new Friend("Alis Anderson", link));
        list.add(new Friend("Andriy Mechanic", link));
        list.add(new Friend("Jack London", link));
        list.add(new Friend("Nick Cok", link));
        // Will be replace

        FriendsRecyclerViewAdapter mAdapter = new FriendsRecyclerViewAdapter(list);
        mRvFriends.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((view, position) ->
                Toast.makeText(getApplicationContext(), "Position = " + position, Toast.LENGTH_SHORT).show());

        mRvFriends.setFocusable(false);
    }
}
