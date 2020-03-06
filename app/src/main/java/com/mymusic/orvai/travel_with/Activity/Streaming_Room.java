package com.mymusic.orvai.travel_with.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mymusic.orvai.travel_with.Controller.StreamingRoom_Chat_Adapter;
import com.mymusic.orvai.travel_with.Interface.API;
import com.mymusic.orvai.travel_with.Interface.ServiceCallbacks;
import com.mymusic.orvai.travel_with.R;
import com.mymusic.orvai.travel_with.URLs.URLs;
import com.mymusic.orvai.travel_with.Utils.Retrofit_Builder;
import com.mymusic.orvai.travel_with.model.Chat_Message;
import com.mymusic.orvai.travel_with.model.Streaming_Start_Time_API;
import com.mymusic.orvai.travel_with.model.Room_Thumbnail_API;
import com.mymusic.orvai.travel_with.model.Room_delete_API;
import com.mymusic.orvai.travel_with.model.Vod_Chat_Record_API;
import com.mymusic.orvai.travel_with.model.Vod_Registration_API;
import com.mymusic.orvai.travel_with.service.Chat_Channel_Service;
import com.mymusic.orvai.travel_with.sharef_mgr.SharedPreferences_M;
import com.pedro.rtplibrary.rtmp.RtmpCamera1;
import com.pedro.rtplibrary.view.OpenGlView;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import org.json.simple.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 스트리머가 영상을 송출하는 액티비티 클래스
 */

public class Streaming_Room extends AppCompatActivity implements ConnectCheckerRtmp, SurfaceHolder.Callback, ServiceCallbacks {

