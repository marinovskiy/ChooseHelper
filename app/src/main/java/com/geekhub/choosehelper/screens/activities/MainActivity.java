package com.geekhub.choosehelper.screens.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.User;
import com.geekhub.choosehelper.screens.fragments.AllComparesFragment;
import com.geekhub.choosehelper.screens.fragments.FriendsComparesFragment;
import com.geekhub.choosehelper.ui.adapters.ComparesViewPagerAdapter;
import com.geekhub.choosehelper.utils.ImageUtil;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.db.DbUsersManager;

import butterknife.Bind;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;

public class MainActivity extends BaseSignInActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String INTENT_KEY_USER_NAME = "intent_key_user_name";

    @Bind(R.id.drawer_main)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.navigation_view_main)
    NavigationView mNavigationView;

    @Bind(R.id.toolbar_main)
    Toolbar mToolbar;

    @Bind(R.id.main_tab_layout)
    TabLayout mTabLayout;

//    @Bind(R.id.toolbar_shadow_main)
//    View mToolbarShadow;

    @Bind(R.id.view_pager_main)
    ViewPager mViewPager;

    private User mCurrentUser;

    private RealmChangeListener mUserListener;

    private RealmChangeListener mRealmChangeListener = new RealmChangeListener() {
        @Override
        public void onChange() {
            if (mCurrentUser != null && mCurrentUser.isLoaded()) {
                setupNavDrawerHeader(mCurrentUser);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /** setup UI elements **/
        setupToolbar();
        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);
        mNavigationView.setNavigationItemSelectedListener(this);

        /** work with realm **/
        mUserListener = () -> {
            if (mCurrentUser != null && mCurrentUser.isLoaded()) {
                setupNavDrawerHeader(mCurrentUser);
            }
        };
        getCurrentUserInfo();

        //FirebaseComparesManager.getTwentyCompares();
    }

    @OnClick(R.id.fab_add_main)
    public void onFabClick() {
        if (mCurrentUser != null) {
            Intent intent = new Intent(this, AddCompareActivity.class);
            intent.putExtra(INTENT_KEY_USER_NAME, mCurrentUser.getFullName());
            startActivity(intent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_nav_profile:
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            case R.id.action_nav_about:
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.action_nav_logout:
                logout();
                startSignInActivity();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Realm.getDefaultInstance().removeAllChangeListeners();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            finish();
            // TODO: add toast: "click again for exit"
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                // TODO: search
                return true;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(R.drawable.icon_drawer);
            mToolbar.setNavigationOnClickListener(v -> mDrawerLayout.openDrawer(GravityCompat.START));
        }
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbarShadow.setVisibility(View.INVISIBLE);
        }*/
    }

    private void setupNavDrawerHeader(User user) {
        View headerView = mNavigationView.inflateHeaderView(R.layout.navigation_header_layout);
        ImageView ivAvatar = (ImageView) headerView.findViewById(R.id.nav_header_avatar);
        TextView tvFullName = (TextView) headerView.findViewById(R.id.nav_header_name);
        TextView tvEmail = (TextView) headerView.findViewById(R.id.nav_header_email);
        tvFullName.setText(user.getFullName());
        tvEmail.setText(user.getEmail());
        if (user.getPhotoUrl() != null) {
            ImageUtil.loadCircleImage(ivAvatar, user.getPhotoUrl());
        } else {
            ivAvatar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.icon_no_avatar));
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ComparesViewPagerAdapter adapter = new ComparesViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(AllComparesFragment.newInstance(), "All");
        adapter.addFragment(FriendsComparesFragment.newInstance(), "Friend's");
        //adapter.addFragment(MyComparesFragment.newInstance(), "My");
        viewPager.setAdapter(adapter);
    }

    private void getCurrentUserInfo() {
        mCurrentUser = DbUsersManager.getUserAsync(Prefs.getUserId());
        mCurrentUser.addChangeListener(mUserListener);
    }
}
