package com.geekhub.choosehelper.utils.firebase;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.geekhub.choosehelper.models.db.Compare;
import com.geekhub.choosehelper.models.network.NetworkAuthor;
import com.geekhub.choosehelper.models.network.NetworkCompare;
import com.geekhub.choosehelper.models.network.NetworkVariant;
import com.geekhub.choosehelper.utils.ModelConverter;
import com.geekhub.choosehelper.utils.db.DbComparesManager;

import java.util.ArrayList;
import java.util.List;

public class FirebaseComparesManager {

    public static final String TAG = "FirebaseComparesManage";

    public static void addCompare(String userId, String userName, String question,
                                  List<NetworkVariant> variants, String date) {
        Firebase firebase = new Firebase(FirebaseConstants.FB_REFERENCE_MAIN)
                .child(FirebaseConstants.FB_REFERENCE_COMPARES);
        /** create author variable **/
        NetworkAuthor author = new NetworkAuthor();
        author.setId(userId);
        author.setFullName(userName);
        /** create compare variable **/
        NetworkCompare compare = new NetworkCompare();
        compare.setQuestion(question);
        compare.setDate(date);
        compare.setVariants(variants);
        compare.setNetworkAuthor(author);
        /** push to firebase **/
        firebase.push().setValue(compare);
    }

    public static void getLastTwentyCompares() {
        Firebase firebase = new Firebase(FirebaseConstants.FB_REFERENCE_MAIN)
                .child(FirebaseConstants.FB_REFERENCE_COMPARES);

        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Compare> compares = new ArrayList<>();
                for (DataSnapshot compareSnapshot : dataSnapshot.getChildren()) {
                    NetworkCompare networkCompare = compareSnapshot.getValue(NetworkCompare.class);
                    compares.add(ModelConverter.convertToCompare(networkCompare, compareSnapshot.getKey()));
                    Log.i("logtags", "id=" + compareSnapshot.getKey());
                }
                if (!compares.isEmpty()) {
                    DbComparesManager.saveCompares(compares);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.i(TAG, "saveUserFromFb: firebase = error details:" + firebaseError.getDetails()
                        + "message: " + firebaseError.getMessage() + " code: " + firebaseError.getCode());
            }
        });
    }

}
