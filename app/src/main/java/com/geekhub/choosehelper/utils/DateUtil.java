package com.geekhub.choosehelper.utils;

import android.text.format.DateFormat;

import java.util.Date;

public class DateUtil {

    public static String convertDateTime(long dateTime) {
        return DateFormat.format("d'th' MMM yyyy H m", new Date(dateTime)).toString();
    }

}
