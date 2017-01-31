package com.bypassmobile.octo.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {

    @SerializedName("login")
    private final String name;

    @SerializedName("avatar_url")
    private final String profileURL;

    @SerializedName("following")
    private int numberOfFollowers;

    public User(String name, String profileURL, int numberOfFollowers) {
        this.name = name;
        this.profileURL = profileURL;
        this.numberOfFollowers = numberOfFollowers;
    }

    public String getName() {
        return name;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public int getNumberOfFollowers() {
        return numberOfFollowers;
    }

    public void setNumberOfFollowers(int numberOfFollowers) {
        this.numberOfFollowers = numberOfFollowers;
    }
}
