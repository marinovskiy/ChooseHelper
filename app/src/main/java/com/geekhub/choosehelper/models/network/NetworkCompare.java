package com.geekhub.choosehelper.models.network;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NetworkCompare {

    private String question;

    private String userId;

    private long date;

    private List<NetworkVariant> variants;

    private List<NetworkComment> networkComments;

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

    public List<NetworkVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<NetworkVariant> variants) {
        this.variants = variants;
    }

    public List<NetworkComment> getNetworkComments() {
        return networkComments;
    }

    public void setNetworkComments(List<NetworkComment> networkComments) {
        this.networkComments = networkComments;
    }
}
