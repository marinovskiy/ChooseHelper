package com.geekhub.choosehelper.utils.firebase;

import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;
import com.geekhub.choosehelper.models.network.NetworkCompare;

public class FirebaseComparesManager {

    public static void addNewCompare(NetworkCompare networkCompare) {
        Firebase firebase = new Firebase(FirebaseConstants.FB_REF_MAIN)
                .child(FirebaseConstants.FB_REF_COMPARES);
        firebase.push().setValue(networkCompare, ServerValue.TIMESTAMP);
    }
}