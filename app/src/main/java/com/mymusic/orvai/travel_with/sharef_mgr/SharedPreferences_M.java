package com.mymusic.orvai.travel_with.sharef_mgr;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.mymusic.orvai.travel_with.model.User;
import com.nhn.android.naverlogin.OAuthLogin;


/**
 * Created by orvai on 2018-01-16.
 */

public class SharedPreferences_M { // 로그인 세션을 유지하기 위해, 셰어드 프리퍼런스를 사용해야 한다.

    OAuthLogin oAuthLogin;
    FirebaseAuth mAuth;

    private static final String SHARED_PREF_NAME = "USER_INFO";
    private static final String KEY_USER_NICKNAME = "USER_NICKNAME";
    private static final String KEY_USER_PIC_URL = "USER_PIC";
    private static final String KEY_USER_UUID = "USER_UUID";
    private static final String KEY_USER_LOGIN_METHOD = "LOGIN_METHOD";
    private static final String KEY_USER_FCM_TOKEN = "FCM_TOKEN";
    private static SharedPreferences_M mInstance;

    private Context mCtx; // 안드로이드 시스템(context)을 다루는 변수

    private SharedPreferences_M(Context context) {
        mCtx = context; // 생성자에서 만들어진 어플리케이션 컨텍스트.
    }

    public static synchronized SharedPreferences_M getInstance(Context context) { // 외부에서 쉐어드 프리퍼런스에 접근하기 위한 메서드(객체를 새로 생성하지 않고도(new 없이도) 접근 가능), 싱글턴 패턴이다.
        if (mInstance == null) {
            mInstance = new SharedPreferences_M(context);
        }
        return mInstance;
    }

    public void userLogin(User user) { // 로그인 했을 시, 유저의 정보를 저장하기 위한 셰어드 프리퍼런스.
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_NICKNAME, user.getUser_nickname());
        editor.putString(KEY_USER_PIC_URL, user.getUser_pic_url());
        editor.putString(KEY_USER_UUID, user.getUser_uuid());
        editor.putString(KEY_USER_LOGIN_METHOD, user.getLogin_method());
        editor.putString(KEY_USER_FCM_TOKEN, user.getFmc_token());
        editor.apply();
    }

    public boolean isLoggedIn() { // 이미 로그인 했는지 안 했는지 알도록 하는 셰어드 프리퍼런스.
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_NICKNAME, null) != null;
    }

    public User getUser() { // 로그인 한 유저의 정보를 담은 유저 헬퍼 클래스, 셰어드프린퍼런스의 getter라 생각
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getString(KEY_USER_NICKNAME, null),
                sharedPreferences.getString(KEY_USER_PIC_URL, null),
                sharedPreferences.getString(KEY_USER_UUID, null),
                sharedPreferences.getString(KEY_USER_LOGIN_METHOD, null),
                sharedPreferences.getString(KEY_USER_FCM_TOKEN, null)
        );
    }

    public void logout() { // 로그아웃 메소드, 소셜 로그인 연동 일시해제 및 셰어드 프리퍼런스 다 지워놓음.
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch (sharedPreferences.getString(KEY_USER_LOGIN_METHOD, "")) {
            case "네이버":
                oAuthLogin = OAuthLogin.getInstance();
                oAuthLogin.init(mCtx, "_kOw6kze0jcB8ZTg2kWx", "aMIGArinIo", "걸어요, 여행");
                oAuthLogin.logout(mCtx); // 네이버로그인 로그아웃
                break;
            case "카카오톡":
                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                    }
                });
                break;
            case "구글":
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut(); // 구글 파이어베이스 로그아웃
                break;
        }
        editor.clear();
        editor.apply();


    }
}
