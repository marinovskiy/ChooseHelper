package com.geekhub.choosehelper.utils.firebase;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.geekhub.choosehelper.models.network.NetworkUser;
import com.geekhub.choosehelper.screens.activities.BaseSignInActivity;
import com.geekhub.choosehelper.utils.ModelConverter;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.db.DbUsersManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 07.03.2016.
 */
public class FirebaseUsersManager {

    public static final String TAG = FirebaseUsersManager.class.getSimpleName();

    public static boolean isUserExist(String id) {
        final boolean[] result = new boolean[1];
        Firebase firebase = new Firebase(FirebaseConstants.FB_REFERENCE_MAIN)
                .child(FirebaseConstants.FB_REFERENCE_USERS)
                .child(id);
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                result[0] = dataSnapshot != null;
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                result[0] = false;
            }
        });
        return result[0];
    }

    public static void saveUserFromFirebase(String id) {

        Firebase firebase = new Firebase(FirebaseConstants.FB_REFERENCE_MAIN)
                .child(FirebaseConstants.FB_REFERENCE_USERS)
                .child(id);

        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                NetworkUser networkUser = dataSnapshot.getValue(NetworkUser.class);
                DbUsersManager.saveUser(ModelConverter.convertToUser(networkUser));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.i(TAG, "saveUserFromFb: firebase = error details:" + firebaseError.getDetails()
                        + "message: " + firebaseError.getMessage() + " code: " + firebaseError.getCode());
            }
        });
    }

    public static void saveUserToFirebase(NetworkUser networkUser) {

        Firebase firebase = new Firebase(FirebaseConstants.FB_REFERENCE_MAIN)
                .child(FirebaseConstants.FB_REFERENCE_USERS)
                .child(Prefs.getUserId());

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put(FirebaseConstants.FB_REFERENCE_USER_EMAIL, networkUser.getEmail());
        userInfo.put(FirebaseConstants.FB_REFERENCE_USER_FULL_NAME, networkUser.getFullName());
        userInfo.put(FirebaseConstants.FB_REFERENCE_USER_PHOTO_URL, networkUser.getPhotoUrl());

        if (isUserExist(Prefs.getUserId())) {
            firebase.updateChildren(userInfo);
        } else if (!isUserExist(Prefs.getUserId())) {
            firebase.setValue(userInfo);
        }
    }

}
