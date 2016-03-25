package com.geekhub.choosehelper.utils;

import android.support.annotation.NonNull;

import com.geekhub.choosehelper.models.db.Author;
import com.geekhub.choosehelper.models.db.Compare;
import com.geekhub.choosehelper.models.db.User;
import com.geekhub.choosehelper.models.db.Variant;
import com.geekhub.choosehelper.models.network.NetworkAuthor;
import com.geekhub.choosehelper.models.network.NetworkCompare;
import com.geekhub.choosehelper.models.network.NetworkUser;
import com.geekhub.choosehelper.models.network.NetworkVariant;

import java.util.List;

import io.realm.RealmList;

public class ModelConverter {

    public static User convertToUser(@NonNull NetworkUser networkUser) {
        User user = new User();
        user.setId(Prefs.getUserId());
        user.setEmail(networkUser.getEmail());
        user.setFullName(networkUser.getFullName());
        user.setPhotoUrl(networkUser.getPhotoUrl());
        /*user.setBirthday(networkUser.getBirthday());
        user.setPlaceLive(networkUser.getPlaceLive());
        user.setAbout(networkUser.getAbout());*/
        return user;
    }

    public static Compare convertToCompare(@NonNull NetworkCompare networkCompare,
                                           String networkCompareId) {
        Compare compare = new Compare();
        compare.setId(networkCompareId);
        compare.setQuestion(networkCompare.getQuestion());
        compare.setDate(networkCompare.getDate());
        compare.setAuthor(convertToAuthor(networkCompare.getNetworkAuthor()));
        List<NetworkVariant> networkVariants = networkCompare.getVariants();
        if (networkVariants != null && !networkVariants.isEmpty()) {
            RealmList<Variant> variants = new RealmList<>();
            for (NetworkVariant networkVariant : networkVariants) {
                variants.add(convertToVariant(networkVariant));
            }
            compare.setVariants(variants);
        }
        return compare;
    }

    public static Author convertToAuthor(@NonNull NetworkAuthor networkAuthor) {
        Author author = new Author();
        author.setId(networkAuthor.getId());
        author.setName(networkAuthor.getFullName());
        return author;
    }

    public static Variant convertToVariant(@NonNull NetworkVariant networkVariant) {
        Variant variant = new Variant();
        variant.setImageUrl(networkVariant.getImageUrl());
        variant.setDescription(networkVariant.getDescription());
        return variant;
    }

}
