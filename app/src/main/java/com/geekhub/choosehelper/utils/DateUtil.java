package com.geekhub.choosehelper.utils;

import android.text.format.DateFormat;

import java.util.Date;

/**
 * Created by Alex on 06.03.2016.
 */
public class DateUtil {

    public static long getCurrentDateTime() {
        return System.currentTimeMillis();
    }

    public static String convertDateTime(long dateTime) {
        return DateFormat.format("D'th' MMM yyyy H m", new Date(dateTime)).toString();
    }

}
