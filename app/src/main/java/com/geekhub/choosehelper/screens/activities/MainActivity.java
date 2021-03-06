package com.geekhub.choosehelper.screens.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.User;
import com.geekhub.choosehelper.screens.fragments.AllComparesFragment;
import com.geekhub.choosehelper.screens.fragments.FollowingsComparesFragment;
import com.geekhub.choosehelper.screens.fragments.SearchComparesFragment;
import com.geekhub.choosehelper.ui.adapters.ComparesViewPagerAdapter;
import com.geekhub.choosehelper.utils.ImageUtils;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.Utils;
import com.geekhub.choosehelper.utils.db.DbUsersManager;
import com.geekhub.choosehelper.utils.firebase.FirebaseUsersManager;

import butterknife.Bind;
import butterknife.OnClick;
import io.realm.RealmChangeListener;

import static android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;

public class MainActivity extends BaseSignInActivity implements OnNavigationItemSelectedListener {

    @Bind(R.id.drawer_main)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.navigation_view_main)
    NavigationView mNavigationView;

    @Bind(R.id.toolbar_main)
    Toolbar mToolbar;

    @Bind(R.id.tab_layout_main)
    TabLayout mTabLayout;

    @Bind(R.id.toolbar_shadow_main)
    View mToolbarShadow;

    @Bind(R.id.view_pager_main)
    ViewPager mViewPager;

    @Bind(R.id.main_search_container)
    FrameLayout mSearchContainer;

    @Bind(R.id.fab_add_main)
    FloatingActionButton mFab;

    private View mNavHeaderView;

    // is need to reload compares (using after add new compare etc.)
    public static boolean sIsNeedToAutoUpdate = false;

    // is need to exit from app
    private boolean mIsNeedToExit = false;

    // realm
    private User mCurrentUser;

    private RealmChangeListener mUserListener = () -> {
        if (mCurrentUser != null && mCurrentUser.isLoaded()) {
            updateNavDrawerHeader(mCurrentUser);
        }
    };

    // search fragment
    private SearchComparesFragment mSearchComparesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setup UI elements
        setupToolbar();
        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);
        mNavigationView.setNavigationItemSelectedListener(this);

        // search fragment
        mSearchComparesFragment = SearchComparesFragment.newInstance();

        // requests
        fetchCurrentUserFromDb();
        if (Utils.hasInternet(getApplicationContext())) {
            FirebaseUsersManager.saveUserFromFirebase(Prefs.getUserId());
        }
    }

    @OnClick(R.id.fab_add_main)
    public void onFabClick() {
        if (Utils.hasInternet(this)) {
            startActivity(new Intent(this, AddCompareActivity.class));
        } else {
            Utils.showMessage(this, getString(R.string.toast_no_internet));
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_nav_profile:
                mDrawerLayout.closeDrawers();
                Intent userIntent = new Intent(this, ProfileActivity.class);
                userIntent.putExtra(ProfileActivity.INTENT_KEY_USER_ID, Prefs.getUserId());
                userIntent.putExtra(ProfileActivity.INTENT_KEY_USER_NAME, mCurrentUser.getFullName());
                startActivity(userIntent);
                return true;
            case R.id.action_nav_settings:
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_nav_help:
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(this, HelpActivity.class));
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
        if (mCurrentUser != null && mUserListener != null) {
            mCurrentUser.removeChangeListener(mUserListener);
        }
    }

    @Override
    public void onBackPressed() {

        // close navigation drawer
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        // exit from app
        if (!mIsNeedToExit) {
            mIsNeedToExit = true;
            Utils.showMessage(getApplicationContext(), getString(R.string.toast_click_again_exit));
            new Handler().postDelayed(() -> mIsNeedToExit = false, 2000);
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        SearchView searchView = (SearchView)
                MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setQueryHint(getString(R.string.search_hint_compares));

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search),
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        getSupportFragmentManager().beginTransaction()
                                .remove(mSearchComparesFragment)
                                .commit();
                        mSearchContainer.setVisibility(View.GONE);
                        mTabLayout.setVisibility(View.VISIBLE);
                        mViewPager.setVisibility(View.VISIBLE);
                        return true;
                    }
                });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                mSearchComparesFragment.searchCompares(query);
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
            case R.id.action_search:
                if (Utils.hasInternet(getApplicationContext())) {
                    mTabLayout.setVisibility(View.GONE);
                    mViewPager.setVisibility(View.GONE);
                    mSearchContainer.setVisibility(View.VISIBLE);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_search_container, mSearchComparesFragment,
                                    SearchComparesFragment.class.getSimpleName())
                            .addToBackStack(SearchComparesFragment.class.getSimpleName())
                            .commit();
                } else {
                    Utils.showMessage(getApplicationContext(),
                            getString(R.string.toast_cannot_search));
                }
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(R.drawable.icon_drawer);
            mToolbar.setNavigationOnClickListener(v ->
                    mDrawerLayout.openDrawer(GravityCompat.START));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbarShadow.setVisibility(View.GONE);
        }
    }

    private void updateNavDrawerHeader(User user) {
        try {
            if (mNavHeaderView == null) {
                mNavHeaderView = mNavigationView.inflateHeaderView(R.layout.navigation_header_layout);
            } else {
                mNavigationView.removeHeaderView(mNavHeaderView);
                mNavHeaderView = mNavigationView.inflateHeaderView(R.layout.navigation_header_layout);
            }

            ImageView ivAvatar = (ImageView) mNavHeaderView.findViewById(R.id.nav_header_avatar);
            TextView tvFullName = (TextView) mNavHeaderView.findViewById(R.id.nav_header_name);
            TextView tvEmail = (TextView) mNavHeaderView.findViewById(R.id.nav_header_email);

            if (user.getPhotoUrl() != null) {
                ImageUtils.loadCircleImage(ivAvatar, user.getPhotoUrl());
            } else {
                ivAvatar.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.icon_no_avatar));
            }

            mNavHeaderView.setOnClickListener(v -> {
                mDrawerLayout.closeDrawers();
                Intent userIntent = new Intent(this, ProfileActivity.class);
                userIntent.putExtra(ProfileActivity.INTENT_KEY_USER_ID, Prefs.getUserId());
                userIntent.putExtra(ProfileActivity.INTENT_KEY_USER_NAME, mCurrentUser.getFullName());
                startActivity(userIntent);
            });

            tvFullName.setText(user.getFullName());
            tvEmail.setText(user.getEmail());
        } catch (NullPointerException | IllegalArgumentException ignored) {
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ComparesViewPagerAdapter adapter = new ComparesViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(AllComparesFragment.newInstance(), getString(R.string.tab_label_all));
        adapter.addFragment(FollowingsComparesFragment.newInstance(), getString(R.string.tab_label_followings));
        viewPager.setAdapter(adapter);
    }


    private void fetchCurrentUserFromDb() {
        mCurrentUser = DbUsersManager.getUserById(Prefs.getUserId());
        mCurrentUser.addChangeListener(mUserListener);
    }
}