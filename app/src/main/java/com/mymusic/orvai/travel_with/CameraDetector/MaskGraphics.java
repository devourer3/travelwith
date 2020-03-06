package com.mymusic.orvai.travel_with.CameraDetector;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;
import com.mymusic.orvai.travel_with.model.MaskImage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MaskGraphics extends GraphicOverlay.Graphic {

    private static final String TAG = "GoggleGraphics";
    private volatile PointF mLeftPosition;
    private volatile PointF mRightPosition;
    private static int adjustHeightCoodinate = 10000;
    private static int adjustHeightSize = 10000;
    private Bitmap mGoggleBitmap = null;
    private float prevAngle = 0;
    private PointF prevLeftPos = new PointF();

    public MaskGraphics(Bitmap bitmap, GraphicOverlay overlay) {
        super(overlay);
        EventBus.getDefault().register(this);
        mGoggleBitmap = bitmap;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void bitMapTransit(MaskImage maskImage) {
        mGoggleBitmap = maskImage.getMaskBitmap();
        adjustHeightCoodinate = maskImage.getAdjustHeightCoordinate();
        adjustHeightSize = maskImage.getAdjustHeightSize();
    }

    @Override
    public void draw(Canvas canvas) {
        PointF detectLeftPosition = mLeftPosition; // FaceTracker로 검출된 왼쪽눈의 위치
        PointF detectRightPosition = mRightPosition; // FaceTracker로 검출된 오른쪽눈의 위치
        if ((detectLeftPosition == null) || (detectRightPosition == null) || (mGoggleBitmap == null)) {
            return;
        }

        PointF leftPosition = new PointF(translateX(detectLeftPosition.x), translateY(detectLeftPosition.y));
        PointF rightPosition = new PointF(translateX(detectRightPosition.x), translateY(detectRightPosition.y));

        // Use the inter-eye distance to set the size of the eyes.
        float distance = (float) Math.sqrt(Math.pow(rightPosition.x - leftPosition.x, 2) + Math.pow(rightPosition.y - leftPosition.y, 2)); // 눈 사이의 거리
        Log.d(TAG, "distance " + distance);
        int newWidth = (int)(distance * 2.5); // 비트맵 이미지 크기의 너비(눈사이의 거리 * 2.5)
        Bitmap bitmap = null;

        /////////////////// 비트맵의 크기조절
        int newHeight = (int)((mGoggleBitmap.getHeight() * (newWidth)) / (float) mGoggleBitmap.getWidth()) - adjustHeightSize; // 비트맵 이미지 크기의 높이(비트맵의 원래 높이 * 계산된 너비 / 비트맵의 원래 너비)
        bitmap = Bitmap.createScaledBitmap(mGoggleBitmap, newWidth, newHeight, true); // 비트맵 확대 및 축소 메소드
        Log.d(TAG, "Rendered Image Bitmap Size " + newWidth + " , " + newHeight);

        int left = bitmap.getWidth() / 3;

        float angle = getAngle(leftPosition, rightPosition);
        double angleInRadian = Math.atan2(leftPosition.y - rightPosition.y, rightPosition.x - leftPosition.x);

        Log.d(TAG, "angle -> " + String.valueOf(angle));

        if (Math.abs(angle - prevAngle) > 2.0f) {
            Matrix matrix = new Matrix();
            matrix.postRotate(-angle, leftPosition.x, leftPosition.y);
            canvas.setMatrix(matrix);
            left = (int) (left / Math.cos(angleInRadian));
            /////////////////// 비트맵의 위치 (+는 아래로, -는 위로)
            canvas.drawBitmap(bitmap, leftPosition.x - left, (leftPosition.y - bitmap.getHeight() / 2) + adjustHeightCoodinate, null);

            prevAngle = angle;
            prevLeftPos = leftPosition;
        } else {
            Matrix matrix = new Matrix();
            matrix.postRotate(-prevAngle, prevLeftPos.x, prevLeftPos.y);
            canvas.setMatrix(matrix);
            left = (int) (left / Math.cos(Math.toRadians(prevAngle)));
            ////////////////// 비트맵의 위치
            canvas.drawBitmap(bitmap, prevLeftPos.x - left, (prevLeftPos.y - bitmap.getHeight() / 2) + adjustHeightCoodinate, null);
        }
    }


    void updateEyes(PointF leftPosition, PointF rightPosition) {
        mLeftPosition = leftPosition;
        mRightPosition = rightPosition;
        postInvalidate();
    }

    private float getAngle(PointF leftPosition, PointF rightPosition ) {
        float angle = (float) Math.toDegrees(Math.atan2(leftPosition.y - rightPosition.y, rightPosition.x - leftPosition.x));

        if(angle < 0){
            angle += 360;
        }
        return angle;
    }

}
