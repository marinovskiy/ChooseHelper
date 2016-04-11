package com.geekhub.choosehelper.utils.firebase;

import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.widget.CheckBox;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.geekhub.choosehelper.models.network.NetworkLike;
import com.geekhub.choosehelper.utils.Prefs;
import com.geekhub.choosehelper.utils.Utils;

public class FirebaseLikesManager {

    /**
     * firebase reference
     **/
    private static Firebase sFirebaseLikes = new Firebase(FirebaseConstants.FB_REF_MAIN)
            .child(FirebaseConstants.FB_REF_LIKES);

    /**
     * freezing views when like variant
     **/
    private static CardView sMainView;
    private static CheckBox sClickedCheckBox;
    private static CheckBox sOtherCheckBox;

    public static void updateLike(String compareId, int variantNumber, @Nullable CardView mainView,
                                  CheckBox clickedCheckBox, CheckBox otherCheckBox) {
        try {
            sMainView = mainView;
        } catch (NullPointerException ignored) {

        }
        sClickedCheckBox = clickedCheckBox;
        sOtherCheckBox = otherCheckBox;

        sFirebaseLikes.orderByChild(FirebaseConstants.FB_REF_COMPARE_ID)
                .equalTo(compareId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot likeSnapshot) {
                String likeId = null;
                NetworkLike networkLike = null;
                for (DataSnapshot snapshot : likeSnapshot.getChildren()) {
                    if (snapshot.getValue(NetworkLike.class).getUserId().equals(Prefs.getUserId())) {
                        networkLike = snapshot.getValue(NetworkLike.class);
                        likeId = snapshot.getKey();
                    }
                }
                // did not liked before - like this variant
                if (networkLike == null) {
                    likeVariant(compareId, variantNumber);
                }
                // liked before - unlike this variant
                else if (networkLike.getVariantNumber() == variantNumber && networkLike.isLike()) {
                    unLikeVariant(compareId, variantNumber, likeId);
                }
                // other variant liked before - like this variant and unlike other
                else {
                    likeVariant(compareId, variantNumber);
                    unLikeVariant(compareId, Utils.getOtherVariantNumber(variantNumber), likeId);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //Toast.makeText(getContext(), "Error! Please, try later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void likeVariant(String compareId, int variantNumber) {
        Firebase firebaseLike = new Firebase(FirebaseConstants.FB_REF_MAIN)
                .child(FirebaseConstants.FB_REF_COMPARES).child(compareId)
                .child(FirebaseConstants.FB_REF_VARIANTS).child(String.valueOf(variantNumber))
                .child(FirebaseConstants.FB_REF_LIKES);

        firebaseLike.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue((Long) currentData.getValue() + 1);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot snapshot) {
                if (firebaseError == null) {
                    NetworkLike networkLike = new NetworkLike();
                    networkLike.setCompareId(compareId);
                    networkLike.setVariantNumber(variantNumber);
                    networkLike.setUserId(Prefs.getUserId());
                    networkLike.setIsLike(true);
                    sFirebaseLikes.push().setValue(networkLike);
                }
                if (sMainView != null) {
                    Utils.unBlockViews(sMainView, sClickedCheckBox, sOtherCheckBox);
                } else {
                    Utils.unBlockViews(sClickedCheckBox, sOtherCheckBox);
                }
            }
        });
    }

    public static void unLikeVariant(String compareId, int variantNumber, String likeId) {
        Firebase firebaseLike = new Firebase(FirebaseConstants.FB_REF_MAIN)
                .child(FirebaseConstants.FB_REF_COMPARES).child(compareId)
                .child(FirebaseConstants.FB_REF_VARIANTS).child(String.valueOf(variantNumber))
                .child(FirebaseConstants.FB_REF_LIKES);

        firebaseLike.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                if (currentData.getValue() != null && (Long) currentData.getValue() != 0) {
                    currentData.setValue((Long) currentData.getValue() - 1);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot snapshot) {
                if (firebaseError == null) {
                    sFirebaseLikes.child(likeId).setValue(null);
                }
                if (sMainView != null) {
                    Utils.unBlockViews(sMainView, sClickedCheckBox, sOtherCheckBox);
                } else {
                    Utils.unBlockViews(sClickedCheckBox, sOtherCheckBox);
                }
            }
        });
    }

}
