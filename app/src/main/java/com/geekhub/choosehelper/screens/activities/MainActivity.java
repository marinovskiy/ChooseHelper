package com.geekhub.choosehelper.screens.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.User;
import com.geekhub.choosehelper.screens.fragments.AllComparesFragment;
import com.geekhub.choosehelper.screens.fragments.FriendsComparesFragment;
import com.geekhub.choosehelper.screens.fragments.MyComparesFragment;
import com.geekhub.choosehelper.ui.adapters.ComparesViewPagerAdapter;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.db.DbUsersManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class MainActivity extends BaseSignInActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //  Logs
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

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

    /*@Bind(R.id.fab_add_main)
    FloatingActionButton mFab;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setUpToolbar();
        setUpNavDrawer();
        mNavigationView.setNavigationItemSelectedListener(this);
        setUpNavHeader();
        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //mToolbarShadow.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.fab_add_main)
    public void onClick() {
        startActivity(new Intent(MainActivity.this, AddCompareActivity.class));
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_nav_profile:
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            case R.id.action_nav_logout:
                signOut();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
    public void doAfterSignOut() {
        super.doAfterSignOut();
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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

    private void setUpToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }

    private void setUpNavDrawer() {
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationIcon(R.drawable.icon_drawer);
            mToolbar.setNavigationOnClickListener(v -> mDrawerLayout.openDrawer(GravityCompat.START));
        }
    }

    private void setUpNavHeader() {
        User user = DbUsersManager.getUser(Prefs.getUserId());
        if (user != null && user.isLoaded()) {
            Log.i(LOG_TAG, "setUpNavHeader: " + user.getFullName());
            Log.i(LOG_TAG, "setUpNavHeader: " + user.getEmail());
            Log.i(LOG_TAG, "setUpNavHeader: " + user.getPhotoUrl());
        }
        View headerView = mNavigationView.inflateHeaderView(R.layout.navigation_header_layout);
        ImageView ivAvatar = (ImageView) headerView.findViewById(R.id.nav_header_avatar);
        TextView tvFullName = (TextView) headerView.findViewById(R.id.nav_header_name);
        TextView tvEmail = (TextView) headerView.findViewById(R.id.nav_header_email);
        tvFullName.setText("Name");
        tvEmail.setText("Email");
        Glide.with(this)
                .load(R.drawable.test_img)
                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .into(ivAvatar);
    }

    private void setupViewPager(ViewPager viewPager) {
        ComparesViewPagerAdapter adapter = new ComparesViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(AllComparesFragment.newInstance(), "All");
        adapter.addFragment(FriendsComparesFragment.newInstance(), "Friend's");
        adapter.addFragment(MyComparesFragment.newInstance(), "My");
        viewPager.setAdapter(adapter);
    }
}
