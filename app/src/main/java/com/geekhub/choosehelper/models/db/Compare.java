package com.geekhub.choosehelper.models.db;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Compare extends RealmObject {

    @PrimaryKey
    private String id;

    private long date;

    private User author;

    private boolean isOpen;

    private String question;

    private RealmList<Variant> variants;

    private RealmList<Comment> comments;

    private int likedVariant;

    private String category;

    public Compare() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public RealmList<Variant> getVariants() {
        return variants;
    }

    public void setVariants(RealmList<Variant> variants) {
        this.variants = variants;
    }

    public RealmList<Comment> getComments() {
        return comments;
    }

    public void setComments(RealmList<Comment> comments) {
        this.comments = comments;
    }

    public int getLikedVariant() {
        return likedVariant;
    }

    public void setLikedVariant(int likedVariant) {
        this.likedVariant = likedVariant;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}