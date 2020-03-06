package com.mymusic.orvai.travel_with.model;

import com.google.gson.annotations.SerializedName;

public class Vod_List_Api {

    @SerializedName("vod_number")
    private String vod_number;
    @SerializedName("vod_name")
    private String vod_name;
    @SerializedName("vod_streamer")
    private String vod_streamer;
    @SerializedName("vod_location")
    private String vod_location;
    @SerializedName("vod_watchers")
    private String vod_watchers;
    @SerializedName("vod_thumbnail")
    private String vod_thumbnail;
    @SerializedName("vod_key")
    private String vod_key;


    public Vod_List_Api(String vod_number, String vod_name, String vod_streamer, String vod_location, String vod_watchers, String vod_thumbnail, String vod_key) {
        this.vod_number = vod_number;
        this.vod_name = vod_name;
        this.vod_streamer = vod_streamer;
        this.vod_location = vod_location;
        this.vod_watchers = vod_watchers;
        this.vod_thumbnail = vod_thumbnail;
        this.vod_key = vod_key;
    }

    public String getVod_number() {
        return vod_number;
    }

    public String getVod_name() {
        return vod_name;
    }

    public String getVod_streamer() {
        return vod_streamer;
    }

    public String getVod_location() {
        return vod_location;
    }

    public String getVod_watchers() {
        return vod_watchers;
    }

    public String getVod_thumbnail() {
        return vod_thumbnail;
    }

    public String getVod_key() {
        return vod_key;
    }
}
