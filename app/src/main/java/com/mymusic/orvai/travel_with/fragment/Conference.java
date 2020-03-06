package com.mymusic.orvai.travel_with.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mymusic.orvai.travel_with.Activity.Conference_Dialog;
import com.mymusic.orvai.travel_with.Controller.ConferenceRoom_Adapter;
import com.mymusic.orvai.travel_with.R;
import com.mymusic.orvai.travel_with.Utils.Recycler_Streaming_room_deco;
import com.mymusic.orvai.travel_with.Utils.Retrofit_Builder;
import com.mymusic.orvai.travel_with.model.Conference_Room_List_API;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Conference extends Fragment {


    Context mCtx;
    Unbinder unbinder;
    @BindView(R.id.conference_toolbar_title)
    TextView conferenceToolbarTitle;
    @BindView(R.id.conference_toolbar)
    Toolbar conferenceToolbar;
    @BindView(R.id.conferenceRecycler)
    RecyclerView conferenceRecycler;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    ArrayList<Conference_Room_List_API> conference_room_list_apis;
    ConferenceRoom_Adapter adapter;

    public Conference() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCtx = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conference, container, false);
        mCtx = view.getContext();
        unbinder = ButterKnife.bind(this, view);
        ((AppCompatActivity) getActivity()).setSupportActionBar(conferenceToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        conference_room_list_apis = new ArrayList<>();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LinearLayoutManager lmgr = new LinearLayoutManager(mCtx);
        conferenceRecycler.setLayoutManager(lmgr);
        conferenceRecycler.addItemDecoration(new Recycler_Streaming_room_deco());
        Retrofit_Builder.get_Aws_Api_Service().request_conference_list().enqueue(new Callback<List<Conference_Room_List_API>>() {
            @Override
            public void onResponse(Call<List<Conference_Room_List_API>> call, Response<List<Conference_Room_List_API>> response) {
                if(!conference_room_list_apis.isEmpty()) conference_room_list_apis.clear();
                adapter = new ConferenceRoom_Adapter(conference_room_list_apis, mCtx);
                conference_room_list_apis.addAll(response.body());
                conferenceRecycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Conference_Room_List_API>> call, Throwable t) {

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick(R.id.floatingActionButton)
    public void onViewClicked() {
        Conference_Dialog dialog = new Conference_Dialog(mCtx);
        dialog.setCancelable(true);
//        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
    }
}
