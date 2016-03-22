package com.geekhub.choosehelper.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.geekhub.choosehelper.screens.activities.SignUpActivity;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;

/**
 * Created by Alex on 22.03.2016.
 */
public class AmazonUtil {

    public static final String POOL_ID = "us-east-1:38c82a5b-7f63-47d8-b9d8-3ea2ac6b4291";
    public static final String BASE_URL = "https://s3.amazonaws.com/choosehelper";
    public static final String BUCKET_NAME = "choosehelper";
    public static final String FOLDER_AVATARS = "/avatars";
    public static final String FOLDER_IMAGES = "/images";

    private static CognitoCachingCredentialsProvider sCredentialsProvider;
    private static AmazonS3Client sAmazonS3Client;
    private static TransferUtility sTransferUtility;

    private static CognitoCachingCredentialsProvider getCredentialsProvider(Context context) {
        if (sCredentialsProvider == null) {
            sCredentialsProvider = new CognitoCachingCredentialsProvider(
                    context.getApplicationContext(),
                    POOL_ID,
                    Regions.US_EAST_1
            );
        }
        return sCredentialsProvider;
    }

    private static AmazonS3Client getAmazonS3Client(Context context) {
        if (sAmazonS3Client == null) {
            sAmazonS3Client = new AmazonS3Client(getCredentialsProvider(context.getApplicationContext()));
            sAmazonS3Client.setRegion(Region.getRegion(Regions.US_EAST_1));
        }
        return sAmazonS3Client;
    }

    public static TransferUtility getTransferUtility(Context context) {
        if (sTransferUtility == null) {
            sTransferUtility = new TransferUtility(getAmazonS3Client(context.getApplicationContext()),
                    context.getApplicationContext());
        }
        return sTransferUtility;
    }

    public static void uploadImage(TransferObserver observer) {
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.i(SignUpActivity.LOG_TAG, "onStateChanged: ");
                //TODO toast or pd
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                Log.i(SignUpActivity.LOG_TAG, "onStateChanged: ");
                //TODO toast or pd
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.i(SignUpActivity.LOG_TAG, "onStateChanged: ");
                //TODO toast exception
            }
        });
    }

    // get uri for camera picture
    public static Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    // get uri for gallery picture
    @SuppressWarnings("NewApi")
    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        if (needToCheckUri && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = context.getContentResolver()
                    .query(uri, projection, selection, selectionArgs, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.moveToFirst()) {
                return cursor.getString(column_index);
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
