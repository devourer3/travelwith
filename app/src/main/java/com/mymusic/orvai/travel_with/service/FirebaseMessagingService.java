package com.mymusic.orvai.travel_with.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.mymusic.orvai.travel_with.Activity.Call_Receiving;
import com.mymusic.orvai.travel_with.Utils.Retrofit_Builder;
import com.mymusic.orvai.travel_with.sharef_mgr.SharedPreferences_M;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    Context mCtx;
    public static String F_NICKNAME;
    public static String F_PIC_URL;
    public static String F_ROOM_NUMBER;

    public FirebaseMessagingService() {
    }


    @Override
    public void onNewToken(String token) { // 기기마다 프로그램 재설치, 캐시 데이터 제거, 앱 복원 등 할 때만 토큰이 새로 갱신됨. 따라서, 내 사용자 DB에 이 토큰을 UPDATE를 할 필요가 있음.
        super.onNewToken(token);
        Log.d("GB", token);
//        sendRegistrationToken(token);
    }



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String type = remoteMessage.getData().get("type");
        F_NICKNAME = remoteMessage.getData().get("user_id");
        F_PIC_URL = remoteMessage.getData().get("pic_url");
        F_ROOM_NUMBER = remoteMessage.getData().get("room_number");
        if(type.equals("call")){
            Intent intent = new Intent(this, Call_Receiving.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }

//    private void saveRegistrationToken(String token) {
//    }

}
