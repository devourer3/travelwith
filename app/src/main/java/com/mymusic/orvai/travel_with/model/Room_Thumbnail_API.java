package com.mymusic.orvai.travel_with.model;

import com.google.gson.annotations.SerializedName;

public class Room_Thumbnail_API {

    @SerializedName("result")
    String result;
    @SerializedName("room_number")
    String room_number;
    @SerializedName("room_thumbnail")
    String room_thumbnail;


    public Room_Thumbnail_API(String result, String room_number, String room_thumbnail) {
        this.result = result;
        this.room_number = room_number;
        this.room_thumbnail = room_thumbnail;
    }


    public String getResult() {
        return result;
    }

    public String getRoom_number() {
        return room_number;
    }

    public String getRoom_thumbnail() {
        return room_thumbnail;
    }
}
