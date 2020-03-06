package com.mymusic.orvai.travel_with.model;

import com.google.gson.annotations.SerializedName;

public class Email_Registration_API {

    @SerializedName("msg")
    private String message;

    @SerializedName("error")
    private Boolean error;

    public Email_Registration_API(String message, Boolean error) {
        this.message = message;
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public Boolean getError() {
        return error;
    }
}
