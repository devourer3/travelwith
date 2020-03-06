package com.mymusic.orvai.travel_with.Utils;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class Recycler_Streaming_room_deco extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) { // 각 아이템을 배치할 때 호출(아이템 마다)
        super.getItemOffsets(outRect, view, parent, state);

        outRect.set(20, 20, 20, 20);
        view.setBackgroundColor(0xFFf1f1f1);
        ViewCompat.setElevation(view, 15.0f);

    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) { // 아이템을 배치하기 전에 호출
        super.onDraw(c, parent, state);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) { // 아이템을 배치 다하고 호출
        super.onDrawOver(c, parent, state);
    }
}
