package com.mymusic.orvai.travel_with.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mymusic.orvai.travel_with.Activity.Attraction_Near_Location;
import com.mymusic.orvai.travel_with.Controller.Attraction_Adapter;
import com.mymusic.orvai.travel_with.Interface.API;
import com.mymusic.orvai.travel_with.R;
import com.mymusic.orvai.travel_with.URLs.URLs;
import com.mymusic.orvai.travel_with.Utils.Recycler_Streaming_room_deco;
import com.mymusic.orvai.travel_with.model.Attraction_Result_API;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Attraction extends Fragment {

    @BindView(R.id.attraction_toolbar_title)
    public TextView attractionToolbarTitle;
    @BindView(R.id.attraction_toolbar)
    public Toolbar attractionToolbar;
    Unbinder unbinder;
    Context mCtx;
    public API api_service;
    @BindView(R.id.location_map)
    Button locationMap;
    private int city_code, district_code, page_no;
    @BindView(R.id.district1)
    public Spinner city;
    @BindView(R.id.district2)
    public Spinner district;
    @BindView(R.id.search)
    public Button search;
    @BindView(R.id.attraction_recycler)
    public RecyclerView attractionRecycler;
    private ArrayAdapter<CharSequence> city_adapter, district_adapter;
    private List<Attraction_Result_API.Item> att_list;
    private RecyclerView.Adapter adapter;
    public LinearLayoutManager linearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attraction, container, false);
        unbinder = ButterKnife.bind(this, view);
        mCtx = view.getContext();
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(attractionToolbar);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(false);
        init_recyclerView();
        init_spinner();
        init_retrofit();
        return view;
    }


    private void init_recyclerView() {
        page_no = 1;
        att_list = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(mCtx);
        attractionRecycler.setLayoutManager(new LinearLayoutManager(mCtx));
        adapter = new Attraction_Adapter(mCtx, att_list);
        attractionRecycler.addItemDecoration(new Recycler_Streaming_room_deco());
        attractionRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    page_no++;
                    api_pagination(city_code, district_code, page_no);
                }
            }
        });
        attractionRecycler.setAdapter(adapter);
    }

    private void init_retrofit() {
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(URLs.TOUR_API_3_0_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
        api_service = retrofit.create(API.class);
    }

    private void api_request(int city_code, int district_code, int page_no) {
        Call<Attraction_Result_API> call = api_service.get_attractions_areaBased(city_code, district_code, page_no, "AND", "Travel_with", "json");
        call.enqueue(new Callback<Attraction_Result_API>() {
            @Override
            public void onResponse(Call<Attraction_Result_API> call, Response<Attraction_Result_API> response) {
                List item_list = response.body().getResponse().getBody().getItems().getItem();
                att_list.addAll(item_list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Attraction_Result_API> call, Throwable t) {
                Log.d("실패사유", t.getMessage());
            }
        });
    }

    private void api_pagination(int city_code, int district_code, int page_no) {
        Toast.makeText(mCtx, page_no + "페이지 째 입니다!", Toast.LENGTH_SHORT).show();
        Call<Attraction_Result_API> call = api_service.get_attractions_areaBased(city_code, district_code, page_no, "AND", "Travel_with", "json");
        call.enqueue(new Callback<Attraction_Result_API>() {
            @Override
            public void onResponse(Call<Attraction_Result_API> call, Response<Attraction_Result_API> response) {
                List item_list = response.body().getResponse().getBody().getItems().getItem();
                att_list.addAll(item_list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Attraction_Result_API> call, Throwable t) {
                Log.d("실패사유", t.getMessage());
            }
        });
    }

    private void init_spinner() {
        city_adapter = ArrayAdapter.createFromResource(mCtx, R.array.city, R.layout.support_simple_spinner_dropdown_item);
        city.setAdapter(city_adapter);
        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (Objects.requireNonNull(city_adapter.getItem(position)).toString()) {
                    case "지역선택":
                        city_code = 0;
                        break;
                    case "서울":
                        city_code = 1;
                        district_adapter = ArrayAdapter.createFromResource(mCtx, R.array.seoul_district, R.layout.support_simple_spinner_dropdown_item);
                        district.setAdapter(district_adapter);
                        district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                switch (district_adapter.getItem(position).toString()) {
                                    case "지역선택":
                                        district_code = 0;
                                    case "강남구":
                                        district_code = 1;
                                        break;
                                    case "강동구":
                                        district_code = 2;
                                        break;
                                    case "강북구":
                                        district_code = 3;
                                        break;
                                    case "강서구":
                                        district_code = 4;
                                        break;
                                    case "관악구":
                                        district_code = 5;
                                        break;
                                    case "광진구":
                                        district_code = 6;
                                        break;
                                    case "구로구":
                                        district_code = 7;
                                        break;
                                    case "금천구":
                                        district_code = 8;
                                        break;
                                    case "노원구":
                                        district_code = 9;
                                        break;
                                    case "도봉구":
                                        district_code = 10;
                                        break;
                                    case "동대문구":
                                        district_code = 11;
                                        break;
                                    case "동작구":
                                        district_code = 12;
                                        break;
                                    case "마포구":
                                        district_code = 13;
                                        break;
                                    case "서대문구":
                                        district_code = 14;
                                        break;
                                    case "서초구":
                                        district_code = 15;
                                        break;
                                    case "성동구":
                                        district_code = 16;
                                        break;
                                    case "성북구":
                                        district_code = 17;
                                        break;
                                    case "송파구":
                                        district_code = 18;
                                        break;
                                    case "양천구":
                                        district_code = 19;
                                        break;
                                    case "영등포구":
                                        district_code = 20;
                                        break;
                                    case "용산구":
                                        district_code = 21;
                                        break;
                                    case "은평구":
                                        district_code = 22;
                                        break;
                                    case "종로구":
                                        district_code = 23;
                                        break;
                                    case "중구":
                                        district_code = 24;
                                        break;
                                    case "중랑구":
                                        district_code = 25;
                                        break;
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                        break;
                    case "인천":
                        city_code = 2;
                        district_adapter = ArrayAdapter.createFromResource(mCtx, R.array.incheon_district, R.layout.support_simple_spinner_dropdown_item);
                        district.setAdapter(district_adapter);
                        district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                switch (Objects.requireNonNull(district_adapter.getItem(position)).toString()) {
                                    case "시군구 선택":
                                        district_code = 0;
                                        break;
                                    case "강화군":
                                        district_code = 1;
                                        break;
                                    case "계양구":
                                        district_code = 2;
                                        break;
                                    case "남구":
                                        district_code = 3;
                                        break;
                                    case "남동구":
                                        district_code = 4;
                                        break;
                                    case "동구":
                                        district_code = 5;
                                        break;
                                    case "부평구":
                                        district_code = 6;
                                        break;
                                    case "서구":
                                        district_code = 7;
                                        break;
                                    case "연수구":
                                        district_code = 8;
                                        break;
                                    case "옹진군":
                                        district_code = 9;
                                        break;
                                    case "중구":
                                        district_code = 10;
                                        break;
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.search)
    public void onViewClicked() {
        page_no = 1;
        att_list.clear();
        api_request(city_code, district_code, page_no);
    }

    @OnClick(R.id.location_map)
    public void onViewClicked2() {
        Intent intent = new Intent(mCtx, Attraction_Near_Location.class);
        startActivity(intent);
    }
}
//    @OnClick(R.id.button2)
//    public void onViewClicked1() {
//        String token = FirebaseInstanceId.getInstance().getToken();
//        Log.d("토큰값", token);
//    }

// 샤오미 토큰: fkUbK9UmRzM:APA91bFU44p71RIJIg5HATYon8avrCXH-W0CIpcvIuMzeduIR9V2YBaDeNrdYhDDRgIvHw9N4irqlmv9m5Lf9XfkPOltGNyVEOawlu22I42xZZU8UZ6IR7OpYAaKu2v27mZ6bsUJ5o6y

// 노키아 토큰: cgQLX7Sh_yQ:APA91bE-XjXUKK-OL7GSHsAbsi7vqrlCZa9bfX2e_yqo_u59R_sSuhL7q8uF3qX5kHqtcE9Mxo9Ub9W9z0zJXo36Ry7QAav4r0qzRyk8NKektSpm_kwqh6DkZy7b6TfSJPmK_uiIO9XIq1NPMHq1w-3yEaM4exhwqw


//    @OnClick(R.id.calling)
//    public void onViewClicked2() {
//        Intent intent = new Intent(mCtx, Call_Start_Dialog.class);
//        intent.putExtra("token_number", "fkUbK9UmRzM:APA91bFU44p71RIJIg5HATYon8avrCXH-W0CIpcvIuMzeduIR9V2YBaDeNrdYhDDRgIvHw9N4irqlmv9m5Lf9XfkPOltGNyVEOawlu22I42xZZU8UZ6IR7OpYAaKu2v27mZ6bsUJ5o6y");
//        startActivity(intent);
//    }