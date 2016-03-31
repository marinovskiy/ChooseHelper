package com.geekhub.choosehelper.utils.firebase;

import com.firebase.client.Firebase;
import com.geekhub.choosehelper.models.network.NetworkComment;
import com.geekhub.choosehelper.models.network.NetworkCompare;
import com.geekhub.choosehelper.models.network.NetworkVariant;

import java.util.List;

public class FirebaseComparesManager {

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

    public static void addCommentToCompare(NetworkComment networkComment) {
        Firebase firebase = new Firebase(FirebaseConstants.FB_REFERENCE_MAIN)
                .child(FirebaseConstants.FB_REFERENCE_COMMENTS);
        firebase.push().setValue(networkComment);
    }

}
