package com.geekhub.choosehelper.utils;

import android.text.format.DateFormat;

import java.util.Date;

public class DateUtil {

    public static String convertDateTime(long dateTime) {
        return DateFormat.format("D'th' MMM yyyy H m", new Date(dateTime)).toString();
    }

}
