package com.geekhub.choosehelper.models.vk;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Alex on 07.03.2016.
 */
public class VkUser {

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("photo_200")
    private String imageUrl;

    @SerializedName("bdate")
    private String birthday;

    private VkCountry country;

    private VkCity city;

    private String about;

    public VkUser() {

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public VkCountry getCountry() {
        return country;
    }

    public void setCountry(VkCountry country) {
        this.country = country;
    }

    public VkCity getCity() {
        return city;
    }

    public void setCity(VkCity city) {
        this.city = city;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
