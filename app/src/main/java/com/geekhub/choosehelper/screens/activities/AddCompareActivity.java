package com.geekhub.choosehelper.screens.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.network.NetworkCompare;
import com.geekhub.choosehelper.models.network.NetworkVariant;
import com.geekhub.choosehelper.screens.fragments.AddCompareFragment;
import com.geekhub.choosehelper.screens.fragments.PreviewFragment;
import com.geekhub.choosehelper.utils.ImageUtils;
import com.geekhub.choosehelper.utils.db.DbUsersManager;
import com.geekhub.choosehelper.utils.firebase.FirebaseComparesManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class AddCompareActivity extends BaseSignInActivity {

    @Bind(R.id.toolbar_add_compare)
    Toolbar mToolbar;

    @Bind(R.id.toolbar_shadow_add_compare)
    View mToolbarShadow;

    @Bind(R.id.add_compare_container_preview)
    FrameLayout mPreviewContainer;

    private int mMenuType = 0;

    private AddCompareFragment mAddCompareFragment;

    private NetworkCompare mNewCompare = new NetworkCompare();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_compare);
        setupToolbar();

        mAddCompareFragment = AddCompareFragment.newInstance();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.add_compare_container_preview,
                            mAddCompareFragment,
                            AddCompareFragment.class.getSimpleName())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.label_new_compare);
        }
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.icon_cancel));
        mMenuType = 0;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mMenuType == 0) {
            getMenuInflater().inflate(R.menu.menu_forward, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_done, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_forward:

                mNewCompare = mAddCompareFragment.getNewCompare();

                String question = mNewCompare.getQuestion();
                String firstVariant = mNewCompare.getVariants().get(0).getDescription();
                String secondVariant = mNewCompare.getVariants().get(1).getDescription();

                if (question.equals("") || firstVariant.equals("") || secondVariant.equals("")) {
                    Toast.makeText(this, R.string.toast_empty_fields, Toast.LENGTH_SHORT).show();
                } else {
                    showComparePreview();
                }
                return true;
            case R.id.action_done:

                List<NetworkVariant> prevVariants = mNewCompare.getVariants();
                String firstImgUrl = null;
                String secondImgUrl = null;

                if (prevVariants.get(0).getImageUrl() != null) {
                    firstImgUrl = ImageUtils.getUrlAndStartUpload(prevVariants.get(0).getImageUrl(),
                            getApplicationContext());
                }
                if (prevVariants.get(1).getImageUrl() != null) {
                    secondImgUrl = ImageUtils.getUrlAndStartUpload(prevVariants.get(1).getImageUrl(),
                            getApplicationContext());
                }

                List<NetworkVariant> variants = new ArrayList<>();
                variants.add(new NetworkVariant(firstImgUrl, prevVariants.get(0).getDescription()));
                variants.add(new NetworkVariant(secondImgUrl, prevVariants.get(1).getDescription()));

                mNewCompare.setVariants(variants);

                FirebaseComparesManager.addNewCompare(mNewCompare);
                MainActivity.sIsNeedToAutoUpdate = true;
                finish();
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
            mToolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.icon_cancel));
            mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbarShadow.setVisibility(View.GONE);
        }
    }

    private void showComparePreview() {
        mMenuType = 1;
        invalidateOptionsMenu();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.label_preview);
        }
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.icon_back));

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.add_compare_container_preview,
                        PreviewFragment.newInstance(DbUsersManager.getCurrentUser(), mNewCompare),
                        PreviewFragment.class.getSimpleName())
                .addToBackStack(PreviewFragment.class.getSimpleName())
                .commit();
    }
}