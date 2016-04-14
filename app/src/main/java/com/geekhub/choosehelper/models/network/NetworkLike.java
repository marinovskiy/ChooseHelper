package com.geekhub.choosehelper.models.network;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NetworkLike {

    private String userId;

    private String compareId;

    private int variantNumber;

    @JsonProperty("like")
    private boolean isLike;

    public NetworkLike() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCompareId() {
        return compareId;
    }

    public void setCompareId(String compareId) {
        this.compareId = compareId;
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