    @BindView(R.id.rtmp_view)
    OpenGlView rtmpView;
    @BindView(R.id.start_streaming_btn)
    Button startStreamingBtn;
    @BindView(R.id.camera_switch)
    Button cameraSwitch;
    @BindView(R.id.e_chat_msg)
    EditText eChatMsg;
    @BindView(R.id.send_btn)
    Button sendBtn;
    @BindView(R.id.s_chat_view)
    RecyclerView chatView;
    private RecyclerView.Adapter adapter;
    private RtmpCamera1 rtmpCamera1;
    private String stream_key, room_number, user_id, pic_url, location, room_name;
    private AlertDialog.Builder dialog;
    private Chat_Channel_Service c_service;
    Long streamingStartTime = 0L;
    public boolean isBound, chatRecord = false;
    public List<Chat_Message> msg_list;
    public Handler handler;
    public Thread thumbnailGen, vodGen;
    Context mCtx;
    Retrofit retrofit;
    API r_service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming_room);
        ButterKnife.bind(this);
        init();
    }

    private void init() { // 생성 화면 다이얼로그로 부터 받은 스트림 키, 방 번호, 방 이름, 사용자의 현재 주소 값을 받아오고 스트리밍 방송 준비를 위한 준비를 마침.
        mCtx = getApplicationContext();
        stream_key = getIntent().getStringExtra("stream_key");
        room_number = getIntent().getStringExtra("room_number");
        room_name = getIntent().getStringExtra("room_name");
        location = getIntent().getStringExtra("location");
        user_id = SharedPreferences_M.getInstance(mCtx).getUser().getUser_nickname();
        pic_url = SharedPreferences_M.getInstance(mCtx).getUser().getUser_pic_url();
        rtmpCamera1 = new RtmpCamera1(rtmpView, this);
        rtmpView.getHolder().addCallback(this);
        Intent intent = new Intent(this, Chat_Channel_Service.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        dialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog);
        retrofit = new Retrofit.Builder().baseUrl(URLs.AWS_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        r_service = retrofit.create(API.class);
//        rtmpView.setKeepAspectRatio(true); // 적정비율 잡아주기
        msg_list = new ArrayList<>();
        thumbnailGen = new Thread(new ThumbnailGen());
        vodGen = new Thread(new VodGen());
    }

    @Override
    protected void onResume() { // Resume이 되었을 때 채팅 목록을 보여주기 위한 리사이클러 뷰 어댑터를 생성하고, 어댑터를 결합 함.
        super.onResume();
        adapter = new StreamingRoom_Chat_Adapter(msg_list, this);
        chatView.setLayoutManager(new LinearLayoutManager(mCtx));
        chatView.setAdapter(adapter);
        handler = new Handler();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() { // 소켓 채널 서비스와 메시지를 교환하기 위한 커넥션 객체 생성 및 콜백메소드 등록
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Chat_Channel_Service.MyLocalBinder myLocalBinder = (Chat_Channel_Service.MyLocalBinder) service;
            c_service = myLocalBinder.getService();
            c_service.setCallbacks(Streaming_Room.this);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        rtmpCamera1.startPreview();
//        rtmpCamera1.getGlInterface().setFilter(new CartoonFilterRender());
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (rtmpCamera1.isStreaming())
            rtmpCamera1.stopStream();
        rtmpCamera1.stopPreview();
    }

    @Override
    protected void onDestroy() { // 화면이 Destroy될 때, 마지막으로 스트리밍 방 섬네일을 생성하는 스레드가 중단되지 않았다면 다시 종료 함
        super.onDestroy();
        unbindService(serviceConnection);
        if (thumbnailGen != null && !thumbnailGen.isInterrupted()) {
            thumbnailGen.interrupt();
        }
    }


    @Override
    public void onBackPressed() {
        dialog.setTitle("방 삭제").setMessage("정말 나가시겠습니까?");
        dialog.setPositiveButton("확인", (dialog, which) -> deleteRoomAPI2());
        dialog.setNegativeButton("취소", null);
        dialog.create();
        dialog.show();
    }

    private void deleteRoomAPI1() { // 방을 나갔을 시 스트리밍 방 목록을 지우기 위한 요청을 보내는 메소드
        Call<Room_delete_API> call = r_service.request_room_delete(room_number);
        call.enqueue(new Callback<Room_delete_API>() {
            @Override
            public void onResponse(Call<Room_delete_API> call, Response<Room_delete_API> response) {
                Toast.makeText(Streaming_Room.this, response.message(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Room_delete_API> call, Throwable t) {
            }
        });
    }

    private void deleteRoomAPI2() { // 방을 나갔을 시, 스트리밍 방과 연결된 채팅방까지 지우기 위한 요청을 보내는 메소드
        Call<Room_delete_API> call = r_service.request_room_delete(room_number);
        call.enqueue(new Callback<Room_delete_API>() {
            @Override
            public void onResponse(Call<Room_delete_API> call, Response<Room_delete_API> response) {
                String result = response.body().getResult();
                if (result.equals("success")) {
                    deleteMsg();
                } else {
                    Toast.makeText(Streaming_Room.this, "오류 발생!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Room_delete_API> call, Throwable t) {
            }
        });
    }

    private void deleteMsg() {
        JSONObject json = new JSONObject();
        json.put("room_number", room_number);
        json.put("request", "delete_room");
        json.put("user_id", SharedPreferences_M.getInstance(mCtx).getUser().getUser_nickname());
        String job = json.toJSONString();
        if (thumbnailGen != null && !thumbnailGen.isInterrupted()) {
            thumbnailGen.interrupt();
        }
        c_service.sendMessage(job);
    }

    @OnClick({R.id.start_streaming_btn, R.id.camera_switch, R.id.send_btn}) // ButterKnife를 사용한 뷰 클릭 이벤트 메소드
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.start_streaming_btn: // 스트리밍 시작 버튼을 눌렀을 때
                if (!rtmpCamera1.isStreaming()) {
                    if (rtmpCamera1.prepareAudio() && rtmpCamera1.prepareVideo()) { // 오디오와, 비디오 소스가 준비 되었다면
                        startStreamingBtn.setVisibility(View.INVISIBLE); // 스타트 버튼을 사라지고
                        rtmpCamera1.startStream(URLs.AWS_STREAMING_URL + stream_key); // rtmp 프로토콜에 기반한 스트리밍 시작(스트림 키는 유저의 uuid 값)
                        StreamingStart(); // 섬네일 생성 및 VOD 저장을 위한 메소드
                    } else {
                        Toast.makeText(this, "스트림 준비가 되지 않았습니다. 퍼미션을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "스트림 중입니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.camera_switch: // 전/후 화면 전환 버튼
                rtmpCamera1.switchCamera();
                break;

            case R.id.send_btn: // 스트리밍 중 채팅을 전송하는 버튼
                Long chat_time = System.currentTimeMillis();
                String chat_msg = eChatMsg.getText().toString();
                if (TextUtils.isEmpty(chat_msg)) {
                    eChatMsg.setError("메시지를 입력해 주세요.");
                } else {
                    JSONObject jsonObject = new JSONObject(); // 채팅 메시지 전송 폼은 JSON 사용
                    jsonObject.put("request", "send_message");
                    jsonObject.put("user_id", user_id);
                    jsonObject.put("pic_url", pic_url);
                    jsonObject.put("message", chat_msg);
                    jsonObject.put("room_number", room_number);
                    send_to_me(chat_msg);
                    c_service.sendMessage(jsonObject.toJSONString());
                    if(chatRecord) { // VOD서비스에 채팅을 기록하는 부울 값이 true 라면, 애플리케이션 서버의 Postgresql 데이터베이스에 유저 아이디, 유저 프로필 사진주소, 채팅메시지, 채팅을 보낸 시간(싱크를 맞추기 위함), 스트리밍 방 번호를 Reotrofit CAll로 보냄
                        Retrofit_Builder.get_Aws_Api_Service().request_chat_add(user_id, pic_url, chat_msg, String.valueOf(chat_time - streamingStartTime), room_number).enqueue(new Callback<Vod_Chat_Record_API>() {
                            @Override
                            public void onResponse(Call<Vod_Chat_Record_API> call, Response<Vod_Chat_Record_API> response) {
                            }

                            @Override
                            public void onFailure(Call<Vod_Chat_Record_API> call, Throwable t) {
                            }
                        });
                    }
                    eChatMsg.setText("");
                }
                break;
        }
    }

    private void StreamingStart() { // 스트리밍이 시작 버튼을 눌렀을 때, 애플리케이션 서버의 Postgresql 데이터베이스 안에 스트리밍 생성 시작 시간과, 방 번호를 저장하는 메소드 VOD생성, 섬네일 생성 스레드를 실행시키는 메소드.
        streamingStartTime = System.currentTimeMillis();
        Retrofit_Builder.get_Aws_Api_Service().request_streaming_start_time(room_number, String.valueOf(streamingStartTime)).enqueue(new Callback<Streaming_Start_Time_API>() {
            @Override
            public void onResponse(Call<Streaming_Start_Time_API> call, Response<Streaming_Start_Time_API> response) {
                if (response.body().getResult().equals("success")) {
                    chatRecord = true;
                    thumbnailGen.start();
                    vodGen.start();
                }
            }

            @Override
            public void onFailure(Call<Streaming_Start_Time_API> call, Throwable t) {
            }
        });
    }

    private void send_to_me(String chat_msg) { // 메시지를 보내고 채널서버로부터 메시지를 수신하였을 때, 송신자가 나인 경우 나에게만 표시되도록 하는 메소드
        msg_list.add(new Chat_Message(pic_url, user_id, chat_msg));
        handler.post(() -> adapter.notifyDataSetChanged());
    }

    @Override
    public void receive_msg(JSONObject message) { // 소켓채널 서비스 클래스로부터 메시지를 받는 메소드
        String j_user_id = String.valueOf(message.get("sender"));
        String j_pic_url = String.valueOf(message.get("pic_url"));
        String j_msg = String.valueOf(message.get("message"));
        msg_list.add(new Chat_Message(j_pic_url, j_user_id, j_msg));
        handler.post(() -> adapter.notifyDataSetChanged());
    }

    @Override
    public void onConnectionSuccessRtmp() { // RTMP 연결 성공 콜백 메소드
        runOnUiThread(() -> {
//                Toast.makeText(Streaming_Room.this, "연결 성공.", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onConnectionFailedRtmp(String s) { // RTMP 연결 실패 콜백 메소드
        runOnUiThread(() -> {
//                Toast.makeText(Streaming_Room.this, "연결 끊김.", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDisconnectRtmp() { // RTMP 연결끊김 콜백 메소드
//                Toast.makeText(Streaming_Room.this, "연결 해제.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthErrorRtmp() { // RTMP 인증실패 콜백 메소드
        runOnUiThread(() -> {
//                Toast.makeText(Streaming_Room.this, "인증 오류.", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onAuthSuccessRtmp() { // RTMP 인증성공 콜백 메소드
        runOnUiThread(() -> {
//                Toast.makeText(Streaming_Room.this, "인증 성공", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void create_room_response_ok(String streaming_key, String room_number) { // 채팅 서버로부터 방 만든 것에 대한 응답 콜백 메소드
    }

    @Override
    public void delete_room_response_ok() {
        finish();
    } // 채팅 서버로부터 방 삭제 응답 콜백 메소드


    public void saveVodThumbnail() { // 현재 스트리밍의 VOD 서비스를 위한 섬네일 이미지를 저장하여, 애플리케이션 서버 Redis 데이터베이스에 전송하는 Retrofit Call 메소드
        rtmpCamera1.getGlInterface().takePhoto(photo -> {
            Bitmap tmpPhoto = Bitmap.createScaledBitmap(photo, 100, 100, false);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            tmpPhoto.compress(Bitmap.CompressFormat.PNG, 100, baos);
            String base64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            Retrofit_Builder.get_Aws_Api_Service().request_vod_create(room_number, room_name, user_id, location, stream_key, base64).enqueue(new Callback<Vod_Registration_API>() {
                @Override
                public void onResponse(Call<Vod_Registration_API> call, Response<Vod_Registration_API> response) {
                }

                @Override
                public void onFailure(Call<Vod_Registration_API> call, Throwable t) {
                }
            });
        });
    }

    public void captureThumbnail() { // 현재 송출하는 화면을 섬네일로 쓰기 위해 압축 후, Base64 기반으로 인코딩하여 서버의 Redis 데이터베이스에 올리는 Retrofit Call 메소드
        rtmpCamera1.getGlInterface().takePhoto(photo -> {
            Bitmap tmpPhoto = Bitmap.createScaledBitmap(photo, 100, 100, false);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            tmpPhoto.compress(Bitmap.CompressFormat.PNG, 50, baos);
            String base64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            if (!thumbnailGen.isInterrupted()) {
                Retrofit_Builder.get_Aws_Api_Service().request_room_thumbnail(room_number, base64).enqueue(new Callback<Room_Thumbnail_API>() {
                    @Override
                    public void onResponse(Call<Room_Thumbnail_API> call, Response<Room_Thumbnail_API> response) {
                    }

                    @Override
                    public void onFailure(Call<Room_Thumbnail_API> call, Throwable t) {
                    }
                });
            }
        });
    }

    public class ThumbnailGen implements Runnable { // 5초마다 섬네일 생성을 위한 반복 스레드
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    deleteRoomAPI1();
                }
                captureThumbnail();
            }
        }
    }

    public class VodGen implements Runnable { // 스트리밍 시작 후 6초 후에 VOD를 제작을 위한 일회용 스레드 발생
        @Override
        public void run() {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            saveVodThumbnail();
        }
    }


}
