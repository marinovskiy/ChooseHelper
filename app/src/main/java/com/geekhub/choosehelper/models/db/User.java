package com.geekhub.choosehelper.models.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Alex on 28.02.2016.
 */
public class User extends RealmObject {

    @PrimaryKey
    private String id;

    private String email;

    private String fullName;

    private String photoUrl;

    private String birthday;

    private String placeLive;

    private String about;

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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPlaceLive() {
        return placeLive;
    }

    public void setPlaceLive(String placeLive) {
        this.placeLive = placeLive;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
