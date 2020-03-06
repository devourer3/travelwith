package com.mymusic.orvai.travel_with.Controller;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mymusic.orvai.travel_with.R;
import com.mymusic.orvai.travel_with.model.Chat_Message;
import com.mymusic.orvai.travel_with.sharef_mgr.SharedPreferences_M;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StreamingRoom_Chat_Adapter extends RecyclerView.Adapter<StreamingRoom_Chat_Adapter.ViewHolder> {

    private List<Chat_Message> chat_messages;
    public Context mCtx;

    public StreamingRoom_Chat_Adapter(List<Chat_Message> chat_messages, Context mCtx) {
        this.chat_messages = chat_messages;
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_streaming_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String my_id = SharedPreferences_M.getInstance(mCtx).getUser().getUser_nickname();
        Chat_Message chat_message = chat_messages.get(position);
        Glide.with(mCtx).load(chat_message.getPic_url()).into(holder.circleImageView);
        if((chat_message.getUser_id()).equals(my_id)){
            holder.user_id.setTextColor(Color.parseColor("#ffc778"));
            holder.user_id.setText(chat_message.getUser_id());
            holder.msg.setTextColor(Color.parseColor("#ffffff"));
            holder.msg.setText(chat_message.getMessage());
        } else {
            holder.user_id.setTextColor(Color.parseColor("#00ffd0"));
            holder.user_id.setText(chat_message.getUser_id());
            holder.msg.setTextColor(Color.parseColor("#ffffff"));
            holder.msg.setText(chat_message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return chat_messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView user_id;
        TextView msg;

        public ViewHolder(View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.s_user_pic);
            user_id = itemView.findViewById(R.id.s_user_id);
            msg = itemView.findViewById(R.id.s_chat_msg);
        }
    }
}
