package com.geekhub.choosehelper.utils.firebase;

import android.util.Log;

import com.firebase.client.Firebase;
import com.geekhub.choosehelper.models.network.NetworkCompare;
import com.geekhub.choosehelper.models.network.NetworkVariant;

import java.util.List;

public class FirebaseComparesManager {

    public static void addCompare(String userId, String question, String category,
                                  List<NetworkVariant> variants, long date) {
        Firebase firebase = new Firebase(FirebaseConstants.FB_REF_MAIN)
                .child(FirebaseConstants.FB_REF_COMPARES);
        /** create compare variable **/
        NetworkCompare compare = new NetworkCompare();
        compare.setQuestion(question);
        compare.setCategory(category);
        compare.setDate(date);
        compare.setOpen(true);
        compare.setVariants(variants);
        compare.setUserId(userId);
        /** push to firebase **/
        firebase.push().setValue(compare);
    }

    public static void deleteCompare(String compareId) {
        Firebase firebase = new Firebase(FirebaseConstants.FB_REF_MAIN)
                .child(FirebaseConstants.FB_REF_COMPARES)
                .child(compareId);
        firebase.setValue(null, (firebaseError, firebase1) -> {
            if (firebaseError != null) {
                Log.i("FirebaseComparesManager", "deleteCompare: error: " + firebaseError);
            } else {
                Log.i("FirebaseComparesManager", "deleteCompare: complete");
            }
        });
    }

}
