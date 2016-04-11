package com.geekhub.choosehelper.dialogs;


import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.ui.Settings;
import com.geekhub.choosehelper.utils.Prefs;

public class SettingsAlertDialog {

    private static String[] sVariantsCompCount = {"" + Prefs.COMPARES_COUNT_MIN, "" + Prefs.COMPARES_COUNT_TEN, ""
            + Prefs.COMPARES_COUNT_TWENTY, "" + Prefs.COMPARES_COUNT_FIFTY, "" + Prefs.COMPARES_COUNT_MAX};

    private static String[] sVariantsLanguage = {Prefs.LANGUAGE_UA, Prefs.LANGUAGE_EN};

    private static int dialogType;


    public static AlertDialog createDialog(Context context, Settings settings) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(settings.getTitle())
                .setIcon(R.drawable.icon_settings_dark)
                .setCancelable(false)
                .setNegativeButton(R.string.dialog_settings_btn_cancel, (dialog, which) -> {
                    dialog.cancel();
                })
                .setSingleChoiceItems(generateItems(settings.getTitle()), -1, new ApplyClickListener());

        return builder.create();
    }

    private static String[] generateItems(String settingsTitle) {
        if (settingsTitle.equals(
                Resources.getSystem().getString(R.string.dialog_settings_title_compares_count))) {
            dialogType = 0;
            return sVariantsCompCount;
        } else if (settingsTitle.equals(
                Resources.getSystem().getString(R.string.dialog_settings_title_language))) {
            dialogType = 1;
            return sVariantsLanguage;
        }
        return new String[]{"..."};
    }

    private static class ApplyClickListener implements AlertDialog.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (dialogType) {
                case 0: {
                    Prefs.setNumberOfCompares(Integer.parseInt(sVariantsCompCount[which]));
                    break;
                }
                case 1:
                    Prefs.setLanguageSettings(sVariantsLanguage[which]);
                    break;
            }
        }
    }
}
