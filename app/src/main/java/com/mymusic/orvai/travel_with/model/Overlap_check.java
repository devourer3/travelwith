package com.mymusic.orvai.travel_with.model;

import com.google.gson.annotations.SerializedName;

public class Overlap_check {

    @SerializedName("error")
    private String error;
    @SerializedName("user_nickname")
    private String user_nickname;
    @SerializedName("user_pic_url")
    private String user_pic_url;
    @SerializedName("user_uuid")
    private String user_uuid;
    @SerializedName("user_reg_method")
    private String reg_method;
    @SerializedName("user_fcm_token")
    private String fcm_token;


    public Overlap_check(String error, String user_nickname, String user_pic_url, String user_uuid, String reg_method, String fcm_token) {
        this.error = error;
        this.user_nickname = user_nickname;
        this.user_pic_url = user_pic_url;
        this.user_uuid = user_uuid;
        this.reg_method = reg_method;
        this.fcm_token = fcm_token;
    }

    public String getError() {
        return error;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public String getUser_pic_url() {
        return user_pic_url;
    }

    public String getUser_uuid() {
        return user_uuid;
    }

    public String getReg_method() {
        return reg_method;
    }

    public String getFcm_token() {
        return fcm_token;
    }
}
