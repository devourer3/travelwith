package com.mymusic.orvai.travel_with.model;

import com.google.gson.annotations.SerializedName;

public class Vod_Registration_API {

    @SerializedName("result")
    private String result;

    @SerializedName("room_number")
    private String room_number;

    public Vod_Registration_API(String result, String room_number) {
        this.result = result;
        this.room_number = room_number;
    }

    public String getRoom_number() {
        return room_number;
    }

    public String getResult() {
        return result;
    }

}
