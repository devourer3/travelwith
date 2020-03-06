package com.mymusic.orvai.travel_with.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mymusic.orvai.travel_with.Activity.CameraMask;
import com.mymusic.orvai.travel_with.Activity.Login;
import com.mymusic.orvai.travel_with.R;
import com.mymusic.orvai.travel_with.sharef_mgr.SharedPreferences_M;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

public class Etc extends Fragment {


    @BindView(R.id.attraction_toolbar_title)
    TextView attractionToolbarTitle;
    @BindView(R.id.attraction_toolbar)
    Toolbar attractionToolbar;
    @BindView(R.id.profile_btn)
    ImageView profileBtn;
    @BindView(R.id.nickname)
    TextView nickname;
    @BindView(R.id.footstep)
    TextView footstep;
    @BindView(R.id.login_method)
    TextView loginMethod;
    Unbinder unbinder;
    @BindView(R.id.log_btn)
    TextView logBtn;
    Context mCtx;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_etc, container, false);
        unbinder = ButterKnife.bind(this, view);
        ((AppCompatActivity) getActivity()).setSupportActionBar(attractionToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        mCtx = getActivity().getApplicationContext();
        if (SharedPreferences_M.getInstance(getActivity()).isLoggedIn()) {
            Glide.with(mCtx).load(SharedPreferences_M.getInstance(mCtx).getUser().getUser_pic_url()).into(profileBtn);
            nickname.setText(SharedPreferences_M.getInstance(mCtx).getUser().getUser_nickname());
            loginMethod.setText(SharedPreferences_M.getInstance(mCtx).getUser().getLogin_method());
            logBtn.setText("로그아웃");
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && resultCode == RESULT_OK) {
            profileBtn.setImageURI(Uri.parse(data.getStringExtra("editPicture")));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.profile_btn, R.id.log_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.profile_btn:
                startActivityForResult(new Intent(mCtx, CameraMask.class), 1000);
                break;
            case R.id.log_btn:
                if (SharedPreferences_M.getInstance(getActivity()).isLoggedIn()) {
                    SharedPreferences_M.getInstance(mCtx).logout();
                    profileBtn.setImageResource(R.drawable.activity_register_face_default);
                    nickname.setText(getText(R.string.guest)); // 밑의 getResources 와 이것의 차이는?
                    loginMethod.setText(getResources().getText(R.string.guest));
                    logBtn.setText("로그인");
                } else {
                    startActivity(new Intent(mCtx, Login.class));
                }
                break;
        }
    }






}
