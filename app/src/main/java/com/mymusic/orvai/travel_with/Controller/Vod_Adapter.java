package com.mymusic.orvai.travel_with.Controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mymusic.orvai.travel_with.Activity.VodPlayer;
import com.mymusic.orvai.travel_with.Interface.API;
import com.mymusic.orvai.travel_with.Interface.ItemClickListener;
import com.mymusic.orvai.travel_with.R;
import com.mymusic.orvai.travel_with.URLs.URLs;
import com.mymusic.orvai.travel_with.Utils.GlobalApplication;
import com.mymusic.orvai.travel_with.Utils.Retrofit_Builder;
import com.mymusic.orvai.travel_with.model.Room_Enter_API;
import com.mymusic.orvai.travel_with.model.Vod_Enter_API;
import com.mymusic.orvai.travel_with.model.Vod_List_Api;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Vod_Adapter extends RecyclerView.Adapter<Vod_Adapter.ViewHolder> {

    private List<Vod_List_Api> vod_list;
    private Context mCtx;


    public Vod_Adapter(List<Vod_List_Api> vod_list, Context mCtx) {
        this.vod_list = vod_list;
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vod_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Vod_List_Api vod_api_result = vod_list.get(position);
        String imageSTR = vod_api_result.getVod_thumbnail();
        if (imageSTR.length() >= 100) {
            Glide.with(mCtx).asBitmap().load(decodeImage(imageSTR)).into(holder.thumbnail);

        } else {
            Glide.with(mCtx).load(vod_api_result.getVod_thumbnail()).into(holder.thumbnail); // 썸네일
        }
        holder.vod_subject.setText(vod_api_result.getVod_name()); // 제목
        holder.vod_streamer.setText(vod_api_result.getVod_streamer()); // 아이디
        holder.vod_location.setText(vod_api_result.getVod_location()); // 위치
        holder.vod_watchers.setText(" "+vod_api_result.getVod_watchers()); // 시청자 수

        holder.setItemClickListener((view, position1, isLongClick) -> {
            if (!isLongClick) {
                mCtx = view.getContext();
                final String vod_number = vod_api_result.getVod_number();
                URLs.VOD_KEY = vod_api_result.getVod_key();
                GlobalApplication.getGlobalApplicationContext().progressOn(mCtx);
                Retrofit_Builder.get_Aws_Api_Service().request_vod_enter(vod_number).enqueue(new Callback<Vod_Enter_API>() {
                    @Override
                    public void onResponse(Call<Vod_Enter_API> call, Response<Vod_Enter_API> response) {
                        if(response.body().getResult().equals("success"))
                        GlobalApplication.getGlobalApplicationContext().progressOFF();
                        mCtx.startActivity(new Intent(mCtx, VodPlayer.class).putExtra("vod_key", URLs.VOD_KEY).putExtra("vod_number", vod_number));
                    }

                    @Override
                    public void onFailure(Call<Vod_Enter_API> call, Throwable t) {

                    }
                });
            }
        });
    }


    @Override
    public int getItemCount() {
        return vod_list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ItemClickListener itemClickListener;
        private ImageView thumbnail;
        private TextView vod_subject;
        private TextView vod_streamer;
        private TextView vod_location;
        private TextView vod_watchers;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            thumbnail = itemView.findViewById(R.id.vod_thumbnail);
            vod_subject = itemView.findViewById(R.id.vod_subject);
            vod_streamer = itemView.findViewById(R.id.vod_streamer);
            vod_location = itemView.findViewById(R.id.vod_location);
            vod_watchers = itemView.findViewById(R.id.vod_watcher);
        }

        void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }


        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), true);
            return true;
        }
    }

    private Bitmap decodeImage(String encodedString) {
        byte[] decodedString = Base64.decode(encodedString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

}
