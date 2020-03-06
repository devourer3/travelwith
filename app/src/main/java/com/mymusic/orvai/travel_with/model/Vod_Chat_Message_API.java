package com.mymusic.orvai.travel_with.model;

import com.google.gson.annotations.SerializedName;

public class Vod_Chat_Message_API {

    @SerializedName("chatuserprofile")
    private String pic_url;
    @SerializedName("chatuser")
    private String user_id;
    @SerializedName("chatcontent")
    private String message;
    @SerializedName("chattime")
    private Long chatTime;

    public Vod_Chat_Message_API(String pic_url, String user_id, String message, Long chatTime) {
        this.pic_url = pic_url;
        this.user_id = user_id;
        this.message = message;
        this.chatTime = chatTime;
    }

    public String getPic_url() {
        return pic_url;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getMessage() {
        return message;
    }

    public Long getChatTime() {
        return chatTime;
    }
}
