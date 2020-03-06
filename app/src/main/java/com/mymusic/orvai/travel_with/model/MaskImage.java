package com.mymusic.orvai.travel_with.model;

import android.graphics.Bitmap;

public class MaskImage {

    private Bitmap maskBitmap;
    private int adjustHeightCoordinate;
    private int adjustHeightSize;

    public MaskImage(Bitmap maskBitmap, int adjustHeightCoordinate, int adjustHeightSize) {
        this.maskBitmap = maskBitmap;
        this.adjustHeightCoordinate = adjustHeightCoordinate;
        this.adjustHeightSize = adjustHeightSize;
    }

    public int getAdjustHeightCoordinate() {
        return adjustHeightCoordinate;
    }

    public int getAdjustHeightSize() {
        return adjustHeightSize;
    }

    public Bitmap getMaskBitmap() {
        return maskBitmap;
    }

}
