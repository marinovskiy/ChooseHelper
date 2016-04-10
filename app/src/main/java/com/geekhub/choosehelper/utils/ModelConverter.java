package com.geekhub.choosehelper.utils;

import android.support.annotation.NonNull;

import com.geekhub.choosehelper.ChooseHelperApplication;
import com.geekhub.choosehelper.models.db.Comment;
import com.geekhub.choosehelper.models.db.Compare;
import com.geekhub.choosehelper.models.db.Follower;
import com.geekhub.choosehelper.models.db.Following;
import com.geekhub.choosehelper.models.db.User;
import com.geekhub.choosehelper.models.db.Variant;
import com.geekhub.choosehelper.models.network.NetworkComment;
import com.geekhub.choosehelper.models.network.NetworkCompare;
import com.geekhub.choosehelper.models.network.NetworkFollower;
import com.geekhub.choosehelper.models.network.NetworkFollowing;
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
        List<NetworkFollowing> networkFollowings = networkUser.getFollowings();
        if (networkFollowings != null && !networkFollowings.isEmpty()) {
            RealmList<Following> followings = new RealmList<>();
            for (NetworkFollowing networkFollowing : networkFollowings) {
                followings.add(convertToFollowing(networkFollowing));
            }
            user.setFollowings(followings);
        }
        return user;
    }

    public static Compare convertToCompare(NetworkCompare networkCompare, String networkCompareId,
                                           NetworkUser author, String authorId,
                                           int likedVariant) {
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
        compare.setLikedVariant(likedVariant);
        return compare;
    }

    public static Compare convertToCompare(NetworkCompare networkCompare, String networkCompareId,
                                           User author, int likedVariant) {
        Compare compare = new Compare();
        compare.setId(networkCompareId);
        compare.setDate(-1 * networkCompare.getDate());
        compare.setQuestion(networkCompare.getQuestion());
        compare.setAuthor(author);
        List<NetworkVariant> networkVariants = networkCompare.getVariants();
        if (networkVariants != null && !networkVariants.isEmpty()) {
            RealmList<Variant> variants = new RealmList<>();
            for (NetworkVariant networkVariant : networkVariants) {
                variants.add(convertToVariant(networkVariant));
            }
            compare.setVariants(variants);
        }
        compare.setLikedVariant(likedVariant);
        return compare;
    }

    public static Variant convertToVariant(@NonNull NetworkVariant networkVariant) {
        Variant variant = new Variant();
        variant.setId(ChooseHelperApplication.sVariantPrimaryKey.incrementAndGet());
        variant.setLikes(networkVariant.getLikes());
        variant.setImageUrl(networkVariant.getImageUrl());
        variant.setDescription(networkVariant.getDescription());
        return variant;
    }

    public static Comment convertToComment(NetworkComment networkComment, String networkCommentId,
                                           NetworkUser author, String authorId) {
        Comment comment = new Comment();
        comment.setId(networkCommentId);
        comment.setDate(-1 * networkComment.getDate());
        comment.setAuthor(convertToUser(author, authorId));
        comment.setCommentText(networkComment.getCommentText());
        return comment;
    }

    public static Follower convertToFollower(NetworkFollower networkFollower) {
        Follower follower = new Follower();
        follower.setId(ChooseHelperApplication.sFollowerPrimaryKey.incrementAndGet());
        follower.setFollowerId(networkFollower.getFollowerId());
        return follower;
    }

    public static Following convertToFollowing(NetworkFollowing networkFollowing) {
        Following following = new Following();
        following.setId(ChooseHelperApplication.sFollowingPrimaryKey.incrementAndGet());
        following.setUserId(networkFollowing.getUserId());
        return following;
    }
}
