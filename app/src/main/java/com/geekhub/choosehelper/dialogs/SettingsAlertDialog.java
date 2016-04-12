package com.geekhub.choosehelper.dialogs;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.ui.Settings;
import com.geekhub.choosehelper.screens.activities.SettingsActivity;
import com.geekhub.choosehelper.utils.Prefs;

public class SettingsAlertDialog {

    private final String[] mComparesCount = {"6", "10", "20", "50", "66"};

    private Context mContext;
    private Settings mSettings;

    public SettingsAlertDialog(Context mContext, Settings mSettings) {
        this.mContext = mContext;
        this.mSettings = mSettings;
    }

    public void openSettingsDialog() {
        AlertDialog dialog;

        if (mSettings.getTitle().equals(mContext.getString(R.string.ad_title_compares_count))) {
            dialog = createDialog(Prefs.SETTINGS_NUMBER_OF_COMPARES,
                    mContext.getString(R.string.ad_title_compares_count), mComparesCount);
            dialog.show();
        } else {
            Toast.makeText(mContext, "NO!", Toast.LENGTH_SHORT).show();
        }
    }

    // Create a specifics alert dialog
    private AlertDialog createDialog(String key, String title, String[] variants) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title)
                .setIcon(R.drawable.icon_settings_dark)
                .setCancelable(false)
                .setNegativeButton(R.string.ad_btn_cancel, (dialog, which) -> {
                    dialog.cancel();
                })
                .setSingleChoiceItems(variants, -1, new ApplyClickListener(key));

        return builder.create();
    }

    /*
     ** Work with preferences
     */

    private class ApplyClickListener implements AlertDialog.OnClickListener {

        private String mPrefsKey;

        public ApplyClickListener(String prefsKey) {
            this.mPrefsKey = prefsKey;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (mPrefsKey.equals(Prefs.SETTINGS_NUMBER_OF_COMPARES)) {
                Prefs.setNumberOfCompares(Integer.parseInt(mComparesCount[which]));
            }
            dialog.cancel();
            SettingsActivity.updateRV();
        }
    }
}
