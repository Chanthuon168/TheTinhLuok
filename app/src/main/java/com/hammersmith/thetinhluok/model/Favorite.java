package com.hammersmith.thetinhluok.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Chan Thuon on 9/18/2016.
 */
public class Favorite {
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("image")
    private String image;

    public Favorite() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
