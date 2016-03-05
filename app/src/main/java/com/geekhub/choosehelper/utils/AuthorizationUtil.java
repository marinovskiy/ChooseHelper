package com.geekhub.choosehelper.utils;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.geekhub.choosehelper.models.db.User;
import com.geekhub.choosehelper.models.network.NetworkUser;
import com.geekhub.choosehelper.screens.activities.BaseSignInActivity;

import java.util.HashMap;
import java.util.Map;

public class AuthorizationUtil {

    private static final String TAG = "AuthorizationUtil";

    // Check if user already exist in firebase
    public static boolean isExistInFb(String id) {
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

    // Create new user in firebase and save to fb and db
    public static void saveNewUser(String id, String email, String fullName, String photoUrl) {
        // saving into firebase
        Firebase firebase = new Firebase(BaseSignInActivity
                .FIREBASE_BASE_REFERENCE)
                .child("users")
                .child(id);
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", email);
        userInfo.put("fullName", fullName);
        //userInfo.put("photoUrl", photoUrl);
        firebase.setValue(userInfo);
        // saving into database
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPhotoUrl(photoUrl);
        DbUtil.saveUser(user);
    }

    // Get user from firebase by id and save to local database
    public static void saveUserFromFb(String id) {
        Firebase firebase = new Firebase(BaseSignInActivity
                .FIREBASE_BASE_REFERENCE)
                .child("users")
                .child(id);
        Log.i(TAG, "isExistInFb: firebase = " + firebase.getRef());
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                NetworkUser networkUser = dataSnapshot.getValue(NetworkUser.class);
                DbUtil.saveUser(ModelConverter.convertToUser(networkUser));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.i(TAG, "saveUserFromFb: firebase = error");
            }
        });
    }

}
