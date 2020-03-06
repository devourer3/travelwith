package com.mymusic.orvai.travel_with.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.mymusic.orvai.travel_with.Interface.API;
import com.mymusic.orvai.travel_with.R;
import com.mymusic.orvai.travel_with.URLs.URLs;
import com.mymusic.orvai.travel_with.model.Overlap_check;
import com.mymusic.orvai.travel_with.model.User;
import com.mymusic.orvai.travel_with.sharef_mgr.SharedPreferences_M;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity {

    @BindView(R.id.button_naverlogin)
    OAuthLoginButton buttonNaverlogin;
    @BindView(R.id.x_btn)
    ImageView xBtn;
    @BindView(R.id.button_kakaologin)
    LoginButton buttonKakaologin;
    SessionCallback callback;
    private static final int RC_SIGN_IN = 9001;
    @BindView(R.id.button_googlelogin)
    SignInButton buttonGooglelogin;

    public FirebaseAuth mAuth;
    private OAuthLogin oAuthLogin;
    private List<AuthUI.IdpConfig> providers;
    private Context mCtx;
    public Retrofit retrofit;
    private API r_service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mCtx = this;

        if (SharedPreferences_M.getInstance(mCtx).isLoggedIn())
            finish();

        retrofit = new Retrofit.Builder().baseUrl(URLs.AWS_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        r_service = retrofit.create(API.class);
        providers = new ArrayList<>();
        providers.add(new AuthUI.IdpConfig.GoogleBuilder().build());

        /**
         * 네이버 로그인 준비
         */

        oAuthLogin = OAuthLogin.getInstance();
        oAuthLogin.init(mCtx, "_kOw6kze0jcB8ZTg2kWx", "aMIGArinIo", "걸어요, 여행");

        if (oAuthLogin.getAccessToken(this) != null) {
            // Token을 가지고 있다면 토큰을 폐기시키고 다시 핸들러 요청
            oAuthLogin.logout(this);
            buttonNaverlogin.setOAuthLoginHandler(new Naver_OAuthHandler(mCtx, oAuthLogin, this));
        } else {
            // 토큰이 없다면 바로 핸들러 요청
            buttonNaverlogin.setOAuthLoginHandler(new Naver_OAuthHandler(mCtx, oAuthLogin, this));
        }

        /**
         * 카카오톡 로그인 준비
         */

        callback = new SessionCallback(); // 카카오톡 로그인 인증 콜백
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen(); // 세션 오픈되어있는지 확인한 후 오픈되어있으면 바로 onSessionOpened 실행

        /**
         * 구글 파이어베이스 로그인 준비
         */

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SharedPreferences_M.getInstance(mCtx).isLoggedIn())
            finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
        oAuthLogin.logout(mCtx);
    } // 카카오톡 세션 자동 해제.


    /**
     * @param requestCode
     * @param resultCode
     * @param data        카카오톡 콜백 , 구글 프로필 정보 받아오기
     */


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // 구글구글구글구글구글구글구글구글구글구글구글구글구글구글구글구글구글구글구글구글
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data); // 성공? 실패 결과 값
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String nickname, profile_url, email, key_id, reg_method;
                nickname = user.getDisplayName();
                profile_url = String.valueOf(user.getPhotoUrl());
                email = user.getEmail();
                key_id = user.getUid();
                reg_method = "구글";
                final Intent intent = new Intent(mCtx, Nickname_set.class);
                intent.putExtra("nickname", nickname);
                intent.putExtra("pic_url", profile_url);
                intent.putExtra("email", email);
                intent.putExtra("social_uid", key_id);
                intent.putExtra("login_method", reg_method);
                overlap_check(key_id, reg_method, intent);
            } else {
                Toast.makeText(mCtx, "구글 로그인 실패!", Toast.LENGTH_SHORT).show();
            }
        }

        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) { // 카카오톡 정보 받아오기
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() { // 세션이 열려있다면
            requestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Logger.e(exception);
            }
        }
    } // 카카오톡 로그인 세션 인터페이스

    private void requestMe() { // 카카오톡카카오톡카카오톡카카오톡카카오톡카카오톡카카오톡카카오톡카카오톡카카오톡카카오톡
        UserManagement.getInstance().me(new MeV2ResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Toast.makeText(mCtx, "연결이 실패했습니다.", Toast.LENGTH_SHORT).show();
                redirectMainActivity();
            }

            @Override
            public void onSuccess(MeV2Response result) {
                String nickname, profile_url, email, key_id, reg_method;
                nickname = result.getNickname();
                profile_url = result.getProfileImagePath();
                email = result.getKakaoAccount().getEmail();
                key_id = String.valueOf(result.getId());
                reg_method = "카카오톡";
                Intent intent = new Intent(mCtx, Nickname_set.class);
                intent.putExtra("nickname", nickname);
                intent.putExtra("pic_url", profile_url);
                intent.putExtra("email", email);
                intent.putExtra("social_uid", key_id);
                intent.putExtra("login_method", reg_method);
                overlap_check(key_id, reg_method, intent);
            }
        });
    } // 카카오톡 프로필 가져오는 메소드

    protected void redirectMainActivity() { // 세션 연결이 실패하면 취할 메소드
        final Intent intent = new Intent(this, Main.class);
        startActivity(intent);
        finish();
    }

    public class Naver_OAuthHandler extends OAuthLoginHandler {
        private Context mCtx;
        private OAuthLogin mOAuthLoginModule;
        private Login loginactivity;

        Naver_OAuthHandler(Context mCtx, OAuthLogin oAuthLogin, Login loginactivity) {
            this.mCtx = mCtx;
            this.mOAuthLoginModule = oAuthLogin;
            this.loginactivity = loginactivity;
        }

        @Override
        public void run(boolean success) {
            if (success) {
                String accessToken = mOAuthLoginModule.getAccessToken(mCtx);
                String refreshToken = mOAuthLoginModule.getRefreshToken(mCtx);
                long expiresAt = mOAuthLoginModule.getExpiresAt(mCtx);
                String tokenType = mOAuthLoginModule.getTokenType(mCtx);
                ProfileTask task = new ProfileTask();
                task.execute(accessToken); // AsyncTask의 String[0] 번 째 값으로 넘김
            } else {
                String errorCode = mOAuthLoginModule.getLastErrorCode(mCtx).getCode();
                String errorDesc = mOAuthLoginModule.getLastErrorDesc(mCtx);
                Toast.makeText(mCtx, "errorCode:" + errorCode + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
            }
        }

        @SuppressLint("StaticFieldLeak")
        class ProfileTask extends AsyncTask<String, Void, String> { // 네이버네이버네이버네이버네이버네이버네이버네이버네이버네이버네이버
            String result;

            @Override
            protected String doInBackground(String... strings) {
                String token = strings[0]; // accessToken값
                String header = "Bearer " + token;
                try {
                    String apiURL = "https://openapi.naver.com/v1/nid/me";
                    URL url = new URL(apiURL);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Authorization", header);
                    int responseCode = con.getResponseCode();
                    BufferedReader br;
                    if (responseCode == 200) {
                        br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    } else {
                        br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    }
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = br.readLine()) != null) {
                        response.append(inputLine);
                    }
                    result = response.toString();
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.getString("resultcode").equals("00")) {
                        JSONObject jsonObject = new JSONObject(object.getString("response"));
                        Intent intent = new Intent(mCtx, Nickname_set.class);
                        String nickname, profile_url, email, key_id, reg_method;
                        nickname = jsonObject.getString("nickname");
                        profile_url = jsonObject.getString("profile_image");
                        key_id = jsonObject.getString("id");
                        reg_method = "네이버";
                        if (!jsonObject.has("email")) {
                            Toast.makeText(mCtx, "이메일 제공해 동의하지 않았습니다.", Toast.LENGTH_SHORT).show();
                            email = "없음";
                        } else {
                            email = jsonObject.getString("email");
                        }
                        intent.putExtra("nickname", nickname);
                        intent.putExtra("pic_url", profile_url);
                        intent.putExtra("email", email);
                        intent.putExtra("social_uid", key_id);
                        intent.putExtra("login_method", reg_method);
                        overlap_check(key_id, reg_method, intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void overlap_check(String key_id, String reg_method, final Intent intent) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if(!task.isSuccessful()) {
                Log.w("GB", "getInstanceId failed", task.getException());
                return;
            }
            String fcm_token = task.getResult().getToken();
            Call<Overlap_check> call = r_service.request_overlap_check(key_id, reg_method, fcm_token);
            call.enqueue(new Callback<Overlap_check>() {
                @Override
                public void onResponse(Call<Overlap_check> call, Response<Overlap_check> response) {
                    if (response.body().getError().equals("true")) { // 중복되는 아이디가 있으면, fcm_token은 바꿔줘
                        User user = new User(response.body().getUser_nickname(), response.body().getUser_pic_url(), response.body().getUser_uuid(), response.body().getReg_method(), response.body().getFcm_token());
                        SharedPreferences_M.getInstance(mCtx).userLogin(user);
                        Toast.makeText(Login.this, "이미 존재하는 아이디로 로그인 합니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<Overlap_check> call, Throwable t) {
                    Toast.makeText(Login.this, "등록 실패! 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @OnClick(R.id.x_btn)
    public void onViewClicked() {
        onBackPressed();
    }

    @OnClick(R.id.button_googlelogin)
    public void googleLogin() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), RC_SIGN_IN);
    }


}
