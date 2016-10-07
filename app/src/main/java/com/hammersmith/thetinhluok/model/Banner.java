package com.hammersmith.thetinhluok.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Chan Thuon on 10/6/2016.
 */
public class Banner {
    @SerializedName("image_url")
    private String image;
    @SerializedName("url_site")
    private String website;
    @SerializedName("title")
    private String title;

    public Banner() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
