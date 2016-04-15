package com.geekhub.choosehelper.screens.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.ui.Tip;
import com.geekhub.choosehelper.ui.adapters.TipAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class HelpActivity extends BaseSignInActivity {

    @Bind(R.id.toolbar_help)
    Toolbar mToolbar;

    @Bind(R.id.toolbar_shadow_help)
    View mToolbarShadow;

    @Bind(R.id.help_rv)
    RecyclerView mRvTips;

    private final int[] images = {R.drawable.logo, R.drawable.img_compare, R.drawable.img_create_compare,
            R.drawable.img_search, R.drawable.img_settings, R.drawable.logo};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setupToolbar();
        setupRV(initTip());
    }

    private void setupToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.icon_back));
            mToolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbarShadow.setVisibility(View.GONE);
        }
    }

    private void setupRV(List<Tip> tips) {
        mRvTips.setLayoutManager(new LinearLayoutManager(this));
        mRvTips.setAdapter(new TipAdapter(tips, getBaseContext()));
    }

    private List<Tip> initTip() {
        List<Tip> list = new ArrayList<>();
        for (String title : getBaseContext().getResources().getStringArray(R.array.help_titles)) {
            Tip t = new Tip();
            t.setTitle(title);
            list.add(t);
        }
        int i = 0;
        for (String desc : getBaseContext().getResources().getStringArray(R.array.help_text)) {
            list.get(i).setDescription(desc);
            list.get(i).setImage(images[i++]);
        }
        return list;
    }
}
