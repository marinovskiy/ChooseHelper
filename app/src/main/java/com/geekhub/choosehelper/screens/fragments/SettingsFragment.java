package com.geekhub.choosehelper.screens.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.Preference;
import android.view.View;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.screens.activities.MainActivity;
import com.geekhub.choosehelper.services.NotificationService;

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

    @BindString(R.string.settings_notifications_f_new_compare)
    String mSettingsNotificationNewCompare;

    private SharedPreferences mSharedPreferences;

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

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Set<String> categoriesSet = mSharedPreferences
                .getStringSet(getString(R.string.settings_categories), new HashSet<>());
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
        Preference preference = findPreference(getString(R.string.settings_categories));
        preference.setSummary(catSummary);
        preference = findPreference(getString(R.string.settings_notifications_f_new_compare));
        preference.setSummary(mSharedPreferences.getString(mSettingsNotificationNewCompare, "Off"));
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
        // categories
        if (key.equals(mSettingsCategories)) {
            MainActivity.sIsNeedToAutoUpdate = true;
            Set<String> categoriesSet = sharedPreferences.getStringSet(key, new HashSet<>());
            String[] categoriesArray = getActivity().getResources()
                    .getStringArray(R.array.categories);
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
        } // number of compares
        else if (key.equals(mSettingsNumbersOfCompares)) {
            MainActivity.sIsNeedToAutoUpdate = true;
            preference.setSummary(String.format(Locale.getDefault(),
                    getString(R.string.settings_number_of_compares),
                    sharedPreferences.getString(key, "")));
        } // new following compare
        else if (key.equals(mSettingsNotificationNewCompare)) {
            String title = sharedPreferences.getString(key, "");
            preference.setSummary((Integer.parseInt(title) == 0) ? getString(R.string.off)
                    : getString(R.string.on));
            if (Integer.parseInt(title) == 1) {
                getActivity().startService(new Intent(getActivity(), NotificationService.class));
            } else {
                getActivity().stopService(new Intent(getActivity(), NotificationService.class));
            }
        }
    }
}
