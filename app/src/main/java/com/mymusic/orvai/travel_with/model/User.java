package com.mymusic.orvai.travel_with.model;

public class User {

    private String user_nickname;
    private String user_pic_url;
    private String user_uuid;
    private String login_method;
    private String fmc_token;

    public User(String user_nickname, String user_pic_url, String user_uuid, String login_method, String fmc_token) {
        this.user_nickname = user_nickname;
        this.user_pic_url = user_pic_url;
        this.user_uuid = user_uuid;
        this.login_method = login_method;
        this.fmc_token = fmc_token;
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

    public String getLogin_method() {
        return login_method;
    }

    public String getFmc_token() {
        return fmc_token;
    }
}
