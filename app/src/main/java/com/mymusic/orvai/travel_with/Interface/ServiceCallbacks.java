package com.mymusic.orvai.travel_with.Interface;

import org.json.simple.JSONObject;

public interface ServiceCallbacks { // 서비스 콜백을 등록하기 위한 인터페이스

    void create_room_response_ok(String streaming_key, String room_number);

    void delete_room_response_ok();

    void receive_msg(JSONObject jsonObject);

}
