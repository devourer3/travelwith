package com.mymusic.orvai.travel_with.Activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.mymusic.orvai.travel_with.Controller.Vod_Chat_Adapter;
import com.mymusic.orvai.travel_with.R;
import com.mymusic.orvai.travel_with.URLs.URLs;
import com.mymusic.orvai.travel_with.Utils.Retrofit_Builder;
import com.mymusic.orvai.travel_with.fragment.Vod;
import com.mymusic.orvai.travel_with.model.Vod_Chat_Message_API;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VodPlayer extends AppCompatActivity {

    Context mCtx;
    SimpleExoPlayer player;
    String vod_key, vod_number;
    @BindView(R.id.exoVodPlayer)
    PlayerView exoVodPlayer;
    @BindView(R.id.chatView)
    RecyclerView chatView;
    Handler handler;
    public static long FORETIME = 0L;
    List<Vod_Chat_Message_API> vod_chat_messages;
    ArrayList<Vod_Chat_Message_API> chat_temp_messages;
    Vod_Chat_Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_player);
        ButterKnife.bind(this);
        mCtx = getApplicationContext();
        FORETIME = 0L;
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackFtry = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(videoTrackFtry);
        player = ExoPlayerFactory.newSimpleInstance(mCtx, trackSelector);
        exoVodPlayer.setPlayer(player);
        chatView.setBackgroundColor(Color.parseColor("#66242424"));
        vod_key = getIntent().getStringExtra("vod_key");
        vod_number = getIntent().getStringExtra("vod_number");
        exoVodPlayer.setUseController(false);
        vod_chat_messages = new ArrayList<>();
        chat_temp_messages = new ArrayList<>();
        adapter = new Vod_Chat_Adapter(vod_chat_messages, mCtx);
        chatView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = 20;
            }
        });
        chatView.setLayoutManager(new LinearLayoutManager(mCtx));
        chatView.setAdapter(adapter);
        handler = new Handler();
    }


    @Override
    protected void onResume() {
        super.onResume();
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFatry = new DefaultDataSourceFactory(mCtx, Util.getUserAgent(mCtx, "Travel_With"), bandwidthMeter);
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFatry).createMediaSource(Uri.parse("http://13.125.242.174/rec/" + vod_key + "_recorded.flv"));
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
        Retrofit_Builder.get_Aws_Api_Service().request_vod_chat_load(vod_number).enqueue(new Callback<List<Vod_Chat_Message_API>>() {
            @Override
            public void onResponse(Call<List<Vod_Chat_Message_API>> call, Response<List<Vod_Chat_Message_API>> response) {
                chat_temp_messages.addAll(response.body());
                chatStart(chat_temp_messages);
            }

            @Override
            public void onFailure(Call<List<Vod_Chat_Message_API>> call, Throwable t) {
            }
        });

    }

    private void chatStart(ArrayList<Vod_Chat_Message_API> chat_list) {
        Iterator<Vod_Chat_Message_API> temp_list = chat_list.iterator();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (temp_list.hasNext()) {
                    Vod_Chat_Message_API chatObject = temp_list.next();
                    long chattime = chatObject.getChatTime();
                    try {
                        Thread.sleep(Math.abs(FORETIME-chattime));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    vod_chat_messages.add(chatObject);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                    FORETIME = chattime;
                }
            }
        }).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.stop();
    }

}
