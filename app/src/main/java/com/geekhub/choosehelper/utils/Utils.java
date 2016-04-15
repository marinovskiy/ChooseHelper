package com.geekhub.choosehelper.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.widget.CheckBox;
import android.widget.Toast;

import com.geekhub.choosehelper.R;

public class Utils {

    // internet
    public static boolean hasInternet(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    // dialogs
    public static void showPhotoPickerDialog(Context context,
                                             DialogInterface.OnClickListener onClickListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setCancelable(true)
                .setTitle("Photo")
                .setItems(R.array.photo_variants, onClickListener)
                .create();
        alertDialog.show();
    }

    // show message
    public static void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // get other variant number for operations
    public static int getOtherVariantNumber(int currentVariantNumber) {
        return currentVariantNumber == 0 ? 1 : 0;
    }

    // methods for block and unblock views when like is transacting to server
    public static void blockViews(CardView cardView, CheckBox checkBox1, CheckBox checkBox2) {
        cardView.setClickable(false);
        checkBox1.setClickable(false);
        checkBox2.setClickable(false);
    }

    public static void unBlockViews(CardView cardView, CheckBox checkBox1, CheckBox checkBox2) {
        cardView.setClickable(true);
        checkBox1.setClickable(true);
        checkBox2.setClickable(true);
    }

    public static void blockViews(CheckBox checkBox1, CheckBox checkBox2) {
        checkBox1.setClickable(false);
        checkBox2.setClickable(false);
    }

    public static void unBlockViews(CheckBox checkBox1, CheckBox checkBox2) {
        checkBox1.setClickable(true);
        checkBox2.setClickable(true);
    }
}
