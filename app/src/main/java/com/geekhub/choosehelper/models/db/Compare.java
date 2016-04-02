package com.geekhub.choosehelper.models.db;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Compare extends RealmObject {

    @PrimaryKey
    private String id;

    private String question;

    private User author;

    private RealmList<Variant> variants;

    private RealmList<Comment> comments;

    private long date;

    private int likedVariant;

    public Compare() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public RealmList<Variant> getVariants() {
        return variants;
    }

    public void setVariants(RealmList<Variant> variants) {
        this.variants = variants;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
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
}
