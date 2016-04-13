package com.geekhub.choosehelper.screens.fragments;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geekhub.choosehelper.R;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import butterknife.BindString;
import butterknife.ButterKnife;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

    @BindString(R.string.settings_categories)
    String mSettingsCategories;

    @BindString(R.string.settings_numbers_of_compares)
    String mSettingsNumbersOfCompares;

    @BindString(R.string.settings_language)
    String mSettingsLanguage;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        /*Set<String> categoriesSet = sp.getStringSet(mSettingsCategories, new HashSet<>());
        String categoriesString = "";
        if (categoriesSet.size() != 0) {
            int i = 0;
            for (String s : categoriesSet) {
                if (i == (categoriesSet.size() - 1)) {
                    categoriesString = categoriesString + s;
                } else {
                    categoriesString = categoriesString + s + ", ";
                }
                i++;
            }
        } else {
            categoriesString = "No categories selected";
        }*/
    }

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        MultiSelectListPreference listPreference = (MultiSelectListPreference) getPreferenceManager().findPreference(mSettingsCategories);
        listPreference.setSummary("test");
        return super.onCreateView(inflater, container, savedInstanceState);
    }*/

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
        if (key.equals(mSettingsCategories)) {
            AllComparesFragment.sIsNeedToAutoUpdate = true;
            Set<String> categoriesSet = sharedPreferences.getStringSet(key, new HashSet<>());
            String categoriesString = "";
            if (categoriesSet.size() != 0) {
                int i = 0;
                for (String s : categoriesSet) {
                    if (i == (categoriesSet.size() - 1)) {
                        categoriesString = categoriesString + s;
                    } else {
                        categoriesString = categoriesString + s + ", ";
                    }
                    i++;
                }
            } else {
                categoriesString = "No categories selected";
            }
            preference.setSummary(categoriesString);
        } else if (key.equals(mSettingsNumbersOfCompares)) {
            AllComparesFragment.sIsNeedToAutoUpdate = true;
            preference.setSummary(String.format(Locale.getDefault(), "Show %s compares in main",
                    sharedPreferences.getString(key, "")));
        } else if (key.equals(mSettingsLanguage)) {
            preference.setSummary(sharedPreferences.getString(key, ""));
        }
    }
}
