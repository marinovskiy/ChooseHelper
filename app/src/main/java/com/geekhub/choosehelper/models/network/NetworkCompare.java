package com.geekhub.choosehelper.models.network;

import java.util.List;

public class NetworkCompare {

    private String question;

    private String userId;

    private long date;

    private boolean isOpen;

    private List<NetworkVariant> variants;

    private String category;

    public NetworkCompare() {

    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public List<NetworkVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<NetworkVariant> variants) {
        this.variants = variants;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
