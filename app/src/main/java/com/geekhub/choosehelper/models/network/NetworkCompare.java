package com.geekhub.choosehelper.models.network;

import java.util.List;

/**
 * Created by Alex on 06.03.2016.
 */
public class NetworkCompare {

    private String title;

    private List<NetworkVariant> variants;

    private NetworkAuthor networkAuthor;

    private String date;

    public NetworkCompare() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<NetworkVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<NetworkVariant> variants) {
        this.variants = variants;
    }

    public NetworkAuthor getNetworkAuthor() {
        return networkAuthor;
    }

    public void setNetworkAuthor(NetworkAuthor networkAuthor) {
        this.networkAuthor = networkAuthor;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
