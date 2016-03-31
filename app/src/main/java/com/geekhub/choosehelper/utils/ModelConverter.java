package com.geekhub.choosehelper.utils;

import android.support.annotation.NonNull;

import com.geekhub.choosehelper.ChooseHelperApplication;
import com.geekhub.choosehelper.models.db.Author;
import com.geekhub.choosehelper.models.db.Comment;
import com.geekhub.choosehelper.models.db.Compare;
import com.geekhub.choosehelper.models.db.User;
import com.geekhub.choosehelper.models.db.Variant;
import com.geekhub.choosehelper.models.network.NetworkAuthor;
import com.geekhub.choosehelper.models.network.NetworkComment;
import com.geekhub.choosehelper.models.network.NetworkCompare;
import com.geekhub.choosehelper.models.network.NetworkUser;
import com.geekhub.choosehelper.models.network.NetworkVariant;

import java.util.List;

import io.realm.RealmList;

public class ModelConverter {

    public static User convertToUser(@NonNull NetworkUser networkUser,
                                     String networkUserId) {
        User user = new User();
        user.setId(networkUserId);
        user.setEmail(networkUser.getEmail());
        user.setFullName(networkUser.getFullName());
        user.setPhotoUrl(networkUser.getPhotoUrl());
        /*user.setBirthday(networkUser.getBirthday());
        user.setPlaceLive(networkUser.getPlaceLive());
        user.setAbout(networkUser.getAbout());*/
        return user;
    }

    public static Compare convertToCompare(NetworkCompare networkCompare, String networkCompareId,
                                           NetworkUser author, String authorId) {
        Compare compare = new Compare();
        compare.setId(networkCompareId);
        compare.setDate(-1 * networkCompare.getDate());
        compare.setQuestion(networkCompare.getQuestion());
        compare.setAuthor(convertToUser(author, authorId));
        List<NetworkVariant> networkVariants = networkCompare.getVariants();
        if (networkVariants != null && !networkVariants.isEmpty()) {
            RealmList<Variant> variants = new RealmList<>();
            for (NetworkVariant networkVariant : networkVariants) {
                variants.add(convertToVariant(networkVariant));
            }
            compare.setVariants(variants);
        }
        List<NetworkComment> networkComments = networkCompare.getNetworkComments();
//        if (networkComments != null && !networkComments.isEmpty()) {
//            RealmList<Comment> comments = new RealmList<>();
//            for (NetworkComment networkComment : networkComments) {
//                comments.add(convertToComment(networkComments));
//            }
//            compare.setComments(comments);
//        }
        return compare;
    }

    public static Author convertToAuthor(@NonNull NetworkAuthor networkAuthor) {
        Author author = new Author();
        author.setId(networkAuthor.getId());
        author.setFullName(networkAuthor.getFullName());
        author.setPhotoUrl(networkAuthor.getPhotoUrl());
        return author;
    }

    public static Variant convertToVariant(@NonNull NetworkVariant networkVariant) {
        Variant variant = new Variant();
        variant.setId(ChooseHelperApplication.sPrimaryKey.incrementAndGet());
        variant.setImageUrl(networkVariant.getImageUrl());
        variant.setDescription(networkVariant.getDescription());
        return variant;
    }

    public static Comment convertToComment(NetworkComment networkComment, String networkCommentId,
                                           NetworkUser author, String authorId) {
        Comment comment = new Comment();
        comment.setId(networkCommentId);
        comment.setDate(networkComment.getDate());
        comment.setAuthor(convertToUser(author, authorId));
        comment.setCommentText(networkComment.getCommentText());
        return comment;

    }

}
