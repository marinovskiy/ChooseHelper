package com.geekhub.choosehelper.models.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Like extends RealmObject {

    @PrimaryKey
    private String id;

    private String compareId;

    private String userId;

    private int variantNumber;

    private boolean isLike;

    public Like() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompareId() {
        return compareId;
    }

    public void setCompareId(String compareId) {
        this.compareId = compareId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setIsLike(boolean isLike) {
        this.isLike = isLike;
    }

    public int getVariantNumber() {
        return variantNumber;
    }

    public void setVariantNumber(int variantNumber) {
        this.variantNumber = variantNumber;
    }
}
