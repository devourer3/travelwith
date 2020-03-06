package com.mymusic.orvai.travel_with.Controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mymusic.orvai.travel_with.Activity.VodPlayer;
import com.mymusic.orvai.travel_with.Interface.ItemClickListener;
import com.mymusic.orvai.travel_with.R;
import com.mymusic.orvai.travel_with.URLs.URLs;
import com.mymusic.orvai.travel_with.Utils.GlobalApplication;
import com.mymusic.orvai.travel_with.Utils.Retrofit_Builder;
import com.mymusic.orvai.travel_with.model.Vod_Chat_Message_API;
import com.mymusic.orvai.travel_with.model.Vod_Enter_API;
import com.mymusic.orvai.travel_with.model.Vod_List_Api;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Vod_Chat_Adapter extends RecyclerView.Adapter<Vod_Chat_Adapter.ViewHolder> {

    private List<Vod_Chat_Message_API> vod_chat_list;
    private Context mCtx;

    public Vod_Chat_Adapter(List<Vod_Chat_Message_API> vod_chat_list, Context mCtx) {
        this.vod_chat_list = vod_chat_list;
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_watching_chat, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Vod_Chat_Message_API vod_chat_api_result = vod_chat_list.get(position);
        Glide.with(mCtx).load(vod_chat_api_result.getPic_url()).into(holder.circleImageView);
        holder.user_id.setTextColor(Color.parseColor("#ff8c04"));
        holder.user_id.setText(vod_chat_api_result.getUser_id());
        holder.msg.setTextColor(Color.parseColor("#ffffff"));
        holder.msg.setText(vod_chat_api_result.getMessage());
    }


    @Override
    public int getItemCount() {
        return vod_chat_list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView user_id;
        TextView msg;

        ViewHolder(View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.w_user_pic);
            user_id = itemView.findViewById(R.id.w_user_id);
            msg = itemView.findViewById(R.id.w_chat_msg);
        }
    }

}
