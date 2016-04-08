package com.geekhub.choosehelper.models.ui;

public class UserInfo {

    private int count;

    private String title;

    public UserInfo() {

    }

    public UserInfo(int count, String title) {
        this.count = count;
        this.title = title;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
