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

/**
 * Created by Alex on 07.03.2016.
 */
public class FirebaseUsersManager {

    public static void saveUserFromFirebase() {
        Firebase firebase = new Firebase(BaseSignInActivity
                .FIREBASE_BASE_REFERENCE)
                .child(FirebaseConstants.FB_REFERENCE_USERS)
                .child(Prefs.getUserId());
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                NetworkUser networkUser = dataSnapshot.getValue(NetworkUser.class);
                Log.i("qwertytag", "saveUserFromFb:" + networkUser.getEmail());
                Log.i("qwertytag", "saveUserFromFb:" + networkUser.getPhotoUrl());
                Log.i("qwertytag", "saveUserFromFb:" + networkUser.getFullName());
                Log.i("qwertytag", "saveUserFromFb:" + networkUser.getAbout());
                DbUsersManager.saveUser(ModelConverter.convertToUser(networkUser));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.i("tag", "saveUserFromFb: firebase = error");
            }
        });
    }

}
