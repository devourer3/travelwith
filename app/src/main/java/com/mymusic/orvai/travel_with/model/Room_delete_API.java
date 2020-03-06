package com.mymusic.orvai.travel_with.model;

import com.google.gson.annotations.SerializedName;

public class Room_delete_API {

    @SerializedName("result")
    private String result;
    @SerializedName("room_number")
    private String room_number;

    public Room_delete_API(String result, String room_number) {
        this.result = result;
        this.room_number = room_number;
    }

    public String getResult() {
        return result;
    }

    public String getRoom_number() {
        return room_number;
    }
}
