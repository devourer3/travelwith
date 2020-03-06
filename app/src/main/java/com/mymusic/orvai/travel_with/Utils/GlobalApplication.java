package com.mymusic.orvai.travel_with.Utils;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatDialog;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.mymusic.orvai.travel_with.R;

public class GlobalApplication extends Application {
    private static GlobalApplication instance = null;
    AppCompatDialog appCompatDialog;

    public static GlobalApplication getGlobalApplicationContext() {
        if(instance == null)
            throw new IllegalStateException("this application does not inherit com.kakao.GlobalApplication");
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        KakaoSDK.init(new KakaoSDKAdapter());
    }

    private static class KakaoSDKAdapter extends KakaoAdapter {

        @Override
        public ISessionConfig getSessionConfig() {
            return new ISessionConfig() {
                @Override
                public AuthType[] getAuthTypes() {
                    return new AuthType[] {AuthType.KAKAO_TALK};
                }

                @Override
                public boolean isUsingWebviewTimer() {
                    return false;
                }

                @Override
                public boolean isSecureMode() {
                    return false;
                }

                @Override
                public ApprovalType getApprovalType() {
                    return ApprovalType.INDIVIDUAL;
                }

                @Override
                public boolean isSaveFormData() {
                    return true;
                }
            };
        }

        @Override
        public IApplicationConfig getApplicationConfig() {
            return new IApplicationConfig() {
                @Override
                public Context getApplicationContext() {
                    return GlobalApplication.getGlobalApplicationContext();
                }
            };
        }

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        instance = null;
    }

    public void progressOn(Context context) {
        appCompatDialog = new AppCompatDialog(context);
        appCompatDialog.setCancelable(false);
        appCompatDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        appCompatDialog.setContentView(R.layout.dialog_loading);
        appCompatDialog.show();
        final ImageView loading_image = appCompatDialog.findViewById(R.id.loading_image);
        Glide.with(context).load(R.drawable.loading_image).into(loading_image);
    }

    public void progressOFF() {
        if(appCompatDialog != null && appCompatDialog.isShowing()) {
            appCompatDialog.dismiss();
        }
    }

}