package com.hammersmith.thetinhluok.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Chan Thuon on 9/12/2016.
 */
public class Comment {
    @SerializedName("id")
    private int id;
    @SerializedName("comment")
    private String comment;
    @SerializedName("profile")
    private String profile;
    @SerializedName("created_at")
    private String createAt;
    @SerializedName("name")
    private String name;
    @SerializedName("profile_reply")
    private String profileReply;
    @SerializedName("name_reply")
    private String nameReply;
    @SerializedName("reply")
    private String reply;

    public Comment() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileReply() {
        return profileReply;
    }

    public void setProfileReply(String profileReply) {
        this.profileReply = profileReply;
    }

    public String getNameReply() {
        return nameReply;
    }

    public void setNameReply(String nameReply) {
        this.nameReply = nameReply;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
