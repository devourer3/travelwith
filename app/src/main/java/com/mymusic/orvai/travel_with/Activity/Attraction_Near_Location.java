package com.mymusic.orvai.travel_with.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mymusic.orvai.travel_with.R;
import com.mymusic.orvai.travel_with.Utils.Retrofit_Builder;
import com.mymusic.orvai.travel_with.model.Attraction_Near_User_Location;
import com.mymusic.orvai.travel_with.sharef_mgr.SharedPreferences_M;

import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Attraction_Near_Location extends AppCompatActivity implements OnMapReadyCallback {

    private LocationManager locationManager;
    private double latitude, longitude;
    private Context mCtx;
    public GoogleMap mMap;
    private Geocoder geocoder;
    public String user_address;
    private static final String TAG = "GB";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_near_location);
        ButterKnife.bind(this);
        mCtx = getApplicationContext();
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gMapFragment)).getMapAsync(this);
        getLocation();
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
            user_address = result.get(0).getSubLocality() + ", " + result.get(0).getAdminArea();
            Log.d(TAG, user_address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng my_location = new LatLng(latitude, longitude);
        CameraPosition position = new CameraPosition.Builder().target(my_location).zoom(13.5f).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
        users_location();
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(my_location).fillColor(Color.parseColor("#99f3f3f3")).radius(2500).strokeWidth(10).strokeColor(Color.parseColor("#ccffb638"));
        mMap.addCircle(circleOptions);
        mMap.setOnMarkerClickListener(marker -> {
            Log.d(TAG + "태그값", String.valueOf(marker.getTag()));
            return false;
        });
        mMap.setOnInfoWindowClickListener(marker -> {
            Intent intent = new Intent(mCtx, Call_Start_Dialog.class);
            intent.putExtra("selected_user_uuid", String.valueOf(marker.getTag()));
            startActivity(intent);
        });
    }

    private void users_location() {
        Retrofit_Builder.get_Aws_Api_Service().request_coordinate(SharedPreferences_M.getInstance(mCtx).getUser().getUser_nickname(), latitude, longitude, user_address).enqueue(new Callback<List<Attraction_Near_User_Location>>() {
            @Override
            public void onResponse(Call<List<Attraction_Near_User_Location>> call, Response<List<Attraction_Near_User_Location>> response) {
                for (int i = 0; i < response.body().size(); i++) {
                    Log.d(TAG, response.body().get(i).userNickname);
                    double user_latitude = Double.parseDouble(response.body().get(i).getUserLatitude());
                    double user_longitude = Double.parseDouble(response.body().get(i).getUserLongitude());
                    float accessibility = getDistance(latitude, longitude, user_latitude, user_longitude);
                    if (accessibility < 2500.0f) { // 내 주변 2.5km 이하만 표시
                        LatLng coordinate = new LatLng(user_latitude, user_longitude);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.attraction_activity_user_image));
                        markerOptions.position(coordinate);
                        markerOptions.title(response.body().get(i).getUserNickname());
                        markerOptions.snippet(response.body().get(i).getUserNickname()+"님의 상세페이지");
                        mMap.addMarker(markerOptions).setTag(response.body().get(i).getUserUUID());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Attraction_Near_User_Location>> call, Throwable t) {
            }
        });
    }


    private static float getDistance(double startlat, double startlot,
                                     double endlat, double endlot) {
        float[] result = new float[1];
        Location.distanceBetween(startlat, startlot, endlat, endlot, result);
        return result[0];
    }

}
