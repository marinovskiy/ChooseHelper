package com.geekhub.choosehelper.utils;

import com.firebase.client.Firebase;
import com.geekhub.choosehelper.models.db.Compare;
import com.geekhub.choosehelper.models.network.NetworkAuthor;
import com.geekhub.choosehelper.models.network.NetworkCompare;
import com.geekhub.choosehelper.models.network.NetworkVariant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex on 06.03.2016.
 */
public class FirebaseManager {

    public static final String FIREBASE_REFERENCE_MAIN = "https://choosehelper.firebaseio.com/";
    public static final String FIREBASE_REFERENCE_USERS = "users";
    public static final String FIREBASE_REFERENCE_COMPARES = "compares";

    public static void addCompare(String userId, String fullName, String question,
                                  List<NetworkVariant> variants, String date) {
        Firebase firebase = new Firebase(FIREBASE_REFERENCE_MAIN).child(FIREBASE_REFERENCE_COMPARES);
        Map<String, String> compare = new HashMap<>();
        NetworkCompare newCompare = new NetworkCompare();
        newCompare.setTitle(question);
        newCompare.setDate(date);
        newCompare.setVariants(variants);
        NetworkAuthor author = new NetworkAuthor();
        author.setId(userId);
        author.setFullName(fullName);
        newCompare.setNetworkAuthor(author);
        firebase.push().setValue(newCompare);
    }

}
