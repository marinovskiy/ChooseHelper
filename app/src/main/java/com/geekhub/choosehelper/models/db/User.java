package com.geekhub.choosehelper.models.db;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {

    @PrimaryKey
    private String id;

    private String email;

    private String fullName;

    private String photoUrl;

    private RealmList<Following> followings;

    private RealmList<Follower> followers;

    private RealmList<Compare> compares;

    public User() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public RealmList<Following> getFollowings() {
        return followings;
    }

    public void setFollowings(RealmList<Following> followings) {
        this.followings = followings;
    }

    public RealmList<Follower> getFollowers() {
        return followers;
    }

    public void setFollowers(RealmList<Follower> followers) {
        this.followers = followers;
    }

    public RealmList<Compare> getCompares() {
        return compares;
    }

    public void setCompares(RealmList<Compare> compares) {
        this.compares = compares;
    }
}
