package com.geekhub.choosehelper.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

    //  Application preferences keys
    public static final String LOGGED_TYPE = "logged_type";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_AVATAR_URL = "user_avatar_url";
    public static final String USER_EMAIL = "user_email";

    //  Account logged type key
    public static final int ACCOUNT_NONE = 0;   //  Means that user is not logged in
    public static final int ACCOUNT_GOOGLE_PLUS = 1;
    public static final int ACCOUNT_VK = 2;
    public static final int ACCOUNT_APP = 3;

    private static SharedPreferences sPrefs;

    private Prefs() {
    }

    public static void init(Context context) {
        if (sPrefs == null) {
            sPrefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        }
    }

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

    public static String getUserName() {
        return getString(USER_NAME);
    }

    public static void setUserName(String userName) {
        setString(USER_NAME, userName);
    }

    public static String getUserEmail() {
        return getString(USER_EMAIL);
    }

    public static void setUserEmail(String userEmail) {
        setString(USER_EMAIL, userEmail);
    }

    public static String getUserAvatarUrl() {
        return getString(USER_AVATAR_URL);
    }

    public static void setUserAvatarUrl(String userAvatarUrl) {
        setString(USER_AVATAR_URL, userAvatarUrl);
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
