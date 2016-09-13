package com.hammersmith.thetinhluok.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Chan Thuon on 9/12/2016.
 */
public class Image {
    @SerializedName("id")
    private int id;
    @SerializedName("image")
    private String image;

    public Image() {
    }

    public int getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setId(int id) {
        this.id = id;
    }
}
