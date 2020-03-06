package com.mymusic.orvai.travel_with.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mymusic.orvai.travel_with.R;
import com.mymusic.orvai.travel_with.Utils.Retrofit_Builder;
import com.mymusic.orvai.travel_with.model.Conference_Room_Registration_API;
import com.mymusic.orvai.travel_with.sharef_mgr.SharedPreferences_M;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Conference_Dialog extends Dialog {

    Context mCtx;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.mesgase)
    EditText conferenceTitle;
    @BindView(R.id.regButton)
    Button regButton;
    @BindView(R.id.cancelButton)
    Button cancelButton;

    public Conference_Dialog(@NonNull Context context) {
        super(context);
        mCtx = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.conference_registration_custom_dialog);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.regButton, R.id.cancelButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.regButton:
                if(TextUtils.isEmpty(conferenceTitle.getText().toString())) {
                    conferenceTitle.setError("방 제목을 입력하세요!!!");
                    conferenceTitle.requestFocus();
                } else {
                    Retrofit_Builder.get_Aws_Api_Service().request_conference_create(conferenceTitle.getText().toString(), SharedPreferences_M.getInstance(mCtx).getUser().getUser_uuid()).enqueue(new Callback<Conference_Room_Registration_API>() {
                        @Override
                        public void onResponse(Call<Conference_Room_Registration_API> call, Response<Conference_Room_Registration_API> response) {
                            if(response.body().getResult().equals("success")) {
                                Intent intent = new Intent();
                                intent.setClassName("com.example.admin.jitsiman", "com.example.admin.jitsiman.MainActivity");
                                intent.putExtra("conference_key", response.body().getKey());
                                intent.putExtra("conference_number",response.body().getNumber());
                                mCtx.startActivity(intent);
                                dismiss();
                            } else {
                                Toast.makeText(mCtx, "실패!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Conference_Room_Registration_API> call, Throwable t) {

                        }
                    });
                }
                break;
            case R.id.cancelButton:
                dismiss();
                break;
        }
    }
}
