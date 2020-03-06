package com.mymusic.orvai.travel_with.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mymusic.orvai.travel_with.Controller.Vod_Adapter;
import com.mymusic.orvai.travel_with.R;
import com.mymusic.orvai.travel_with.Utils.Recycler_Streaming_room_deco;
import com.mymusic.orvai.travel_with.Utils.Retrofit_Builder;
import com.mymusic.orvai.travel_with.model.Vod_List_Api;
import com.mymusic.orvai.travel_with.service.Chat_Channel_Service;
import com.mymusic.orvai.travel_with.sharef_mgr.SharedPreferences_M;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Vod extends Fragment {


    @BindView(R.id.vod_list)
    RecyclerView vodList;
    ArrayList<Vod_List_Api> vod_list;
    Vod_Adapter vod_adapter;
    Unbinder unbinder;
    Context mCtx;

    public Vod() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vod, container, false);
        unbinder = ButterKnife.bind(this, view);
        mCtx = getContext();
        vod_list = new ArrayList<>();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!SharedPreferences_M.getInstance(getActivity()).isLoggedIn())
            vodList.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        vod_list.clear();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mCtx);
        vodList.setLayoutManager(linearLayoutManager);
        vodList.addItemDecoration(new Recycler_Streaming_room_deco());
        Retrofit_Builder.get_Aws_Api_Service().request_vod_list().enqueue(new Callback<List<Vod_List_Api>>() {
            @Override
            public void onResponse(Call<List<Vod_List_Api>> call, Response<List<Vod_List_Api>> response) {
                vod_adapter = new Vod_Adapter(vod_list, mCtx);
                vod_list.addAll(response.body());
                vodList.setAdapter(vod_adapter);
                vod_adapter.notifyDataSetChanged();
                Log.d("GB", String.valueOf(response.body()));
            }

            @Override
            public void onFailure(Call<List<Vod_List_Api>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
