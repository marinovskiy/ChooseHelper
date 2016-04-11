package com.geekhub.choosehelper.screens.activities;

import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

    @Bind(R.id.toolbar_settings)
    Toolbar mToolbar;

    @Bind(R.id.recycler_view_settings)
    RecyclerView mRecyclerView;

    @Bind(R.id.toolbar_shadow_settings)
    View mToolbarShadow;

    private List<Settings> mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupToolbar();

        mSettings = generateSettings();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        SettingsAdapter adapter = new SettingsAdapter(mSettings);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((view, position) -> {
            AlertDialog dialog = SettingsAlertDialog
                    .createDialog(SettingsActivity.this, mSettings.get(position));
            dialog.show();
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
        settingsList.add(new Settings(
                Resources.getSystem().getString(R.string.dialog_settings_title_compares_count),
                String.valueOf(Prefs.getNumberOfCompares())));
        settingsList.add(new Settings(
                Resources.getSystem().getString(R.string.dialog_settings_title_language),
                Prefs.getLanguageSettings()));
//        settingsList.add(new Settings(
//                Resources.getSystem().getString(R.string.dialog_settings_title_categories),
//                "..."));
        return settingsList;
    }
}
