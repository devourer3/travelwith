package com.mymusic.orvai.travel_with.Controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mymusic.orvai.travel_with.Interface.ItemClickListener;
import com.mymusic.orvai.travel_with.R;
import com.mymusic.orvai.travel_with.model.Attraction_Detail_Images_Result_API;

import java.util.List;

public class Attraction_More_Images_Adapter extends RecyclerView.Adapter<Attraction_More_Images_Adapter.ViewHolder> {
    public Context mCtx;
    private List<Attraction_Detail_Images_Result_API.ADI_Item> adi_items;

    public Attraction_More_Images_Adapter(Context mCtx, List<Attraction_Detail_Images_Result_API.ADI_Item> adi_items) {
        this.mCtx = mCtx;
        this.adi_items = adi_items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attraction_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Attraction_Detail_Images_Result_API.ADI_Item att_images = adi_items.get(position);
        Glide.with(mCtx).load(att_images.getSmallimageurl()).into(holder.attraction_more_image);
        holder.setItemClickListener((view, position1, isLongClick) -> {
            if(!isLongClick){
                Toast.makeText(mCtx, "끼야야아아악!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return adi_items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ImageView attraction_more_image;
        private ItemClickListener itemClickListener;

        ViewHolder(View itemView) {
            super(itemView);
            attraction_more_image = itemView.findViewById(R.id.att_more_image);
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
