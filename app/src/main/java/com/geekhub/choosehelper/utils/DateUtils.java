package com.geekhub.choosehelper.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static String convertDateTime(long dateTime) {
        return new SimpleDateFormat("d MMM HH:mm", Locale.getDefault()).format(new Date(dateTime));
    }
}