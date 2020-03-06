package com.mymusic.orvai.travel_with.Activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mymusic.orvai.travel_with.Interface.API;
import com.mymusic.orvai.travel_with.Interface.ServiceCallbacks;
import com.mymusic.orvai.travel_with.R;
import com.mymusic.orvai.travel_with.URLs.URLs;
import com.mymusic.orvai.travel_with.model.Room_Registration_API;
import com.mymusic.orvai.travel_with.service.Chat_Channel_Service;
import com.mymusic.orvai.travel_with.sharef_mgr.SharedPreferences_M;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Streaming_Create extends AppCompatActivity implements ServiceCallbacks{

    /**
     * 스트리밍 방 만들면 생기는 다이얼로그 창
     */

    @BindView(R.id.room_name)
    EditText roomName;
    @BindView(R.id.create_btn)
    Button createBtn;
    @BindView(R.id.cancel_btn)
    Button cancelBtn;
    @BindView(R.id.room_max_user)
    Spinner roomMaxUser;
    public String room_name, user_id, user_location, user_uuid;
    int room_max_user;
    public Retrofit retrofit;
    public API r_service;
    public Intent intent;
    private Chat_Channel_Service c_service;
    boolean isBound = false;
    private LocationManager locationManager;
    private Geocoder geocoder;
    private double latitude, longitude;
    private Context mCtx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming_create);
        ButterKnife.bind(this);
        getLocation();
        init();
    }

    private void init() {
        mCtx = getApplicationContext();
        retrofit = new Retrofit.Builder().baseUrl(URLs.AWS_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        r_service = retrofit.create(API.class);
        room_max_user = 100;
        user_id = SharedPreferences_M.getInstance(this).getUser().getUser_nickname();
        user_uuid = SharedPreferences_M.getInstance(this).getUser().getUser_uuid();
        intent = new Intent(this, Chat_Channel_Service.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        final String[] data = getResources().getStringArray(R.array.max_user);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, data);
        roomMaxUser.setAdapter(adapter);
        roomMaxUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (data[position]) {
                    case "100명":
                        room_max_user = 100;
                        break;
                    case "200명":
                        room_max_user = 200;
                        break;
                    case "500명":
                        room_max_user = 500;
                        break;
                    case "1000명":
                        room_max_user = 1000;
                        break;
                    case "2000명":
                        room_max_user = 2000;
                        break;
                    case "5000명":
                        room_max_user = 5000;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Chat_Channel_Service.MyLocalBinder myLocalBinder = (Chat_Channel_Service.MyLocalBinder) service;
            c_service = myLocalBinder.getService();
            c_service.setCallbacks(Streaming_Create.this);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };


    @OnClick({R.id.create_btn, R.id.cancel_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.create_btn:
                room_name = roomName.getText().toString();
                if (TextUtils.isEmpty(room_name)) {
                    roomName.setError("방 제목을 입력해 주세요.");
                    roomName.requestFocus();
                } else {
                    Call<Room_Registration_API> call = r_service.request_room_create(room_name, String.valueOf(room_max_user), user_id, user_location, user_uuid, URLs.AWS_STREAMING_THUMBNAIL_URL);
                    call.enqueue(new Callback<Room_Registration_API>() { // 레디스에 먼저 방을 만들겠다는 신호를 보내고
                        @Override
                        public void onResponse(@NonNull Call<Room_Registration_API> call, @NonNull Response<Room_Registration_API> response) { // 레디스에 방 목록이 만들어 졌으면
                            String room_number = response.body().getRoom_number(); // 레디스에서 만들어진 방 번호를 받고
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("request", "create_room");
                            jsonObject.put("user_id", user_id);
                            jsonObject.put("room_number", room_number);
                            String job = jsonObject.toJSONString();
                            c_service.sendMessage(job); // 해당 방번호로 채팅방을 개설하기 위한 Netty 서버에 메시지 전송.
                        }
                        @Override
                        public void onFailure(@NonNull Call<Room_Registration_API> call, @NonNull Throwable t) {
                            Toast.makeText(Streaming_Create.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            case R.id.cancel_btn:
                onBackPressed();
                break;
        }
    }


    @Override
    public void create_room_response_ok(String streaming_key, String room_number) {
        Intent intent = new Intent(mCtx, Streaming_Room.class);
        intent.putExtra("stream_key", streaming_key);
        intent.putExtra("room_number", room_number);
        intent.putExtra("room_name", room_name);
        intent.putExtra("location", user_location);
        startActivity(intent);
        finish();
    }

    @Override
    public void delete_room_response_ok() {

    } // 안씀.

    @Override
    public void receive_msg(JSONObject message) {

    }


    @SuppressLint("MissingPermission")
    private void getLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(false);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.getType() != ConnectivityManager.TYPE_MOBILE) { // 모바일 네트워크 연결 상태가 아니면,
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); // 네트워크 프로바이더로 지리 정보를 얻음
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            } else { // 모바일 네트워크 + WIFI 다 되면
                String provider = locationManager.getBestProvider(criteria, true); // 최선의 프로바이더로 지리 정보를 얻음
                Location location = locationManager.getLastKnownLocation(provider);
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }
        geocoder = new Geocoder(this);
        try {
            List<Address> result = geocoder.getFromLocation(latitude, longitude, 1);
            user_location = result.get(0).getSubLocality() + ", " + result.get(0).getAdminArea();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
