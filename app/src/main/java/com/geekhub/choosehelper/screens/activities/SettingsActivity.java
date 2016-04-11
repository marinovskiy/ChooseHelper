package com.geekhub.choosehelper.screens.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.dialogs.SettingsAlertDialog;
import com.geekhub.choosehelper.models.ui.Settings;
import com.geekhub.choosehelper.ui.adapters.SettingsAdapter;
import com.geekhub.choosehelper.utils.Prefs;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class SettingsActivity extends BaseSignInActivity {

    private static final String TAG = SettingsActivity.class.getName();

    @Bind(R.id.toolbar_settings)
    Toolbar mToolbar;

    @Bind(R.id.recycler_view_settings)
    RecyclerView mRecyclerView;

    @Bind(R.id.toolbar_shadow_settings)
    View mToolbarShadow;

    private List<Settings> mSettingsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupToolbar();
        mSettingsList = generateSettings();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        SettingsAdapter adapter = new SettingsAdapter(mSettingsList);
        mRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((view, p) -> {
            SettingsAlertDialog dialog =
                    new SettingsAlertDialog(SettingsActivity.this, mSettingsList.get(p));
            dialog.openSettingsDialog();
            Log.d(TAG, "In preferences: " + mSettingsList.get(p).getTitle()
                    + " value/" + Prefs.getNumberOfCompares());
        });
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

    private List<Settings> generateSettings() {
        List<Settings> settingsList = new ArrayList<>();
        settingsList.add(new Settings(Prefs.SETTINGS_NUMBER_OF_COMPARES, String.valueOf(Prefs.getNumberOfCompares())));
        settingsList.add(new Settings("Language", "English"));
        settingsList.add(new Settings("Filters", "..."));
        return settingsList;
    }
}
