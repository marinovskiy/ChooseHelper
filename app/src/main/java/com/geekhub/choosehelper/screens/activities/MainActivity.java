package com.geekhub.choosehelper.screens.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.keys.Key;
import com.geekhub.choosehelper.utils.Prefs;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.vk.sdk.VKSdk;

import butterknife.Bind;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.drawer_main)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.navigation_view_main)
    NavigationView mNavigationView;

    @Bind(R.id.toolbar_main)
    Toolbar mToolbar;

    @Bind(R.id.toolbar_shadow_main)
    View mToolbarShadow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(mToolbar);
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
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_nav_profile:
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            case R.id.action_nav_logout:

                if (Prefs.getLoggedType() == Prefs.PREFS_VK)
                    VKSdk.logout();

                if (Prefs.getLoggedType() == Prefs.PREFS_GOOGLE_PLUS) {
                    Auth.GoogleSignInApi.signOut(SignInActivity.getGoogleApiClient())
                            .setResultCallback(
                                    new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(Status status) {
                                            Log.d(Key.LT_MESSAGE, "You are logout");
                                        }
                                    });

                }

                Prefs.setExpired(false);
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
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
}
