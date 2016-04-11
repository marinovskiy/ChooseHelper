package com.geekhub.choosehelper.utils;

import android.content.Context;
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

public class AmazonUtils {

    /** urls for connect and work with amazon simple storage service **/
    private static final String POOL_ID = "us-east-1:38c82a5b-7f63-47d8-b9d8-3ea2ac6b4291";

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
                //TODO toast or pd
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                //TODO toast or pd
            }

            @Override
            public void onError(int id, Exception ex) {
                //TODO toast exception
            }
        });
    }

}
