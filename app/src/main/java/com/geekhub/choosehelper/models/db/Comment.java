package com.geekhub.choosehelper.models.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Comment extends RealmObject {

    @PrimaryKey
    private String id;

    private User author;

    private long date;

    private String commentText;

    public Comment() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
}
