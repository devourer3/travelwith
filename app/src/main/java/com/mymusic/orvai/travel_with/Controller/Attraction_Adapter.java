package com.mymusic.orvai.travel_with.Controller;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mymusic.orvai.travel_with.Activity.Attraction_Detail;
import com.mymusic.orvai.travel_with.Interface.ItemClickListener;
import com.mymusic.orvai.travel_with.R;
import com.mymusic.orvai.travel_with.model.Attraction_Result_API;

import java.util.List;

public class Attraction_Adapter extends RecyclerView.Adapter<Attraction_Adapter.ViewHolder> {
    public Context mCtx;
    private List<Attraction_Result_API.Item> attraction_items;

    public Attraction_Adapter(Context mCtx, List<Attraction_Result_API.Item> attraction_items) {
        this.mCtx = mCtx;
        this.attraction_items = attraction_items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attraction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Attraction_Result_API.Item attraction_item = attraction_items.get(position);
        holder.attraction_desc.setText(attraction_item.getTitle());
        holder.attraction_address.setText(attraction_item.getAddr1());
        if (attraction_item.getFirstimage2() == null) {
            holder.attraction_image.setImageResource(R.drawable.attraction_item_no_image);
        } else {
            Glide.with(mCtx).load(attraction_item.getFirstimage2()).into(holder.attraction_image);
        }
        holder.setItemClickListener((view, position1, isLongClick) -> {
            if (!isLongClick) {
                String content_id, type_id, title, tel_number;
                content_id = attraction_item.getContentid();
                type_id = attraction_item.getContenttypeid();
                title = attraction_item.getTitle();
                tel_number = attraction_item.getTel();
                Intent intent = new Intent(mCtx, Attraction_Detail.class);
                intent.putExtra("content_id", content_id);
                intent.putExtra("title", title);
                intent.putExtra("type_id", type_id);
                intent.putExtra("tel_number", tel_number);
                mCtx.startActivity(intent);
            } else {
                Toast.makeText(mCtx, position1+"번째 관광지라오!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return attraction_items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ImageView attraction_image;
        private TextView attraction_desc, attraction_address;
        private ItemClickListener itemClickListener;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            attraction_image = itemView.findViewById(R.id.attraction_image);
            attraction_desc = itemView.findViewById(R.id.attraction_title);
            attraction_address = itemView.findViewById(R.id.attraction_address);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
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
