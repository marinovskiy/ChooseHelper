package com.geekhub.choosehelper.screens.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.utils.ImageUtils;

import butterknife.Bind;

public class ViewImageActivity extends BaseSignInActivity {

    public static final String INTENT_KEY_IMAGE_URL = "intent_key_image_url";
    public static final String INTENT_KEY_IMAGE_TITLE = "intent_key_image_title";

    @Bind(R.id.toolbar_view_image)
    Toolbar mToolbar;

    @Bind(R.id.view_image_iv)
    ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        if (getIntent() != null) {
            ImageUtils.loadImage(mImage, getIntent().getStringExtra(INTENT_KEY_IMAGE_URL));
            setupToolbar(getIntent().getStringExtra(INTENT_KEY_IMAGE_TITLE));
        }
    }

    private void setupToolbar(String title) {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(title);
            }
            mToolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.icon_back));
            mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }
}
