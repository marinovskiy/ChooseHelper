package com.geekhub.choosehelper.models.network;

public class NetworkUser {

    private String email;

    private String fullName;

    private String photoUrl;

    /*private String birthday;

    private String placeLive;

    private String about;*/

    public NetworkUser() {

    }

    public NetworkUser(String email, String fullName, String photoUrl) {
        this.email = email;
        this.fullName = fullName;
        this.photoUrl = photoUrl;
    }

    /*public NetworkUser(String email, String fullName, String photoUrl, String birthday, String placeLive, String about) {
        this.email = email;
        this.fullName = fullName;
        this.photoUrl = photoUrl;
        this.birthday = birthday;
        this.placeLive = placeLive;
        this.about = about;
    }*/

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    /*public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPlaceLive() {
        return placeLive;
    }

    public void setPlaceLive(String placeLive) {
        this.placeLive = placeLive;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }*/
}
