package com.geekhub.choosehelper.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.geekhub.choosehelper.R;

import java.io.ByteArrayOutputStream;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Alex on 10.03.2016.
 */
public class Utils {

    public static void loadImageByUri(ImageView imageView, Uri uri) {
        Glide.with(imageView.getContext())
                .load(uri)
                .into(imageView);
    }

    public static void loadCircleImageByUri(ImageView imageView, Uri uri) {
        Glide.with(imageView.getContext())
                .load(uri)
                .bitmapTransform(new CropCircleTransformation(imageView.getContext()))
                .into(imageView);
    }

    public static void loadImageByUrl(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .into(imageView);
    }

    public static void loadCircleImageByUrl(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .bitmapTransform(new CropCircleTransformation(imageView.getContext()))
                .into(imageView);
    }

    public static void showErrorMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showPhotoDialog(Context context, DialogInterface.OnClickListener onClickListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("Photo")
                .setItems(R.array.photo_variants, onClickListener)
                .create();
        alertDialog.show();
    }

}
