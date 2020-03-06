package com.mymusic.orvai.travel_with.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mymusic.orvai.travel_with.Activity.Login;
import com.mymusic.orvai.travel_with.Activity.Streaming_Create;
import com.mymusic.orvai.travel_with.Controller.StreamingRoom_Adapter;
import com.mymusic.orvai.travel_with.Interface.API;
import com.mymusic.orvai.travel_with.R;
import com.mymusic.orvai.travel_with.URLs.URLs;
import com.mymusic.orvai.travel_with.Utils.Recycler_Streaming_room_deco;
import com.mymusic.orvai.travel_with.model.Streaming_Room_List_API;
import com.mymusic.orvai.travel_with.service.Chat_Channel_Service;
import com.mymusic.orvai.travel_with.sharef_mgr.SharedPreferences_M;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Streaming extends Fragment {


    @BindView(R.id.streaming)
    RecyclerView streaming;
    Unbinder unbinder;
    @BindView(R.id.start_btn)
    FloatingActionButton startBtn;
    @BindView(R.id.login_btn)
    Button loginBtn;
    Context mCtx;
    ArrayList<Streaming_Room_List_API> streaming_room_apis;
    StreamingRoom_Adapter adapter;
    Retrofit retrofit;
    API r_service;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_streaming, container, false);
        unbinder = ButterKnife.bind(this, view);
        streaming_room_apis = new ArrayList<>();
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        mCtx = getContext();
        if (SharedPreferences_M.getInstance(getActivity()).isLoggedIn()) {
            loginBtn.setVisibility(View.GONE);
            Intent intent = new Intent(mCtx, Chat_Channel_Service.class); // 서비스 스타트 함 시켜줌
            mCtx.startService(intent);
        } else {
            streaming.setVisibility(View.GONE);
            startBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mCtx);
        streaming.setLayoutManager(linearLayoutManager);
        streaming.addItemDecoration(new Recycler_Streaming_room_deco());
        retrofit = new Retrofit.Builder().baseUrl(URLs.AWS_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        r_service = retrofit.create(API.class);
        Call<List<Streaming_Room_List_API>> call = r_service.request_room_list();
        call.enqueue(new Callback<List<Streaming_Room_List_API>>() {
            @Override
            public void onResponse(Call<List<Streaming_Room_List_API>> call, Response<List<Streaming_Room_List_API>> response) {
                adapter = new StreamingRoom_Adapter(streaming_room_apis, mCtx);
                streaming_room_apis.addAll(response.body());
                streaming.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<List<Streaming_Room_List_API>> call, Throwable t) {

            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.login_btn, R.id.start_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                Intent intent = new Intent(mCtx, Login.class);
                startActivity(intent);
                break;
            case R.id.start_btn:
                Intent intent2 = new Intent(mCtx, Streaming_Create.class);
                startActivity(intent2);
                break;
        }
    }
}
