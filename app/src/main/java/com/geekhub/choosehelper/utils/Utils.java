package com.geekhub.choosehelper.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.geekhub.choosehelper.R;

public class Utils {

    /**
     * internet
     **/
    public static boolean hasInternet(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * dialogs
     **/
    public static void showPhotoPickerDialog(Context context,
                                             DialogInterface.OnClickListener onClickListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("Photo")
                .setItems(R.array.photo_variants, onClickListener)
                .create();
        alertDialog.show();
    }

    /**
     * other
     **/
    public static void showErrorMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
