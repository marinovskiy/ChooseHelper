package com.geekhub.choosehelper.screens.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.User;

import butterknife.Bind;
import io.realm.RealmChangeListener;

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

//    /** realm **/
//    private String mUserId;
//
//    private User mUser;
//
//    private RealmChangeListener mUserListener = () -> {
//        if (mUser != null && mUser.isLoaded()) {
//            Toast.makeText(getApplicationContext(), "yeah!!!", Toast.LENGTH_SHORT).show();
//            updateUi(mUser);
//        }
//    };

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

}
