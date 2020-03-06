package com.mymusic.orvai.travel_with.CameraDetector;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.Log;

import com.mymusic.orvai.travel_with.model.MaskImage;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

public class FaceTracker extends Tracker<Face> {

    private static final String TAG = "GoggleFaceTracker";

    private GraphicOverlay mOverlay;
    private MaskGraphics mGoggleGraphic;

    // Record the previously seen proportions of the landmark locations relative to the bounding box
    // of the face.  These proportions can be used to approximate where the landmarks are within the
    // face bounding box if the eye landmark is missing in a future update.

    // 얼굴의 윤곽을 기준으로 이전에 본 랜드 마크 위치의 비율을 기록하십시오.
    // 이러한 비율은 추후 업데이트에서 눈의 표식이 누락 된 경우 얼굴의 윤곽 내에서 랜드 마크가있는 위치를 대략적으로 나타낼 때 사용할 수 있습니다.

    private Map<Integer, PointF> mPreviousProportions = new HashMap<>();

    private Bitmap mBitmap = null;


    //==============================================================================================
    // Methods
    //==============================================================================================

    public FaceTracker(Bitmap bitmap, GraphicOverlay overlay) {
        this.mOverlay = overlay;
        this.mBitmap = bitmap;
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void bitMapTransit(MaskImage maskImage) {
        mBitmap = maskImage.getMaskBitmap();
    }


    /**
     * Resets the underlying google eye graphic.
     * 기본 그래픽을 재설정 합니다.
     */
    @Override
    public void onNewItem(int id, Face face) {
        mGoggleGraphic = new MaskGraphics(mBitmap, mOverlay);
    }

    /**
     * Updates the positions and state of eyes to the underlying graphic, according to the most
     * recent face detection results.  The graphic will render the eyes and simulate the motion of
     * the iris based upon these changes over time.
     * 가장 최근의 얼굴 감지 결과에 따라 기본 그래픽에 대한 눈의 위치와 상태를 업데이트합니다.
     * 그래픽은 시간이 지남에 따라 이러한 변화를 기반으로 눈을 렌더링하고 홍채의 움직임을 시뮬레이션합니다.
     */
    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
        mOverlay.add(mGoggleGraphic);

        updatePreviousProportions(face);

        PointF leftPosition = getLandmarkPosition(face, Landmark.LEFT_EYE);
        Log.d(TAG, "left eye position -> " + leftPosition);
        PointF rightPosition = getLandmarkPosition(face, Landmark.RIGHT_EYE);
        Log.d(TAG, "right eye Position -> " + rightPosition);


        mGoggleGraphic.updateEyes(leftPosition, rightPosition);
    }

    /**
     * Hide the graphic when the corresponding face was not detected.  This can happen for
     * intermediate frames temporarily (e.g., if the face was momentarily blocked from
     * view).
     * 해당면이 감지되지 않을 때 그래픽을 숨 깁니다.
     * 일시적으로 중간 프레임에 발생할 수 있습니다 (예 : 얼굴이 잠시 보이지 않게 차단 된 경우).
     */
    @Override
    public void onMissing(FaceDetector.Detections<Face> detectionResults) {
        mOverlay.remove(mGoggleGraphic);
    }

    /**
     * Called when the face is assumed to be gone for good. Remove a specific graphic from the overlay.
     *
     * 얼굴을 인식하기 힘들 때 호출됩니다. 오버레이에서 해당 그래픽을 삭제합니다.
     */
    @Override
    public void onDone() {
        mOverlay.remove(mGoggleGraphic);
    }

    //==============================================================================================
    // Private
    //==============================================================================================

    private void updatePreviousProportions(Face face) {
        for (Landmark landmark : face.getLandmarks()) {
            PointF position = landmark.getPosition();
            float xProp = (position.x - face.getPosition().x) / face.getWidth();
            float yProp = (position.y - face.getPosition().y) / face.getHeight();
            mPreviousProportions.put(landmark.getType(), new PointF(xProp, yProp));
        }
    }

    /**
     * Finds a specific landmark position, or approximates the position based on past observations
     * if it is not present.
     *
     * 특정 랜드 마크 위치를 찾거나, 존재하지 않는 경우 과거 관측치를 기반으로 위치를 대략적으로 나타냅니다.
     */
    private PointF getLandmarkPosition(Face face, int landmarkId) {
        for (Landmark landmark : face.getLandmarks()) {
            if (landmark.getType() == landmarkId) {
                return landmark.getPosition();
            }
        }

        PointF prop = mPreviousProportions.get(landmarkId);
        if (prop == null) {
            return null;
        }

        float x = face.getPosition().x + (prop.x * face.getWidth());
        float y = face.getPosition().y + (prop.y * face.getHeight());
        return new PointF(x, y);
    }
}
