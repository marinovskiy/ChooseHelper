package com.geekhub.choosehelper.screens.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.Follower;
import com.geekhub.choosehelper.models.db.Following;
import com.geekhub.choosehelper.models.db.User;
import com.geekhub.choosehelper.ui.adapters.UserInfoRecyclerViewAdapter;
import com.geekhub.choosehelper.ui.adapters.UserRecyclerViewAdapter;
import com.geekhub.choosehelper.utils.db.DbUsersManager;

import java.util.List;

import butterknife.Bind;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;

public class FollowersActivity extends BaseSignInActivity {

    public static final String INTENT_KEY_FOLLOWERS_TITLE = "intent_key_followers_title";

    @Bind(R.id.toolbar_followers)
    Toolbar mToolbar;

    @Bind(R.id.toolbar_shadow_followers)
    View mToolbarShadow;

    @Bind(R.id.recycler_view_followers)
    RecyclerView mRecyclerView;

    @Bind(R.id.swipe_to_refresh_followers)
    SwipeRefreshLayout mSwipeToRefreshFollowers;

    private String mToolbarTitle;

    /**
     * realm
     **/
    private String mUserId;

    private User mUser;

    private RealmChangeListener mUserListener = () -> {
        if (mUser != null && mUser.isLoaded()) {
            //updateUi(mUser);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        setupToolbar();
        if (getIntent() != null) {
            mToolbarTitle = getIntent().getStringExtra(INTENT_KEY_FOLLOWERS_TITLE);
            mToolbar.setTitle(mToolbarTitle);

            try {
                if (mToolbarTitle.equals("Followings")) {

                } else if (mToolbarTitle.equals("Followers")) {

                }
            } catch (NullPointerException e) {

            }
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

    /*private void updateUi(User user) {
        UserRecyclerViewAdapter adapter;
        if (mRecyclerView.getAdapter() == null) {
            adapter = new UserRecyclerViewAdapter();
            mRecyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener((view, position) -> {
                Intent intent = new Intent(this, FollowersActivity.class);
                intent.putExtra(ProfileActivity.INTENT_KEY_USER_ID, mUserId);
                startActivity(intent);
            });
        } else {
            adapter = (UserRecyclerViewAdapter) mRecyclerView.getAdapter();
            adapter.updateList();
            adapter.notifyDataSetChanged();
        }
    }

    private void fetchUsersFromDb() {
        mUserList = DbUsersManager.getUserById(mUserId).getFollowers();

        mUserList.addChangeListener(mUsersListener);
    }*/

}
