package com.geekhub.choosehelper.utils;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.geekhub.choosehelper.models.network.NetworkUser;
import com.geekhub.choosehelper.screens.activities.BaseSignInActivity;
import com.geekhub.choosehelper.utils.db.DbUsersManager;
import com.geekhub.choosehelper.utils.firebase.FirebaseConstants;

import java.util.HashMap;
import java.util.Map;

public class AuthorizationUtil {

    private static final String TAG = "AuthorizationUtil";

    // check if user already exist in firebase
    public static boolean isUserExist(String id) {
        final boolean[] result = new boolean[1];

        Firebase firebase = new Firebase(BaseSignInActivity
                .FIREBASE_BASE_REFERENCE)
                .child("users")
                .child(id);

        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    result[0] = false;
                } else {
                    result[0] = true;
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                result[0] = false;
            }
        });

        return result[0];
    }

    // save user to local database and firebase
    public static void saveUser(NetworkUser networkUser) {

        // saving to local database
        DbUsersManager.saveUser(ModelConverter.convertToUser(networkUser));

        // saving to firebase
        Firebase firebase = new Firebase(FirebaseConstants.FB_REFERENCE_MAIN)
                .child(FirebaseConstants.FB_REFERENCE_USERS)
                .child(Prefs.getUserId());
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put(FirebaseConstants.FB_REFERENCE_USER_EMAIL, networkUser.getEmail());
        userInfo.put(FirebaseConstants.FB_REFERENCE_USER_FULL_NAME, networkUser.getFullName());
        userInfo.put(FirebaseConstants.FB_REFERENCE_USER_PHOTO_URL, networkUser.getPhotoUrl());
        userInfo.put(FirebaseConstants.FB_REFERENCE_USER_BIRTHDAY, networkUser.getBirthday());
        userInfo.put(FirebaseConstants.FB_REFERENCE_USER_PLACE_LIVE, networkUser.getPlaceLive());
        userInfo.put(FirebaseConstants.FB_REFERENCE_USER_ABOUT, networkUser.getAbout());
        if (isUserExist(Prefs.getUserId())) {
            firebase.updateChildren(userInfo);
        } else if (!isUserExist(Prefs.getUserId())) {
            firebase.setValue(userInfo);
        }
    }

}
