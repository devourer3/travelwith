package com.mymusic.orvai.travel_with.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attraction_Near_User_by_located {

    @SerializedName("user_nickname")
    @Expose
    public String userNickname;
    @SerializedName("user_pic_url")
    @Expose
    public String user_pic_url;
    @SerializedName("user_fcm_token")
    @Expose
    public String user_fmc_token;

    public String getUserNickname() {
        return userNickname;
    }

    public String getUser_pic_url() {
        return user_pic_url;
    }

    public String getUser_fmc_token() {
        return user_fmc_token;
    }
}
