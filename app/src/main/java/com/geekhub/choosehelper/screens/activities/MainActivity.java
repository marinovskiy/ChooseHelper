package com.geekhub.choosehelper.screens.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.screens.fragments.AllComparesFragment;
import com.geekhub.choosehelper.screens.fragments.FriendsComparesFragment;
import com.geekhub.choosehelper.screens.fragments.MyComparesFragment;
import com.geekhub.choosehelper.ui.adapters.ComparesViewPagerAdapter;

import butterknife.Bind;

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

    @Bind(R.id.toolbar_shadow_main)
    View mToolbarShadow;

    @Bind(R.id.view_pager_main)
    ViewPager mViewPager;

    @Bind(R.id.fab_add_main)
    FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(mToolbar);
        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbarShadow.setVisibility(View.INVISIBLE);
        }
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddCompareActivity.class));
            }
        });
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

    private void setupViewPager(ViewPager viewPager) {
        ComparesViewPagerAdapter adapter = new ComparesViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(AllComparesFragment.newInstance(), "All");
        adapter.addFragment(FriendsComparesFragment.newInstance(), "Friend's");
        adapter.addFragment(MyComparesFragment.newInstance(), "My");
        viewPager.setAdapter(adapter);
    }

}
