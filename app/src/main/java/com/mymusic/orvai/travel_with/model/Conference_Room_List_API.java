package com.mymusic.orvai.travel_with.model;

import com.google.gson.annotations.SerializedName;

public class Conference_Room_List_API {

    @SerializedName("conference_number")
    private String conference_number;
    @SerializedName("conference_name")
    private String conference_name;
    @SerializedName("conference_users")
    private String conference_users;
    @SerializedName("conference_stream_key")
    private String conference_stream_key;

    public String getConference_number() {
        return conference_number;
    }

    public String getConference_name() {
        return conference_name;
    }

    public String getConference_users() {
        return conference_users;
    }

    public String getConference_stream_key() {
        return conference_stream_key;
    }
}
