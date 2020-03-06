package com.mymusic.orvai.travel_with.model;

public class Chat_Message {

    private String pic_url;
    private String user_id;
    private String message;

    public Chat_Message(String pic_url, String user_id, String message) {
        this.pic_url = pic_url;
        this.user_id = user_id;
        this.message = message;
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
}
