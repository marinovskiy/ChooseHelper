package com.geekhub.choosehelper.utils.firebase;

import android.content.Context;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.network.NetworkFollowing;
import com.geekhub.choosehelper.models.network.NetworkUser;
import com.geekhub.choosehelper.utils.ModelConverter;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.Utils;
import com.geekhub.choosehelper.utils.db.DbUsersManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseUsersManager {

    public static final String TAG = FirebaseUsersManager.class.getSimpleName();

    public static void saveUserFromFirebase(String id) {
        Firebase firebase = new Firebase(FirebaseConstants.FB_REF_MAIN)
                .child(FirebaseConstants.FB_REF_USERS)
                .child(id);

        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                NetworkUser networkUser = dataSnapshot.getValue(NetworkUser.class);
                if (networkUser != null) {
                    DbUsersManager.saveUser(ModelConverter.convertToUser(networkUser, id));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public static void saveUserToFirebase(NetworkUser networkUser) {
        Firebase firebase = new Firebase(FirebaseConstants.FB_REF_MAIN)
                .child(FirebaseConstants.FB_REF_USERS)
                .child(Prefs.getUserId());

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put(FirebaseConstants.FB_REF_EMAIL, networkUser.getEmail());
        userInfo.put(FirebaseConstants.FB_REF_FULL_NAME, networkUser.getFullName());
        userInfo.put(FirebaseConstants.FB_REF_PHOTO_URL, networkUser.getPhotoUrl());

        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    NetworkUser networkUser = dataSnapshot.getValue(NetworkUser.class);
                    List<NetworkFollowing> networkFollowings = networkUser.getFollowings();
                    userInfo.put(FirebaseConstants.FB_REF_FOLLOWINGS, networkFollowings);
                }
                firebase.setValue(userInfo);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public static void resetPassword(Context context, String email) {
        Firebase firebase = new Firebase(FirebaseConstants.FB_REF_MAIN);
        firebase.resetPassword(email, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                Utils.showMessage(context, context.getString(R.string.dialog_reset_sent));
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Utils.showMessage(context, context.getString(R.string.toast_wrong_email));
            }
        });
    }

    public static void changePassword(Context context, String email, String oldPassword, String newPassword) {
        Firebase ref = new Firebase(FirebaseConstants.FB_REF_MAIN);
        ref.changePassword(email, oldPassword, newPassword, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                Utils.showMessage(context, context.getString(R.string.dialog_change_pass));
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Utils.showMessage(context, context.getString(R.string.toast_wrong_password));
            }
        });
    }
}