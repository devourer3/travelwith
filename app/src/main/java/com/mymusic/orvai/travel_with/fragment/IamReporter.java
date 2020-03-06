package com.mymusic.orvai.travel_with.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mymusic.orvai.travel_with.R;
import com.mymusic.orvai.travel_with.service.Chat_Channel_Service;
import com.mymusic.orvai.travel_with.sharef_mgr.SharedPreferences_M;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class IamReporter extends Fragment implements TabLayout.OnTabSelectedListener {


    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    Unbinder unbinder;
    @BindView(R.id.attraction_toolbar_title)
    TextView attractionToolbarTitle;
    @BindView(R.id.attraction_toolbar)
    Toolbar attractionToolbar;
    private Fragment stream_fragment, vod_fragment;
    private FragmentManager fragmentManager;
    Context mCtx;
    public IamReporter() {
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
        View view = inflater.inflate(R.layout.fragment_iam_reporter, container, false);
        unbinder = ButterKnife.bind(this, view);
        mCtx = view.getContext();
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(attractionToolbar);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(false);
        tabLayout.addOnTabSelectedListener(this);
        fragmentManager = getFragmentManager();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentManager.beginTransaction().add(R.id.reporterContainer, new Streaming()).commit();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                stream_fragment = new Streaming();
                fragmentManager.beginTransaction().replace(R.id.reporterContainer, stream_fragment).commit();
                break;
            case 1:
                vod_fragment = new Vod();
                fragmentManager.beginTransaction().replace(R.id.reporterContainer, vod_fragment).commit();
                break;
            default:
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
