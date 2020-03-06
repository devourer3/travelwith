package com.mymusic.orvai.travel_with.model;

import com.google.gson.annotations.SerializedName;

public class Conference_Room_Registration_API {

    @SerializedName("conference_key")
    private String key;
    @SerializedName("result")
    private String result;
    @SerializedName("conference_number")
    private String number;

    public String getKey() {
        return key;
    }

    public String getResult() {
        return result;
    }

    public String getNumber() {
        return number;
    }
}
