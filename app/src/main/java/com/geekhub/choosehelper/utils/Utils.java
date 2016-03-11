package com.geekhub.choosehelper.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.widget.Toast;

import com.geekhub.choosehelper.R;

import java.io.ByteArrayOutputStream;

/**
 * Created by Alex on 10.03.2016.
 */
public class Utils {

    public static void showErrorDialog(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    public static void showErrorMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showPhotoPickerDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Photo")
                .setItems(R.array.photo_picker_array, (dialog, which) -> {
                    Toast.makeText(context, "Pos = " + which, Toast.LENGTH_SHORT).show();
//                    switch (which) {
//                        case 0:
//                            Toast.makeText(context, "Pos = " + which, Toast.LENGTH_SHORT).show();
//                            break;
//                        case 1:
//                            Toast.makeText(context, "Pos = " + which, Toast.LENGTH_SHORT).show();
//                            break;
//                    }
                })
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    public static String convertBitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap convertStringToBitmap(String strBitmap) {
        byte[] bytes = Base64.decode(strBitmap, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
