package com.geekhub.choosehelper.models.network;

import java.util.List;

public class NetworkCompare {

    private String question;

    private List<NetworkVariant> variants;

    private NetworkAuthor networkAuthor;

    private String date;

    public NetworkCompare() {

    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
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
