package com.mymusic.orvai.travel_with.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mymusic.orvai.travel_with.Controller.Attraction_More_Images_Adapter;
import com.mymusic.orvai.travel_with.R;
import com.mymusic.orvai.travel_with.Utils.Retrofit_Builder;
import com.mymusic.orvai.travel_with.model.Attraction_Detail_Common_Result_API;
import com.mymusic.orvai.travel_with.model.Attraction_Detail_Images_Result_API;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Attraction_Detail extends AppCompatActivity {

    @BindView(R.id.attraction_toolbar_title)
    TextView attractionToolbarTitle;
    @BindView(R.id.detail_toolbar)
    Toolbar detailToolbar;
    @BindView(R.id.att_detail_image)
    ImageView attDetailImage;
    @BindView(R.id.att_detail_summary)
    TextView attDetailSummary;
    @BindView(R.id.more_images)
    RecyclerView attmoreImages_recycler;
    private List<Attraction_Detail_Images_Result_API.ADI_Item> adi_result_list;
    private Context mCtx;
    private RecyclerView.Adapter adapter;
    private Gson gson;
    public String title, content_id, type_id, tel_number, map_x, map_y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_detail);
        ButterKnife.bind(this);
        mCtx = getApplicationContext();
        init();
        first_api_request();
        second_api_request();
    }

    private void init() {
        setSupportActionBar(detailToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Intent intent = getIntent();
        content_id = intent.getStringExtra("content_id");
        type_id = intent.getStringExtra("type_id");
        tel_number = intent.getStringExtra("tel_number");
        title = intent.getStringExtra("title");
        attractionToolbarTitle.setText(title);
        adi_result_list = new ArrayList<>();
        adapter = new Attraction_More_Images_Adapter(mCtx, adi_result_list);
        attmoreImages_recycler.setHasFixedSize(true);
        attmoreImages_recycler.setAdapter(adapter);
    }

    private void first_api_request() {
        gson = new GsonBuilder().setLenient().create();
        Retrofit_Builder.get_Tour_Api_service(gson).get_attraction_detail_common(content_id, type_id).enqueue(new Callback<Attraction_Detail_Common_Result_API>() {
            @Override
            public void onResponse(Call<Attraction_Detail_Common_Result_API> call, Response<Attraction_Detail_Common_Result_API> response) {
                attDetailSummary.setText(Objects.requireNonNull(response.body()).getResponse().getBody().getItems().getItem().getOverview().replace("<br>", "").replace("<br />", ""));
                Glide.with(mCtx).load(Objects.requireNonNull(response.body()).getResponse().getBody().getItems().getItem().getFirstimage()).into(attDetailImage);
                map_x = response.body().getResponse().getBody().getItems().getItem().getMapx();
                map_y = response.body().getResponse().getBody().getItems().getItem().getMapy();
            }

            @Override
            public void onFailure(Call<Attraction_Detail_Common_Result_API> call, Throwable t) {
            }
        });
    }

    private void second_api_request() {
        gson = new GsonBuilder().setLenient().create();
        Retrofit_Builder.get_Tour_Api_service(gson).get_attraction_detail_more_images(content_id, type_id).enqueue(new Callback<Attraction_Detail_Images_Result_API>() {
            @Override
            public void onResponse(Call<Attraction_Detail_Images_Result_API> call, Response<Attraction_Detail_Images_Result_API> response) {
                List items = response.body().getResponse().getBody().getItems().getItem();
                adi_result_list.addAll(items);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Attraction_Detail_Images_Result_API> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_attraction_detail_menu, menu);
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.att_detail_call:
                if (tel_number.contains("-")) {
                    tel_number.replace("-", "");
                }
                Intent intent_call = new Intent(Intent.ACTION_DIAL);
                intent_call.setData(Uri.parse("tel:" + tel_number));
                startActivity(intent_call);
                break;
            case R.id.att_detail_map:
                Intent intent_map = new Intent(Intent.ACTION_VIEW);
                intent_map.setData(Uri.parse("geo:" + map_y + "," + map_x));
                startActivity(intent_map);
                break;
        }
        return true;

    }
}
