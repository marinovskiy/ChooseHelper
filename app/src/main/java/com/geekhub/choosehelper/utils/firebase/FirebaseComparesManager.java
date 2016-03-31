package com.geekhub.choosehelper.utils.firebase;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.geekhub.choosehelper.models.db.Compare;
import com.geekhub.choosehelper.models.network.NetworkAuthor;
import com.geekhub.choosehelper.models.network.NetworkComment;
import com.geekhub.choosehelper.models.network.NetworkCompare;
import com.geekhub.choosehelper.models.network.NetworkUser;
import com.geekhub.choosehelper.models.network.NetworkVariant;
import com.geekhub.choosehelper.utils.ModelConverter;
import com.geekhub.choosehelper.utils.db.DbComparesManager;
import com.geekhub.choosehelper.utils.db.DbUsersManager;

import java.util.ArrayList;
import java.util.List;

public class FirebaseComparesManager {

    public static final String TAG = "FirebaseComparesManage";

    public static void addCompare(String userId, String question,
                                  List<NetworkVariant> variants, long date) {
        Firebase firebase = new Firebase(FirebaseConstants.FB_REFERENCE_MAIN)
                .child(FirebaseConstants.FB_REFERENCE_COMPARES);
        /** create compare variable **/
        NetworkCompare compare = new NetworkCompare();
        compare.setQuestion(question);
        compare.setDate(date);
        compare.setVariants(variants);
        compare.setUserId(userId);
        //compare.setNetworkComments(null);
        /** push to firebase **/
        firebase.push().setValue(compare);
    }

    /*public static void getCompareById(String id) {
        Firebase firebase = new Firebase(FirebaseConstants.FB_REFERENCE_MAIN)
                .child(FirebaseConstants.FB_REFERENCE_COMPARES)
                .child(id);

        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                NetworkCompare networkCompare = dataSnapshot.getValue(NetworkCompare.class);
                List<Compare> compareList = new ArrayList<>();
                compareList.add(ModelConverter.convertToCompare(networkCompare, dataSnapshot.getKey()));
                DbComparesManager.saveCompares(compareList);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.i(TAG, "getCompareById: firebase = error details:" + firebaseError.getDetails()
                        + "message: " + firebaseError.getMessage() + " code: " + firebaseError.getCode());
            }
        });
    }*/

    public static void addCommentToCompare(String compareId, NetworkComment networkComment) {
        Firebase firebase = new Firebase(FirebaseConstants.FB_REFERENCE_MAIN)
                .child(FirebaseConstants.FB_REFERENCE_COMPARES)
                .child(compareId)
                .child("networkComments");

        firebase.push().setValue(networkComment);
    }

}
