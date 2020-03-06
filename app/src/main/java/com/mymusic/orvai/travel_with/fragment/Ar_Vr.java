package com.mymusic.orvai.travel_with.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mymusic.orvai.travel_with.Activity.Ar_Vr_Unity_Player;
import com.mymusic.orvai.travel_with.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

//import com.mymusic.orvai.travel_with.Activity.WebRTC_Conference;

public class Ar_Vr extends Fragment {

    @BindView(R.id.attraction_toolbar_title)
    TextView attractionToolbarTitle;
    @BindView(R.id.attraction_toolbar)
    Toolbar attractionToolbar;
    Unbinder unbinder;
    public Context mCtx;
    @BindView(R.id.unityPlay)
    CircleImageView unityPlay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ar_vr, container, false);
        unbinder = ButterKnife.bind(this, view);
        mCtx = view.getContext();
        ((AppCompatActivity) getActivity()).setSupportActionBar(attractionToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.unityPlay)
    public void onViewClicked() {
        startActivity(new Intent(mCtx, Ar_Vr_Unity_Player.class));
    }

}
