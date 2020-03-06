package com.mymusic.orvai.travel_with.model;

import com.google.gson.annotations.SerializedName;

public class Streaming_Room_List_API {

    @SerializedName("room_number")
    private String room_number;
    @SerializedName("room_name")
    private String room_name;
    @SerializedName("room_streamer")
    private String room_streamer;
    @SerializedName("room_max_user")
    private String room_max_user;
    @SerializedName("streaming_start_time")
    private Long streaming_start_time;
    @SerializedName("room_location")
    private String room_location;
    @SerializedName("room_users")
    private String room_users;
    @SerializedName("room_thumbnail")
    private String room_thumbnail;
    @SerializedName("room_stream_key")
    private String room_stream_key;


    public String getRoom_number() {
        return room_number;
    }

    public String getRoom_name() {
        return room_name;
    }

    public String getRoom_streamer() {
        return room_streamer;
    }

    public String getRoom_max_user() {
        return room_max_user;
    }

    public String getRoom_location() {
        return room_location;
    }

    public String getRoom_users() {
        return room_users;
    }

    public String getRoom_thumbnail() {
        return room_thumbnail;
    }

    public String getRoom_stream_key() {
        return room_stream_key;
    }

    public Long getStreaming_start_time() {
        return streaming_start_time;
    }
}
