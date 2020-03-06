package com.mymusic.orvai.travel_with.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attraction_Near_User_Location {

    @SerializedName("user_nickname")
    @Expose
    public String userNickname;
    @SerializedName("user_uuid")
    @Expose
    public String userUUID;
    @SerializedName("user_longitude")
    @Expose
    public String userLongitude;
    @SerializedName("user_latitude")
    @Expose
    public String userLatitude;

    public String getUserNickname() {
        return userNickname;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public String getUserLongitude() {
        return userLongitude;
    }

    public String getUserLatitude() {
        return userLatitude;
    }
}
