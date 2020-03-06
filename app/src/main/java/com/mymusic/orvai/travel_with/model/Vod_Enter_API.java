package com.mymusic.orvai.travel_with.model;

import com.google.gson.annotations.SerializedName;

public class Vod_Enter_API {

    @SerializedName("result")
    private String result;

    public Vod_Enter_API(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

}
