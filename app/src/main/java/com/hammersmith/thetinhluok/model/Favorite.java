package com.hammersmith.thetinhluok.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Chan Thuon on 9/18/2016.
 */
public class Favorite {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String title;
    @SerializedName("image")
    private String image;
    @SerializedName("price")
    private String price;
    @SerializedName("discount")
    private String discount;
    @SerializedName("created_at")
    private String createAt;
    @SerializedName("pro_id")
    private int proId;
    @SerializedName("msg")
    private String msg;
    @SerializedName("social_link")
    private String socialLink;

    public Favorite() {
    }

    public Favorite(int proId, String socialLink) {
        this.proId = proId;
        this.socialLink = socialLink;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public int getProId() {
        return proId;
    }

    public void setProId(int proId) {
        this.proId = proId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSocialLink() {
        return socialLink;
    }

    public void setSocialLink(String socialLink) {
        this.socialLink = socialLink;
    }
}
