package com.mymusic.orvai.travel_with.Controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mymusic.orvai.travel_with.Interface.ItemClickListener;
import com.mymusic.orvai.travel_with.R;
import com.mymusic.orvai.travel_with.Utils.GlobalApplication;
import com.mymusic.orvai.travel_with.Utils.Retrofit_Builder;
import com.mymusic.orvai.travel_with.model.Conference_Room_Enter_API;
import com.mymusic.orvai.travel_with.model.Conference_Room_List_API;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConferenceRoom_Adapter extends RecyclerView.Adapter<ConferenceRoom_Adapter.ViewHolder> {

    private List<Conference_Room_List_API> conference_list;
    private Context mCtx;

    public ConferenceRoom_Adapter(List<Conference_Room_List_API> conference_list, Context mCtx) {
        this.conference_list = conference_list;
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conference_room_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Conference_Room_List_API conference_list_api = conference_list.get(position);

        String conference_number = conference_list_api.getConference_number();
        holder.conference_subject.setText(conference_list_api.getConference_name()); // 제목
        holder.conference_participants.setText(" "+conference_list_api.getConference_users()+"명"); // 시청자 수
        
        holder.setItemClickListener((view, position1, isLongClick) -> {
            if(!isLongClick) {
                mCtx = view.getContext();
                GlobalApplication.getGlobalApplicationContext().progressOn(mCtx);
                Retrofit_Builder.get_Aws_Api_Service().request_conference_enter(conference_number).enqueue(new Callback<Conference_Room_Enter_API>() {
                    @Override
                    public void onResponse(Call<Conference_Room_Enter_API> call, Response<Conference_Room_Enter_API> response) {
                        if(response.body().getResult().equals("success")) {
                            Intent intent = new Intent();
                            intent.setClassName("com.example.admin.jitsiman", "com.example.admin.jitsiman.MainActivity");
                            intent.putExtra("conference_key", conference_list_api.getConference_stream_key());
                            intent.putExtra("conference_number", conference_list_api.getConference_number());
                            mCtx.startActivity(intent);
                            GlobalApplication.getGlobalApplicationContext().progressOFF();
                        } else {
                            Toast.makeText(mCtx, "실패!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Conference_Room_Enter_API> call, Throwable t) {
                        Toast.makeText(mCtx, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }



    @Override
    public int getItemCount() {
        return conference_list.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ItemClickListener itemClickListener;
        private TextView conference_subject;
        private TextView conference_participants;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            conference_subject = itemView.findViewById(R.id.conference_subject);
            conference_participants = itemView.findViewById(R.id.conference_participant);
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
}
