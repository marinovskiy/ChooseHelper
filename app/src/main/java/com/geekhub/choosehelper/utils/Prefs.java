package com.geekhub.choosehelper.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.geekhub.choosehelper.R;

public class Prefs {

    // user
    public static final String USER_ID = "prefs_user_id";
    public static final String LOGGED_TYPE = "prefs_logged_type";

    public static final int NOT_LOGIN = 0;
    public static final int GOOGLE_LOGIN = 1;
    public static final int FACEBOOK_LOGIN = 2;
    public static final int FIREBASE_LOGIN = 3;

    // settings
    public static final String SETTINGS_NUMBER_OF_COMPARES = "settings_number_of_compares";
    public static final String SETTINGS_LANGUAGE = "settings_language";
//    public static final String SETTINGS_FILTER_OF_COMPARES = "settings_filter_of_compares";

    public static final int COMPARES_COUNT_MIN = 6;
    public static final int COMPARES_COUNT_TEN = 10;
    public static final int COMPARES_COUNT_TWENTY = 20;
    public static final int COMPARES_COUNT_FIFTY = 50;
    public static final int COMPARES_COUNT_MAX = 66;

    public static final String LANGUAGE_EN = Resources.getSystem().getString(R.string.dialog_settings_language_en);
    public static final String LANGUAGE_UA = Resources.getSystem().getString(R.string.dialog_settings_language_ua);

    public static final String CATEGORIES_FOOD = Resources.getSystem().getString(R.string.dialog_settings_categories_food);
    public static final String CATEGORIES_SPORT = Resources.getSystem().getString(R.string.dialog_settings_categories_sport);
    public static final String CATEGORIES_OTHER = Resources.getSystem().getString(R.string.dialog_settings_categories_other);

    private static SharedPreferences sPrefs;

    private Prefs() {
    }

    public static void init(Context context) {
        if (sPrefs == null) {
            sPrefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        }
    }

    // users
    public static int getLoggedType() {
        return getInt(LOGGED_TYPE);
    }

    public static void setLoggedType(int loggedType) {
        setInt(LOGGED_TYPE, loggedType);
    }

    public static String getUserId() {
        return getString(USER_ID);
    }

    public static void setUserId(String userId) {
        setString(USER_ID, userId);
    }

    // settings
    public static int getNumberOfCompares() {
        return getInt(SETTINGS_NUMBER_OF_COMPARES);
    }

    public static void setNumberOfCompares(int numberOfCompares) {
        setInt(SETTINGS_NUMBER_OF_COMPARES, numberOfCompares);
    }

    public static String getLanguageSettings() {
        return getString(SETTINGS_LANGUAGE);
    }

    public static void setLanguageSettings(String value) {
        setString(SETTINGS_LANGUAGE, value);
    }

    // standard methods
    private static String getString(String key) {
        return sPrefs.getString(key, null);
    }

    private static void setString(String key, String value) {
        sPrefs
                .edit()
                .putString(key, value)
                .apply();
    }

    private static int getInt(String key) {
        return sPrefs.getInt(key, 0);
    }

    private static void setInt(String key, int value) {
        sPrefs
                .edit()
                .putInt(key, value)
                .apply();
    }

}
