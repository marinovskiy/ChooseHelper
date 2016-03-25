package com.geekhub.choosehelper.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String LOGGED_TYPE = "logged_type";

    public static final int NOT_LOGIN = 0;
    public static final int SKIP_LOGIN = 1;
    public static final int GOOGLE_LOGIN = 2;
    public static final int FACEBOOK_LOGIN = 3;
    public static final int FIREBASE_LOGIN = 4;

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

    public static void setUserName(String Name) {
        setString(USER_NAME, Name);
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
