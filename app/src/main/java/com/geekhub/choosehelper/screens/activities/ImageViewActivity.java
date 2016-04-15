package com.geekhub.choosehelper.screens.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.screens.fragments.ImageViewFragment;
import com.geekhub.choosehelper.ui.adapters.ImagesViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class ImageViewActivity extends BaseSignInActivity {

    public static final String INTENT_KEY_IMAGE_DESCRIPTIONS = "intent_key_image_view_descriptions";
    public static final String INTENT_KEY_IMAGE_URLS = "intent_key_image_image_view_urls";
    public static final String INTENT_KEY_LIKES = "intent_key_image_view_likes";
    public static final String INTENT_KEY_POSITION = "intent_key_image_view_position";

    @Bind(R.id.toolbar_view_image)
    Toolbar mToolbar;

    @Bind(R.id.view_pager_image_view)
    ViewPager mViewPager;

    private List<String> mImageUrls = new ArrayList<>();
    private List<String> mLikes = new ArrayList<>();
    private List<String> mDescriptions = new ArrayList<>();
    private int mPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        if (getIntent() != null) {
            mDescriptions = getIntent().getStringArrayListExtra(INTENT_KEY_IMAGE_DESCRIPTIONS);
            mImageUrls = getIntent().getStringArrayListExtra(INTENT_KEY_IMAGE_URLS);
            mLikes = getIntent().getStringArrayListExtra(INTENT_KEY_LIKES);
            mPosition = getIntent().getIntExtra(INTENT_KEY_POSITION, 0);
            setupToolbar();
            setupViewPager(mViewPager);
            mViewPager.setCurrentItem(mPosition);
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(mDescriptions.get(position));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setupToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mDescriptions.get(mPosition));
            }
            mToolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.icon_back));
            mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ImagesViewPagerAdapter adapter = new ImagesViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(ImageViewFragment.newInstance(mImageUrls.get(0), mLikes.get(0)));
        adapter.addFragment(ImageViewFragment.newInstance(mImageUrls.get(1), mLikes.get(1)));
        viewPager.setAdapter(adapter);
    }
}