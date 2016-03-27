package com.geekhub.choosehelper.models.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Variant extends RealmObject {

    @PrimaryKey
    private long id;

    private String imageUrl;

    private String description;

    public Variant() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
