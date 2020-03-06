package com.mymusic.orvai.travel_with.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.mymusic.orvai.travel_with.Controller.WatchingRoom_Chat_Adapter;
import com.mymusic.orvai.travel_with.Interface.ServiceCallbacks;
import com.mymusic.orvai.travel_with.R;
import com.mymusic.orvai.travel_with.URLs.URLs;
import com.mymusic.orvai.travel_with.Utils.Retrofit_Builder;
import com.mymusic.orvai.travel_with.model.Chat_Message;
import com.mymusic.orvai.travel_with.model.Room_IschatStart_API;
import com.mymusic.orvai.travel_with.model.Vod_Chat_Record_API;
import com.mymusic.orvai.travel_with.service.Chat_Channel_Service;
import com.mymusic.orvai.travel_with.sharef_mgr.SharedPreferences_M;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Watching_Room extends AppCompatActivity implements ServiceCallbacks {

    @BindView(R.id.exo_player)
    PlayerView exoPlayer;
    public Chat_Channel_Service c_service;
    public Context mCtx;
    public boolean isBound;
    @BindView(R.id.w_chat_view)
    RecyclerView chatView;
    SimpleExoPlayer player;
    public String room_number, pic_url, user_id;
    @BindView(R.id.e_chat_msg)
    EditText eChatMsg;
    @BindView(R.id.send_btn)
    Button sendBtn;
    public Handler handler;
    public List<Chat_Message> messageList;
    public RecyclerView.Adapter adapter;
    Long chat_start_time = 0L;
    boolean chatRecord = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watching_room);
        ButterKnife.bind(this);
        chat_start_time = 0L;
        init();
    }

    private void init() {
        mCtx = getApplicationContext();

        room_number = getIntent().getStringExtra("room_number");
        chat_start_time = getIntent().getLongExtra("streaming_start_time", 0L);

        chatRecord = chat_start_time != 0;

        pic_url = SharedPreferences_M.getInstance(mCtx).getUser().getUser_pic_url();
        user_id = SharedPreferences_M.getInstance(mCtx).getUser().getUser_nickname();

        String stream_key = getIntent().getStringExtra("stream_key");

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videotrack_Select = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector track_select = new DefaultTrackSelector(videotrack_Select);
        player = ExoPlayerFactory.newSimpleInstance(this, track_select);

        exoPlayer.setPlayer(player);
        exoPlayer.setUseController(false);

        RtmpDataSourceFactory rtmpDataSourceFactory = new RtmpDataSourceFactory();
        MediaSource mediaSource = new ExtractorMediaSource.Factory(rtmpDataSourceFactory).createMediaSource(Uri.parse(URLs.AWS_STREAMING_URL + stream_key));

        player.prepare(mediaSource);
        player.setPlayWhenReady(true);

        Intent intent = new Intent(mCtx, Chat_Channel_Service.class);
        bindService(intent, connection, BIND_AUTO_CREATE);

        handler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        messageList = new ArrayList<>();
        adapter = new WatchingRoom_Chat_Adapter(mCtx, messageList);
        chatView.setLayoutManager(new LinearLayoutManager(mCtx));
        chatView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.stop();
    }

    @Override
    public void receive_msg(JSONObject jsonObject) {
        String j_user_id = String.valueOf(jsonObject.get("sender"));
        Log.d(URLs.TAG + "보내는사람", j_user_id);
        String j_pic_url = String.valueOf(jsonObject.get("pic_url"));
        String j_msg = String.valueOf(jsonObject.get("message"));
        messageList.add(new Chat_Message(j_pic_url, j_user_id, j_msg));
        handler.post(() -> adapter.notifyDataSetChanged());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Chat_Channel_Service.MyLocalBinder myLocalBinder = (Chat_Channel_Service.MyLocalBinder) service;
            c_service = myLocalBinder.getService(); // 서비스 등록 (주기)
            c_service.setCallbacks(Watching_Room.this); // 액티비티 자체를 콜백 등록 (받기)
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(c_service, "수신 실패!", Toast.LENGTH_SHORT).show();
        }
    };


    @OnClick(R.id.send_btn)
    public void sendBtn() {
        Long chat_time = System.currentTimeMillis();
        String chat_msg = eChatMsg.getText().toString();
        if (TextUtils.isEmpty(chat_msg)) {
            eChatMsg.setError("메시지를 입력해 주세요.");
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("request", "send_message");
            jsonObject.put("room_number", room_number);
            jsonObject.put("user_id", user_id);
            jsonObject.put("pic_url", pic_url);
            jsonObject.put("message", chat_msg);
            send_to_me(chat_msg);
            c_service.sendMessage(jsonObject.toJSONString());
            eChatMsg.setText("");
            if (!chatRecord) {
                Retrofit_Builder.get_Aws_Api_Service().request_isChatStart(room_number).enqueue(new Callback<Room_IschatStart_API>() {
                    @Override
                    public void onResponse(Call<Room_IschatStart_API> call, Response<Room_IschatStart_API> response) {
                        if (response.body().getResult().equals("recordOn")) chatRecord = true;
                    }

                    @Override
                    public void onFailure(Call<Room_IschatStart_API> call, Throwable t) {

                    }
                });
            } else {
                Retrofit_Builder.get_Aws_Api_Service().request_chat_add(user_id, pic_url, chat_msg, String.valueOf(chat_time-chat_start_time), room_number).enqueue(new Callback<Vod_Chat_Record_API>() {
                    @Override
                    public void onResponse(Call<Vod_Chat_Record_API> call, Response<Vod_Chat_Record_API> response) {

                    }

                    @Override
                    public void onFailure(Call<Vod_Chat_Record_API> call, Throwable t) {

                    }
                });
                Log.d(URLs.TAG, String.valueOf(chat_time-chat_start_time));
            }
        }
    }

    private void send_to_me(String chat_msg) {
        messageList.add(new Chat_Message(pic_url, user_id, chat_msg));
        handler.post(() -> adapter.notifyDataSetChanged());
    }

    @Override
    public void create_room_response_ok(String streaming_key, String room_number) {

    }

    @Override
    public void delete_room_response_ok() {

    }


}
