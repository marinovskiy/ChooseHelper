package com.geekhub.choosehelper.models.network;

public class NetworkComment {

    private String compareId;

    private String userId;

    private long date;

    private String commentText;

    public NetworkComment() {

    }

    public NetworkComment(String compareId, String userId, long date, String commentText) {
        this.compareId = compareId;
        this.userId = userId;
        this.date = date;
        this.commentText = commentText;
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
