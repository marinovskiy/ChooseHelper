package com.geekhub.choosehelper.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

    public static final String PREFS_IS_EXPIRED = "prefs_is_expired";
    public static final String PREFS_LOGGED_TYPE = "prefs_logged_type";
    public static final int PREFS_GOOGLE_PLUS = 0;
    public static final int PREFS_VK = 1;
    public static final int PREFS_APP_ACCOUNT = 2;

    public static final String PREFS_USER_ID = "prefs_user_id";
    public static final String PREFS_USER_NAME = "prefs_user_name";
    public static final String PREFS_USER_AVATAR_URL = "prefs_user_avatar_url";

    private static SharedPreferences sPrefs = null;

    private Prefs() {

    }

    public static void init(Context applicationContext) {
        if (sPrefs == null) {
            sPrefs = applicationContext.getSharedPreferences(applicationContext.getPackageName(),
                    Context.MODE_PRIVATE);
        }
    }

    public static boolean isExpired() {
        return getBoolean(PREFS_IS_EXPIRED);
    }

    public static void setExpired(boolean isExpired) {
        setBoolean(PREFS_IS_EXPIRED, isExpired);
    }

    public static int getLoggedType() {
        return getInt(PREFS_LOGGED_TYPE);
    }

    public static void setLoggedType(int loggedType) {
        setInt(PREFS_LOGGED_TYPE, loggedType);
    }

    public static String getUserId() {
        return getString(PREFS_USER_ID);
    }

    public static void setUserId(String userId) {
        setString(PREFS_USER_ID, userId);
    }

    public static String getUserName() {
        return getString(PREFS_USER_NAME);
    }

    public static void setUserName(String userName) {
        setString(PREFS_USER_NAME, userName);
    }

    public static String getUserAvatarUrl() {
        return getString(PREFS_USER_AVATAR_URL);
    }

    public static void setUserAvatarUrl(String userAvatarUrl) {
        setString(PREFS_USER_AVATAR_URL, userAvatarUrl);
    }

    private static boolean getBoolean(String key) {
        return sPrefs.getBoolean(key, false);
    }

    private static void setBoolean(String key, boolean value) {
        sPrefs
                .edit()
                .putBoolean(key, value)
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

    private static String getString(String key) {
        return sPrefs.getString(key, null);
    }

    private static void setString(String key, String value) {
        sPrefs
                .edit()
                .putString(key, value)
                .apply();
    }

}
