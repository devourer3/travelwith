package com.mymusic.orvai.travel_with.model;

import com.google.gson.annotations.SerializedName;

public class WebRTC_Calling_Request_API {

    @SerializedName("to")
    private String token;
    @SerializedName("priority")
    private String priority;
    @SerializedName("data")
    private FCM_Data_Model FCMData_model;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public FCM_Data_Model getFCMData_model() {
        return FCMData_model;
    }

    public void setFCMData_model(FCM_Data_Model FCMData_model) {
        this.FCMData_model = FCMData_model;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public static class FCM_Data_Model {
        private String user_id, pic_url, room_number, type;

        public FCM_Data_Model(String user_id, String pic_url, String room_number, String type) {
            this.user_id = user_id;
            this.pic_url = pic_url;
            this.room_number = room_number;
            this.type = type;
        }
    }

}