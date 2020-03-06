package com.mymusic.orvai.travel_with.Controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mymusic.orvai.travel_with.Interface.API;
import com.mymusic.orvai.travel_with.Interface.ItemClickListener;
import com.mymusic.orvai.travel_with.R;
import com.mymusic.orvai.travel_with.URLs.URLs;
import com.mymusic.orvai.travel_with.Utils.GlobalApplication;
import com.mymusic.orvai.travel_with.model.Room_Enter_API;
import com.mymusic.orvai.travel_with.model.Streaming_Room_List_API;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StreamingRoom_Adapter extends RecyclerView.Adapter<StreamingRoom_Adapter.ViewHolder> {

    private List<Streaming_Room_List_API> room_list;
    private Context mCtx;

    public StreamingRoom_Adapter(List<Streaming_Room_List_API> room_list, Context mCtx) {
        this.room_list = room_list;
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_streaming_room_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Streaming_Room_List_API room_api_result = room_list.get(position);
        String imageSTR = room_api_result.getRoom_thumbnail();
        if(imageSTR.length() >= 100) {
            Glide.with(mCtx).asBitmap().load(decodeImage(imageSTR)).into(holder.thumbnail);

        } else {
            Glide.with(mCtx).load(room_api_result.getRoom_thumbnail()).into(holder.thumbnail); // 썸네일
        }
        holder.stream_subject.setText(room_api_result.getRoom_name()); // 제목
        holder.streamer_id.setText(room_api_result.getRoom_streamer()); // 아이디
        holder.stream_location.setText(room_api_result.getRoom_location()); // 위치
        holder.stream_viewers.setText(" "+room_api_result.getRoom_users()+"명"); // 시청자 수
        
        holder.setItemClickListener((view, position1, isLongClick) -> {
            if(!isLongClick) {
                mCtx = view.getContext();
                final String room_number = room_api_result.getRoom_number();
                final String room_name = room_api_result.getRoom_name();
                URLs.STREAM_KEY = room_api_result.getRoom_stream_key();
                URLs.STREAMING_START_TIME = room_api_result.getStreaming_start_time();
                GlobalApplication.getGlobalApplicationContext().progressOn(mCtx);
                Retrofit retrofit = new Retrofit.Builder().baseUrl(URLs.AWS_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
                API r_service = retrofit.create(API.class);
                Call<Room_Enter_API> call = r_service.request_room_enter(room_number);
                call.enqueue(new Callback<Room_Enter_API>() {
                    @Override
                    public void onResponse(Call<Room_Enter_API> call, Response<Room_Enter_API> response) {
                        Log.d(URLs.TAG, response.body().getResult());
                        Log.d(URLs.TAG+"방번호", room_api_result.getRoom_number());
                        if((response.body().getResult()).equals("success")) {
                            Intent intent = new Intent("CHANNEL_SERVICE");
                            intent.putExtra("request", "join_room");
                            intent.putExtra("room_number", room_number);
                            intent.putExtra("room_name", room_name);
                            mCtx.sendBroadcast(intent);
                        } else {
                            GlobalApplication.getGlobalApplicationContext().progressOFF();
                            Toast.makeText(mCtx, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Room_Enter_API> call, Throwable t) {
                    }
                });
            }
        });
    }



    @Override
    public int getItemCount() {
        return room_list.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ItemClickListener itemClickListener;
        private ImageView thumbnail;
        private TextView stream_subject;
        private TextView streamer_id;
        private TextView stream_location;
        private TextView stream_viewers;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            thumbnail = itemView.findViewById(R.id.stream_thumbnail);
            stream_subject = itemView.findViewById(R.id.stream_subject);
            streamer_id = itemView.findViewById(R.id.streamer_id);
            stream_location = itemView.findViewById(R.id.streamer_location);
            stream_viewers = itemView.findViewById(R.id.stream_viewers);
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

    public Bitmap decodeImage(String encodedString) {
        byte[] decodedString = Base64.decode(encodedString, Base64.DEFAULT);
        Bitmap decodedIMG = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedIMG;
    }

}
