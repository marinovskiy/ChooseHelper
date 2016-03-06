package com.geekhub.choosehelper.models.db;

import com.geekhub.choosehelper.models.network.NetworkAuthor;
import com.geekhub.choosehelper.models.network.NetworkVariant;

import java.util.List;

/**
 * Created by Alex on 05.03.2016.
 */
public class Compare {

    private String title;

    private List<NetworkVariant> variants;

    private String author;

    private String date;

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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
