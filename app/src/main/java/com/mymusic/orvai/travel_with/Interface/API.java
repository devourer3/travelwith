package com.mymusic.orvai.travel_with.Interface;

import com.mymusic.orvai.travel_with.model.Attraction_Detail_Common_Result_API;
import com.mymusic.orvai.travel_with.model.Attraction_Detail_Images_Result_API;
import com.mymusic.orvai.travel_with.model.Attraction_Near_User_Location;
import com.mymusic.orvai.travel_with.model.Attraction_Near_User_by_located;
import com.mymusic.orvai.travel_with.model.Attraction_Result_API;
import com.mymusic.orvai.travel_with.model.Conference_Room_Enter_API;
import com.mymusic.orvai.travel_with.model.Conference_Room_List_API;
import com.mymusic.orvai.travel_with.model.Conference_Room_Registration_API;
import com.mymusic.orvai.travel_with.model.Email_Registration_API;
import com.mymusic.orvai.travel_with.model.Overlap_check;
import com.mymusic.orvai.travel_with.model.Room_Enter_API;
import com.mymusic.orvai.travel_with.model.Room_IschatStart_API;
import com.mymusic.orvai.travel_with.model.Room_Quit_API;
import com.mymusic.orvai.travel_with.model.Room_Registration_API;
import com.mymusic.orvai.travel_with.model.Room_Thumbnail_API;
import com.mymusic.orvai.travel_with.model.Room_delete_API;
import com.mymusic.orvai.travel_with.model.Social_Registration_API;
import com.mymusic.orvai.travel_with.model.Streaming_Room_List_API;
import com.mymusic.orvai.travel_with.model.Streaming_Start_Time_API;
import com.mymusic.orvai.travel_with.model.Vod_Chat_Message_API;
import com.mymusic.orvai.travel_with.model.Vod_Chat_Record_API;
import com.mymusic.orvai.travel_with.model.Vod_Enter_API;
import com.mymusic.orvai.travel_with.model.Vod_List_Api;
import com.mymusic.orvai.travel_with.model.Vod_Registration_API;
import com.mymusic.orvai.travel_with.model.WebRTC_Calling_Request_API;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface API {

    @FormUrlEncoded
    @POST("USER_REGISTER.php?action_call=overlap_check")
    Call<Overlap_check> request_overlap_check(
            @Field("user_social_uid") String social_uid,
            @Field("reg_method") String reg_method,
            @Field("fcm_token") String fcm_token);

    @Multipart
    @POST("USER_REGISTER.php?action_call=registration_image")
    Call<Social_Registration_API> request_social_reg_image(
            @Part("user_nickname") RequestBody nickname,
            @Part("user_email") RequestBody email,
            @Part("user_uuid") RequestBody uuid,
            @Part("reg_method") RequestBody method,
            @Part("user_social_uid") RequestBody s_uid,
            @Part MultipartBody.Part image_file);

    @FormUrlEncoded
    @POST("USER_REGISTER.php?action_call=registration_no_image")
    Call<Social_Registration_API> request_social_reg_no_image(
            @Field("user_nickname") String nickname,
            @Field("user_email") String email,
            @Field("user_pic_url") String pic_url,
            @Field("user_uuid") String uuid,
            @Field("reg_method") String method,
            @Field("user_social_uid") String s_uid,
            @Field("user_fcm_token") String token);

    @Multipart
    @POST("USER_REGISTER.php?action_call=registration")
    Call<Email_Registration_API> request_email_reg(
            @Part("user_nickname") String nickname,
            @Part("user_email") String email,
            @Part("user_password") String password,
            @Part("user_pic_url") String pic_url,
            @Part("user_uuid") String uuid,
            @Part("reg_method") String method);

    @FormUrlEncoded
    @POST("ROOM_CONTROLLER.php?action_call=room_create")
    Call<Room_Registration_API> request_room_create(
            @Field("room_name") String room_name,
            @Field("room_max_user") String room_max_user,
            @Field("room_streamer") String room_streamer,
            @Field("room_location") String room_location,
            @Field("room_stream_key") String room_user_uuid,
            @Field("room_thumbnail") String room_thumbnail);


    @GET("ROOM_CONTROLLER.php?action_call=room_list")
    Call<List<Streaming_Room_List_API>> request_room_list();

    @FormUrlEncoded
    @POST("ROOM_CONTROLLER.php?action_call=streaming_start_time")
    Call<Streaming_Start_Time_API> request_streaming_start_time(
            @Field("room_number") String room_number,
            @Field("streaming_start_time") String streaming_start_time
    );

    @FormUrlEncoded
    @POST("ROOM_CONTROLLER.php?action_call=room_delete")
    Call<Room_delete_API> request_room_delete(
            @Field("room_number") String room_number);

    @FormUrlEncoded
    @POST("ROOM_CONTROLLER.php?action_call=room_enter")
    Call<Room_Enter_API> request_room_enter(
            @Field("room_number") String room_number);

    @FormUrlEncoded
    @POST("ROOM_CONTROLLER.php?action_call=room_quit")
    Call<Room_Quit_API> request_room_quit(
            @Field("room_number") String room_number);

    @FormUrlEncoded
    @POST("ROOM_CONTROLLER.php?action_call=isChatStart")
    Call<Room_IschatStart_API> request_isChatStart(
            @Field("room_number") String room_number
    );

    @FormUrlEncoded
    @POST("ROOM_CONTROLLER.php?action_call=room_thumbnail")
    Call<Room_Thumbnail_API> request_room_thumbnail(
            @Field("room_number") String room_number,
            @Field("room_thumbnail") String room_thumbnail
    );

    @FormUrlEncoded
    @POST("VOD_CHAT.php?action_call=chat_add")
    Call<Vod_Chat_Record_API> request_chat_add(
            @Field("chat_user") String user,
            @Field("chat_user_profile") String profile,
            @Field("chat_content") String content,
            @Field("chat_time") String time,
            @Field("vod_number") String number
    );

    @FormUrlEncoded
    @POST("VOD_CONTROLLER.php?action_call=vod_create")
    Call<Vod_Registration_API> request_vod_create(
            @Field("vod_number") String vod_number,
            @Field("vod_name") String vod_name,
            @Field("vod_streamer") String vod_streamer,
            @Field("vod_location") String location,
            @Field("vod_key") String vod_key,
            @Field("vod_thumbnail") String vod_thumbnail);

    @GET("VOD_CONTROLLER.php?action_call=vod_list")
    Call<List<Vod_List_Api>> request_vod_list();

    @FormUrlEncoded
    @POST("VOD_CHAT.php?action_call=chat_load")
    Call<List<Vod_Chat_Message_API>> request_vod_chat_load(
            @Field("vod_number") String vod_number
    );

    @FormUrlEncoded
    @POST("VOD_CONTROLLER.php?action_call=vod_watching")
    Call<Vod_Enter_API> request_vod_enter(
            @Field("vod_number") String vod_number);

    @FormUrlEncoded
    @POST("CONFERENCE_CONTROLLER.php?action_call=conference_create")
    Call<Conference_Room_Registration_API> request_conference_create(
            @Field("conference_name") String name,
            @Field("conference_stream_key") String key
    );

    @GET("CONFERENCE_CONTROLLER.php?action_call=conference_list")
    Call<List<Conference_Room_List_API>> request_conference_list();

    @FormUrlEncoded
    @POST("CONFERENCE_CONTROLLER.php?action_call=conference_enter")
    Call<Conference_Room_Enter_API> request_conference_enter(
            @Field("conference_number") String conference_number);

    @Headers({"Authorization: key=AIzaSyDVKScBj2H_oi94vtzpLbLd_o_doANHdUg", "Content-Type:application/json"})
    @POST("fcm/send")
    Call<ResponseBody> request_calling(@Body WebRTC_Calling_Request_API webRTCCallingApi);

    @GET("openapi/service/rest/KorService/areaBasedList?ServiceKey=cqC2ieFBb%2B6E7kwAXfSBQrsxPl3OvXbP1W%2FNgsLXQ4DGvFzoZNxq84YmqkHqHZ0bdF%2Btw8xX4QbG%2Bp8M7o8fVQ%3D%3D")
    Call<Attraction_Result_API> get_attractions_areaBased(
            @Query("areaCode") int city_code, // 시, 도 코드
            @Query("sigunguCode") int sigungu_Code, // 시군구 코드
            @Query("pageNo") int pageNo, // 현재 페이지 번호
            @Query("MobileOS") String MobileOS,
            @Query("MobileApp") String App_name,
            @Query("_type") String isJson
    );

    @GET("openapi/service/rest/KorService/detailCommon?" +
            "ServiceKey=cqC2ieFBb%2B6E7kwAXfSBQrsxPl3OvXbP1W%2FNgsLXQ4DGvFzoZNxq84YmqkHqHZ0bdF%2Btw8xX4QbG%2Bp8M7o8fVQ%3D%3D" +
            "&MobileOS=AND" +
            "&MobileApp=Travel_with" +
            "&firstImageYN=Y" +
            "&addrinfoYN=Y" +
            "&mapinfoYN=Y" +
            "&overviewYN=Y" +
            "&_type=json")
    Call<Attraction_Detail_Common_Result_API> get_attraction_detail_common(
            @Query("contentId") String content_id,
            @Query("contentTypeId") String type_id);

    @GET("openapi/service/rest/KorService/detailImage?" +
            "ServiceKey=cqC2ieFBb%2B6E7kwAXfSBQrsxPl3OvXbP1W%2FNgsLXQ4DGvFzoZNxq84YmqkHqHZ0bdF%2Btw8xX4QbG%2Bp8M7o8fVQ%3D%3D" +
            "&MobileOS=AND" +
            "&MobileApp=Travel_with" +
            "&imageYN=Y" +
            "&_type=json")
    Call<Attraction_Detail_Images_Result_API> get_attraction_detail_more_images(
            @Query("contentId") String content_id,
            @Query("contentTypeId") String type_id);

    @FormUrlEncoded
    @POST("USER_LOCATION.php")
    Call<List<Attraction_Near_User_Location>> request_coordinate(
            @Field("nickname") String nickname,
            @Field("latitude") double latitude,
            @Field("longitude") double longitude,
            @Field("address") String address
    );

    @FormUrlEncoded
    @POST("USER_INFO_BY_LOCATED.php")
    Call<Attraction_Near_User_by_located> request_located_user_info(
            @Field("user_uuid") String user_uuid
    );

    @GET("ROOM_CONTROLLER.php?action_call=flushAll")
    Call<Response> request_flush_all();
}