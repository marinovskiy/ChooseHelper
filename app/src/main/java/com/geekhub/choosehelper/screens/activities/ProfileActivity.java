package com.geekhub.choosehelper.screens.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.Friend;
import com.geekhub.choosehelper.models.db.User;
import com.geekhub.choosehelper.ui.adapters.FriendsRecyclerViewAdapter;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.db.DbUsersManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class ProfileActivity extends BaseSignInActivity {

    private static final String TAG = ProfileActivity.class.getName();

    @Bind(R.id.profile_toolbar)
    Toolbar mToolbar;

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

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Get user profile from DB
        mUser = DbUsersManager.getUserNotAsync(Prefs.getUserId());

        // Set user info into UI
        Glide.with(getApplicationContext())
                .load(mUser.getPhotoUrl())
                .into(mIvUserAvatar);
        mTvUsername.setText(mUser.getFullName());
        mTvEmail.setText(mUser.getEmail());

        // Init toolbar, friends recycler view
        setToolbar(mToolbar, "Profile");
        setFriendsRecyclerView();
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

    private void setToolbar(Toolbar toolbar, String title) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);

            // Set toolbar options
            if (getSupportActionBar() != null) {
//                toolbar.setTitle(title);
                getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(
                        getApplicationContext(), R.drawable.icon_arrow_back_white));
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        } else {
            Log.d(TAG, "Can`t setup toolbar: Toolbar is null");
        }
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
