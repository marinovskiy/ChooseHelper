package com.geekhub.choosehelper.screens.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.utils.BaseSignInActivity;
import com.geekhub.choosehelper.utils.AppPreferences;

import butterknife.Bind;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ProfileActivity extends BaseSignInActivity {

    @Bind(R.id.toolbar_profile)
    Toolbar mToolbar;

    @Bind(R.id.profile_iv_user_avatar)
    ImageView mIvUserAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupToolbar();
        Glide
                .with(this)
                .load(AppPreferences.getUserAvatarUrl())
                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .into(mIvUserAvatar);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(AppPreferences.getUserName());
            getSupportActionBar().setHomeAsUpIndicator(ContextCompat
                    .getDrawable(getApplicationContext(), R.drawable.icon_arrow_back));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
