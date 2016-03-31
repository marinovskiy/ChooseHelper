package com.geekhub.choosehelper.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    public static String convertDateTime(long dateTime) {
        return new SimpleDateFormat("d'th' MMM HH:mm", Locale.getDefault())
                .format(new Date(dateTime));
    }

}
