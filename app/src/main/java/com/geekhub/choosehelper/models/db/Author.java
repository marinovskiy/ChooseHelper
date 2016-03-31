package com.geekhub.choosehelper.models.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Author extends RealmObject {

    @PrimaryKey
    private String id;

    private String fullName;

    private String photoUrl;

    public Author() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
