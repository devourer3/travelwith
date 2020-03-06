package com.mymusic.orvai.travel_with.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mymusic.orvai.travel_with.Interface.API;
import com.mymusic.orvai.travel_with.R;
import com.mymusic.orvai.travel_with.URLs.URLs;
import com.mymusic.orvai.travel_with.Utils.FileUtils;
import com.mymusic.orvai.travel_with.model.Social_Registration_API;
import com.mymusic.orvai.travel_with.model.User;
import com.mymusic.orvai.travel_with.sharef_mgr.SharedPreferences_M;

import java.io.File;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Nickname_set extends AppCompatActivity {

    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.e_nickname)
    EditText eNickname;
    @BindView(R.id.register_btn)
    Button registerBtn;
    @BindView(R.id.back_btn)
    Button backBtn;
    @BindView(R.id.textView)
    TextView textView;
    String nickname, pic_url, email_txt, login_method, social_uid;
    @BindView(R.id.email)
    TextView email;
    public File imagefile;
    public Retrofit retrofit;
    private API r_service;
    public boolean image_switch = false;
    private Context mCtx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname_set);
        ButterKnife.bind(this);
        mCtx = getApplicationContext();
        retrofit = new Retrofit.Builder().baseUrl(URLs.AWS_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        r_service = retrofit.create(API.class);
        nickname = getIntent().getStringExtra("nickname");
        pic_url = getIntent().getStringExtra("pic_url");
        email_txt = getIntent().getStringExtra("email");
        social_uid = getIntent().getStringExtra("social_uid");
        login_method = getIntent().getStringExtra("login_method");

        Glide.with(this).load(pic_url).into(profileImage);
        eNickname.setText(nickname);
        email.setText(email_txt);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK && data != null){
            Uri imageuri = data.getData();
            imagefile = FileUtils.getFile(getApplicationContext(), imageuri);
            profileImage.setImageURI(imageuri);
            image_switch = true;
        }
    }

    @OnClick({R.id.profile_image, R.id.register_btn, R.id.back_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.profile_image:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 100);
                break;
            case R.id.register_btn:
                if(image_switch) { // 커스텀 이미지를 넣었을 때 회원가입
                    String uuid = UUID.randomUUID().toString().replace("-","");
                    RequestBody image_file = RequestBody.create(MediaType.parse("image/*"), imagefile);
                    RequestBody r_nickname = RequestBody.create(MediaType.parse("text/plain"), nickname);
                    RequestBody r_email = RequestBody.create(MediaType.parse("text/plain"), email_txt);
                    RequestBody r_uuid = RequestBody.create(MediaType.parse("text/plain"), uuid);
                    RequestBody r_loginmethod = RequestBody.create(MediaType.parse("text/plain"), login_method);
                    RequestBody r_social_uid = RequestBody.create(MediaType.parse("text/plain"), social_uid);
                    MultipartBody.Part upload_image_file = MultipartBody.Part.createFormData("user_picture", imagefile.getName(), image_file);
                    Call<Social_Registration_API> call = r_service.request_social_reg_image(r_nickname, r_email, r_uuid, r_loginmethod, r_social_uid, upload_image_file);
                    call.enqueue(new Callback<Social_Registration_API>() {
                        @Override
                        public void onResponse(Call<Social_Registration_API> call, Response<Social_Registration_API> response) {
                            User user = new User(response.body().getUser_nickname(), response.body().getUser_pic_url(), response.body().getUser_uuid(), response.body().getReg_method(), response.body().getFcm_token());
                            SharedPreferences_M.getInstance(mCtx).userLogin(user);
                            finish();
                        }
                        @Override
                        public void onFailure(Call<Social_Registration_API> call, Throwable t) {
                            Toast.makeText(Nickname_set.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else { // 그냥 기본이미지로 회원가입
                    String uuid = UUID.randomUUID().toString().replace("-","");
                    String fcm_token = FirebaseInstanceId.getInstance().getToken();
                    Call<Social_Registration_API> call = r_service.request_social_reg_no_image(nickname, email_txt, pic_url, uuid, login_method, social_uid, fcm_token);
                    call.enqueue(new Callback<Social_Registration_API>() {
                        @Override
                        public void onResponse(Call<Social_Registration_API> call, Response<Social_Registration_API> response) {
                            User user = new User(response.body().getUser_nickname(), response.body().getUser_pic_url(), response.body().getUser_uuid(), response.body().getReg_method(), response.body().getFcm_token());
                            SharedPreferences_M.getInstance(mCtx).userLogin(user);
                            finish();
                        }
                        @Override
                        public void onFailure(Call<Social_Registration_API> call, Throwable t) {
                            Toast.makeText(Nickname_set.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }


}
