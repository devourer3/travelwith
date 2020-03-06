package com.mymusic.orvai.travel_with.Utils;

import com.google.gson.Gson;
import com.mymusic.orvai.travel_with.Interface.API;
import com.mymusic.orvai.travel_with.URLs.URLs;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofit_Builder {

    private static Retrofit retrofit = null;
    private static API api_service = null;

    public static API get_Tour_Api_service(Gson gson){
        if(api_service==null) {
            retrofit = new Retrofit.Builder().baseUrl(URLs.TOUR_API_3_0_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
            api_service = retrofit.create(API.class);
        }
        return api_service;
    }

    public static API get_Aws_Api_Service() {
        if(api_service==null) {
            retrofit = new Retrofit.Builder().baseUrl(URLs.AWS_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
            api_service = retrofit.create(API.class);
        }
        return api_service;
    }
}
