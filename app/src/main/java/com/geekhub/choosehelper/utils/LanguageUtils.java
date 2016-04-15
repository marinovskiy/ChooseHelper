package com.geekhub.choosehelper.utils;


import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Locale;

public class LanguageUtils {

    private static final String TAG = LanguageUtils.class.getName();

    public static final String CODE_EN = "en";
    public static final String CODE_UK = "uk";
    public static final String CODE_RUS = "ru";

    public static void changeLocale(String title, Context context) {
        Resources res = context.getResources();
        DisplayMetrics metrics = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = new Locale(getLanguageCode(title));
        res.updateConfiguration(conf, metrics);

        Log.d(TAG, "Locale has been changed. Current: " + conf.locale.toString());
    }

    private static String getLanguageCode(String title) {
        Log.d(TAG, "TITLE: " + title);
        switch (title) {
            case "English":
                return CODE_EN;
            case "Українська":
                return CODE_UK;
            case "Русский":
                return CODE_RUS;
            default:
                return CODE_EN;
        }
    }
}
