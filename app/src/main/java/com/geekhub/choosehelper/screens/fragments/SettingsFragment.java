package com.geekhub.choosehelper.screens.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.Preference;
import android.view.View;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.screens.activities.MainActivity;
import com.geekhub.choosehelper.services.NotificationComparesService;
import com.geekhub.choosehelper.utils.db.DbComparesManager;
import com.geekhub.choosehelper.utils.services.NotificationCommentedService;
import com.geekhub.choosehelper.utils.services.NotificationLikedService;
import com.geekhub.choosehelper.utils.services.NotificationNewCompareService;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import butterknife.BindString;
import butterknife.ButterKnife;

import static android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

    @BindString(R.string.settings_categories)
    String mSettingsCategories;

    @BindString(R.string.settings_numbers_of_compares)
    String mSettingsNumbersOfCompares;

    @BindString(R.string.settings_notifications_liked)
    String mSettingsNotificationLiked;

    @BindString(R.string.settings_notifications_commented)
    String mSettingsNotificationCommented;

    @BindString(R.string.settings_notifications_f_new_compare)
    String mSettingsNotificationNewCompare;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (key.equals(mSettingsCategories)) { // categories
            MainActivity.sIsNeedToAutoUpdate = true;
            DbComparesManager.clearCompares();
            Set<String> categoriesSet = sharedPreferences.getStringSet(key, new HashSet<>());
            String[] categoriesArray = getActivity().getResources().getStringArray(R.array.categories);
            String catSummary = "";
            if (categoriesSet.size() != 0) {
                int i = 0;
                for (String s : categoriesSet) {
                    if (i == (categoriesSet.size() - 1)) {
                        catSummary = catSummary + categoriesArray[Integer.parseInt(s)];
                    } else {
                        catSummary = catSummary + categoriesArray[Integer.parseInt(s)] + ", ";
                    }
                    i++;
                }
            } else {
                catSummary = getString(R.string.settings_no_categories);
            }
            preference.setSummary(catSummary);
        } else if (key.equals(mSettingsNumbersOfCompares)) { // number of compares
            MainActivity.sIsNeedToAutoUpdate = true;
            DbComparesManager.clearCompares();
            preference.setSummary(String.format(Locale.getDefault(),
                    getString(R.string.settings_number_of_compares),
                    sharedPreferences.getString(key, "")));
        } else if (key.equals(mSettingsNotificationLiked)) { // liked user compare
            String title = sharedPreferences.getString(key, "");
            preference.setSummary((Integer.parseInt(title) == 0) ? getString(R.string.off) : getString(R.string.on));
            if (Integer.parseInt(title) == 1) {
                getActivity().startService(new Intent(getActivity(), NotificationLikedService.class));
            } else {
                getActivity().stopService(new Intent(getActivity(), NotificationLikedService.class));
            }
        } else if (key.equals(mSettingsNotificationCommented)) { // new comment in user compare
            String title = sharedPreferences.getString(key, "");
            preference.setSummary((Integer.parseInt(title) == 0) ? getString(R.string.off) : getString(R.string.on));
            if (Integer.parseInt(title) == 1) {
                getActivity().startService(new Intent(getActivity(), NotificationCommentedService.class));
            } else {
                getActivity().stopService(new Intent(getActivity(), NotificationCommentedService.class));
            }
        } else if (key.equals(mSettingsNotificationNewCompare)) { // new following compare
            String title = sharedPreferences.getString(key, "");
            preference.setSummary((Integer.parseInt(title) == 0) ? getString(R.string.off) : getString(R.string.on));
            if (Integer.parseInt(title) == 1) {
                getActivity().startService(new Intent(getActivity(), NotificationComparesService.class));
            } else {
                //getActivity().stopService(new Intent(getActivity(), NotificationComparesService.class));
            }
        }
    }
}
