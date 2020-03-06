package com.mymusic.orvai.travel_with.model;

import com.google.gson.annotations.SerializedName;

public class Room_Quit_API {

    @SerializedName("result")
    private String result;
    @SerializedName("message")
    private String message;

    public Room_Quit_API(String result, String message) {
        this.result = result;
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }
}
