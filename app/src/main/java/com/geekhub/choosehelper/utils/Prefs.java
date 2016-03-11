package com.geekhub.choosehelper.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

    //  Application preferences keys
    public static final String IS_LOGIN = "is_user_login";

    public static final String USER_ID = "user_id";
    public static final String LOGGED_TYPE = "logged_type";

    //  Account logged type key
    public static final int NOT_LOGIN = 0;   //  Means that user is not logged in
    public static final int GOOGLE_LOGIN = 1;
    public static final int FACEBOOK_LOGIN = 2;
    public static final int FIREBASE_LOGIN = 3;

    private static SharedPreferences sPrefs;

    private Prefs() {
    }

    public static void init(Context context) {
        if (sPrefs == null) {
            sPrefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        }
    }

    public static void setLogin() {
        setBoolean(IS_LOGIN, true);
    }

    public static boolean isLogin() {
        return getBoolean(IS_LOGIN);
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

    private static void setBoolean(String key, boolean value) {
        sPrefs.edit()
                .putBoolean(key, value)
                .apply();
    }

    private static boolean getBoolean(String key) {
        return sPrefs.getBoolean(key, false);
    }

}
