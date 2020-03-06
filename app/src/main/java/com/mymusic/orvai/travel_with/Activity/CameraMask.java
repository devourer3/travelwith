package com.mymusic.orvai.travel_with.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;
import com.mymusic.orvai.travel_with.CameraDetector.CameraSourcePreview;
import com.mymusic.orvai.travel_with.CameraDetector.FaceTracker;
import com.mymusic.orvai.travel_with.CameraDetector.GraphicOverlay;
import com.mymusic.orvai.travel_with.R;
import com.mymusic.orvai.travel_with.model.MaskImage;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CameraMask extends AppCompatActivity {

    public Context mCtx;
    public Activity activity;
    private static final String TAG = "GB";
    private static final String CAPTURE_PATH = "/GLORIA";
    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    @BindView(R.id.ironMan)
    ImageView ironMan;
    @BindView(R.id.blackPanther)
    ImageView blackPanther;
    @BindView(R.id.hulk)
    ImageView hulk;
    @BindView(R.id.vendetta)
    ImageView vendetta;
    @BindView(R.id.jigSaw)
    ImageView jigSaw;
    @BindView(R.id.jason)
    ImageView jason;
    @BindView(R.id.glasses1)
    ImageView glasses1;
    @BindView(R.id.glasses2)
    ImageView glasses2;
    @BindView(R.id.maskTray)
    LinearLayout maskTray;
    @BindView(R.id.maskScroll)
    HorizontalScrollView maskScroll;
    @BindView(R.id.graphicOverlay)
    GraphicOverlay graphicOverlay;
    @BindView(R.id.cameraSource)
    CameraSourcePreview cameraSource;

    public Detector.Processor<Face> processor;
    public Tracker<Face> tracker;
    public MultiProcessor.Factory<Face> factory;
    @BindView(R.id.shutterBtn)
    Button shutterBtn;

    public CameraSource mCameraSource = null;
    public boolean mIsFrontFacing = true;

    public File pictureFile;
    public static String pictureURI = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_mask);
        ButterKnife.bind(this);
        activity = this;
        mCtx = getApplicationContext();

        if (savedInstanceState != null) {
            mIsFrontFacing = savedInstanceState.getBoolean("IsFrontFacing");
        }

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }

    }


    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(graphicOverlay, "카메라 퍼미션 거부",
                Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", listener)
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource(); //////////////////////////////////// 시작
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            Log.d(TAG, "Camera permission granted - initialize the camera source");
//            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage("카메라 퍼미션 거부")
                .setPositiveButton("OK", listener)
                .show();
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("IsFrontFacing", mIsFrontFacing);
    }


    //==============================================================================================
    // Camera Source
    //==============================================================================================

    /**
     * Creates the face detector and the camera.
     */

    private void createCameraSource() {
        Context context = getApplicationContext();
        FaceDetector detector = createFaceDetector(context);

        int facing = CameraSource.CAMERA_FACING_FRONT;
        if (!mIsFrontFacing) {
            facing = CameraSource.CAMERA_FACING_BACK;
        }

        // The camera source is initialized to use either the front or rear facing camera.  We use a
        // relatively low resolution for the camera preview, since this is sufficient for this app
        // and the face detector will run faster at lower camera resolutions.

        // 카메라 소스는 전면 또는 후면 카메라를 사용하도록 초기화됩니다.
        // 또한 카메라 미리보기에 상대적으로 낮은 해상도를 사용하며,
        // 충분히 빠른 얼굴 인식을 위하여 낮은 해상도를 사용합니다.

        //
        // However, note that there is a speed/accuracy trade-off with respect to choosing the
        // camera resolution.  The face detector will run faster with lower camera resolutions,
        // but may miss smaller faces, landmarks, or may not correctly detect eyes open/closed in
        // comparison to using higher camera resolutions.  If you have any of these issues, you may
        // want to increase the resolution.

        // 그러나 카메라 해상도 선택과 관련하여 속도/정확도를 조절할 수 있습니다.
        // 얼굴 인식은 해상도가 낮을수록 더 빨리 작동하지만,
        // 높은 해상도에 비해서, 작은 얼굴 또는 얼굴의 요소 및 눈의 깜빡임들을 인식하지 못할 수도 있습니다.
        // 따라서 이러한 문제가 생길 경우 해상도를 높이는 것이 좋습니다.

        mCameraSource = new CameraSource.Builder(context, detector)
                .setFacing(facing)
                .setRequestedPreviewSize(3840, 2160)
                .setRequestedFps(60.0f)
                .setAutoFocusEnabled(true)
                .build();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     * <p>
     * 카메라 소스가 있는 경우, 시작하거나 다시 시작합니다.
     * 카메라 소스가 아직 존재하지 않는 경우(예를 들어 카메라 소스가 생성하기 전에 onResume이 호출 되는 경우)
     * 다시 카메라 소스가 생성될 때 호출 될 것이다.
     */

    private void startCameraSource() {
        // check that the device has play services available.
        // 기기의 GooglePlay 서비스가 작동되는지 확인하세요.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                cameraSource.start(mCameraSource, graphicOverlay);
                cameraSource.start(mCameraSource);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }


    //==============================================================================================
    // Detector (IMPORTANT!!!)
    //==============================================================================================

    /**
     * Creates the face detector and associated processing pipeline to support either front facing
     * mode or rear facing mode.  Checks if the detector is ready to use, and displays a low storage
     * warning if it was not possible to download the face library.
     * <p>
     * 전면 또는 후면 모드를 지원하는 얼굴 인식(탐지) 및 관련 파이프라인을 생성합니다.
     * 얼굴인식기를 사용할 준비가 되었는지 확인하고 얼굴 라이브러리를 다운로드 할 수 없는 경우 저장 용량 부족 경고를 표시합니다.
     */
    @NonNull
    private FaceDetector createFaceDetector(Context context) {
        // For both front facing and rear facing modes, the detector is initialized to do landmark
        // detection (to find the eyes), classification (to determine if the eyes are open), and
        // tracking.

        // 전면 및 후면 모드일 경우, 탐지기는 눈을 탐지하기위한 표식(Landmark)탐지 및
        // 눈이 열려있는지 확인하기 위한 추적을 수행하도록 초기화 됩니다.

        // Use of "fast mode" enables faster detection for frontward faces, at the expense of not
        // attempting to detect faces at more varied angles (e.g., faces in profile).  Therefore,
        // faces that are turned too far won't be detected under fast mode.

        // "fast mode"의 사용은 더 다양한 각도(얼굴의 측면)에서 얼굴을 검출하려고 시도하지 않고, 전방의 얼굴을
        // 빠르게 인식하려 합니다. 따라서, "fast mode"에선 돌린 얼굴을 인식하지 않습니다.


        // For front facing mode only, the detector will use the "prominent face only" setting,
        // which is optimized for tracking a single relatively large face.  This setting allows the
        // detector to take some shortcuts to make tracking faster, at the expense of not being able
        // to track multiple faces.

        // "정면의 대면 모드"(setProminentfaceonly) 를 사용하는 경우에는, 탐지기는 상대적으로 큰 하나의 얼굴을 추적하는 데 최적화 되며,
        // "눈에 잘 띄는 한 면만" 검출합니다. 그러나, 이러한 설정을 사용하면, 탐지기가 여러 개의 얼굴을 추적 할 수 없습니다.


        // Setting the minimum face size not only controls how large faces must be in order to be
        // detected, it also affects performance.  Since it takes longer to scan for smaller faces,
        // we increase the minimum face size for the rear facing mode a little bit in order to make
        // tracking faster (at the expense of missing smaller faces).  But this optimization is less
        // important for the front facing case, because when "prominent face only" is enabled, the
        // detector stops scanning for faces after it has found the first (large) face.

        // 최소 얼굴 크기(minumum face) 를 설정하면 감지 할 수있는 크기가 큰 얼굴을 제어 할뿐만 아니라 성능에도 영향을줍니다.
        // 더 작은 얼굴을 스캔하는 데 시간이 오래 걸리므로 추적 속도를 높이기 위해 후면을 향한 모드의 최소 얼굴 크기를 약간 늘립니다 (더 작은 얼굴을 잃어 버리는 대신에).
        // 그러나 "눈에 잘 띄는 면만" 이 활성화 된 경우 감지기는 첫 번째 (큰)면을 찾은 후면을 스캔하지 않기 때문에 정면 대면의 경우 덜 중요합니다.

        FaceDetector detector = new FaceDetector.Builder(context)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setTrackingEnabled(true)
                .setMode(FaceDetector.FAST_MODE)
                .setProminentFaceOnly(mIsFrontFacing)
//                .setMinFaceSize(mIsFrontFacing ? 0.35f : 0.15f)
                .build();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outWidth = 3840;
        options.outHeight = 2160;

        final Bitmap goggleImage = BitmapFactory.decodeResource(getResources(), R.drawable.defaultimage, options);
//            final Bitmap goggleImage = BitmapFactory.decodeResource(getResources(), R.drawable.ironman);
        if (mIsFrontFacing) {
            // For front facing mode, a single tracker instance is used with an associated focusing
            // processor.  This configuration allows the face detector to take some shortcuts to
            // speed up detection, in that it can quit after finding a single face and can assume
            // that the nextIrisPosition face position is usually relatively close to the last seen
            // face position.

            // 전면 대칭 모드의 경우 단일 추적기 인스턴스가 연관된 포커싱 프로세서와 함께 사용됩니다.
            // 이 구성은 얼굴 검출기가 단일 얼굴을 찾은 후에 종료 할 수 있고,
            // 다음 IrisPosition 얼굴 위치가 일반적으로 마지막으로 본 얼굴 위치에 비교적 가깝다고
            // 가정 할 수 있다는 점에서 탐지 속도를 높이기 위해 몇 가지 단축키를 사용할 수있게합니다.

            tracker = new FaceTracker(goggleImage, graphicOverlay);
            processor = new LargestFaceFocusingProcessor.Builder(detector, tracker).build();
        } else {
            // For rear facing mode, a factory is used to create per-face tracker instances.  A
            // tracker is created for each face and is maintained as long as the same face is
            // visible, enabling per-face state to be maintained over time.  This is used to store
            // the iris position and velocity for each face independently, simulating the motion of
            // the eyes of any number of faces over time.
            //
            // Both the front facing mode and the rear facing mode use the same tracker
            // implementation, avoiding the need for any additional code.  The only difference
            // between these cases is the choice of Processor: one that is specialized for tracking
            // a single face or one that can handle multiple faces.  Here, we use MultiProcessor,
            // which is a standard component of the mobile vision API for managing multiple items.

            // 후방 대면 모드의 경우, 팩토리가면별로 추적기 인스턴스를 만드는 데 사용됩니다.
            // 추적기는 각 얼굴에 대해 생성되고 동일한 얼굴이 보일 수있는 한 유지되어 시간별 얼굴마다 상태를 유지할 수 있습니다.
            // 이것은 각 얼굴에 대한 홍채 위치와 속도를 독립적으로 저장하는 데 사용되며,
            // 시간이 지남에 따라 여러 얼굴의 눈의 움직임을 시뮬레이션합니다.
            // 앞면 모드와 뒷면 모드 모두 동일한 추적기 구현을 사용하므로 추가 코드가 필요하지 않습니다.
            // 이러한 경우의 유일한 차이점은 프로세서의 선택입니다.
            // 프로세서는 단일면을 추적하는 데 특화된 도구이거나 여러면을 처리 할 수있는 도구입니다.
            // 여기서는 다중 항목을 관리하기위한 모바일 비전 API의 표준 구성 요소 인 MultiProcessor를 사용합니다.

            factory = new MultiProcessor.Factory<Face>() {
                @Override
                public Tracker<Face> create(Face face) {
                    return new FaceTracker(goggleImage, graphicOverlay);
                }
            };
            processor = new MultiProcessor.Builder<>(factory).build();
        }

        detector.setProcessor(processor);

        if (!detector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.

            // 참고 : 얼굴 API를 사용하는 앱이 기기에 처음 설치되면 GMS는 기기를 검색하기 위해 기기에 기본 라이브러리를 다운로드합니다.
            // 보통 앱이 처음 실행되기 전에 완료됩니다. 그러나 해당 다운로드가 아직 완료되지 않은 경우 위의 호출은 얼굴을 감지하지 못합니다.
            // isOperational () 메소드는 필요한 네이티브 라이브러리가 현재 사용 가능한지 확인하는 데 사용할 수 있습니다.
            // 장치에서 라이브러리 다운로드가 완료되면 감지기가 자동으로 작동합니다.

            Log.w(TAG, "Face detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, "스토리지가 부족합니다.", Toast.LENGTH_LONG).show();
                Log.w(TAG, "스토리지가 부족합니다.");
            }
        }
        return detector;
    }

    public void captureView(CameraSource View) {
        View.takePicture(null, bytes -> {
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap face = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            Matrix rotateMat = new Matrix();
            rotateMat.setScale(-1, 1);
            rotateMat.postRotate(90);
            Bitmap rotateBit = Bitmap.createBitmap(face, 0, 0, face.getWidth(), face.getHeight(), rotateMat, false);
            cameraSource.setDrawingCacheEnabled(true);
            Bitmap overlay = cameraSource.getDrawingCache();
            Bitmap result = mergeBitmaps(rotateBit, overlay);
            try {
            String strFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + CAPTURE_PATH;
            File folder = new File(strFolderPath);

            if (!folder.exists()) {
                folder.mkdirs();
            }
                pictureFile = File.createTempFile("IMG-" + System.currentTimeMillis(), ".png", folder);
                if (!pictureFile.exists())
                    pictureFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(pictureFile);
                result.compress(Bitmap.CompressFormat.PNG, 20, fos);
                fos.flush();
                fos.write(bytes);
                fos.close();
                pictureURI = pictureFile.toString();
                Intent intent = getIntent();
                intent.putExtra("editPicture", pictureURI);
                setResult(RESULT_OK, intent);
                finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }



    public Bitmap mergeBitmaps(Bitmap face, Bitmap overlay) {
        int width = face.getWidth();
        int height = face.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Rect faceRect = new Rect(0,0, width, height);
        Rect overlayRect = new Rect(0,0, overlay.getWidth(), overlay.getHeight());
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(face, faceRect, faceRect, null);
        canvas.drawBitmap(overlay, overlayRect, faceRect, null);
        return newBitmap;
    }


            @OnClick({R.id.ironMan, R.id.blackPanther, R.id.hulk, R.id.vendetta, R.id.jigSaw, R.id.jason, R.id.glasses1, R.id.glasses2, R.id.shutterBtn})
            public void onViewClicked(View view) {
                switch (view.getId()) {
                    case R.id.ironMan:
                        Bitmap ironman = BitmapFactory.decodeResource(getResources(), R.drawable.ironman);
                        MaskImage maskImage1 = new MaskImage(ironman, -50, 1); // 비트맵, 이미지의 y좌표, 이미지의 세로 사이즈 크기
                        EventBus.getDefault().post(maskImage1);
                        break;
                    case R.id.blackPanther:
                        Bitmap blackpanther = BitmapFactory.decodeResource(getResources(), R.drawable.blackpanther);
                        MaskImage maskImage2 = new MaskImage(blackpanther, -50, 1);
                        EventBus.getDefault().post(maskImage2);
                        break;
                    case R.id.hulk:
                        Bitmap hulk = BitmapFactory.decodeResource(getResources(), R.drawable.hulk);
                        MaskImage maskImage3 = new MaskImage(hulk, -50, -150);
                        EventBus.getDefault().post(maskImage3);
                        break;
                    case R.id.vendetta:
                        Bitmap vendetta = BitmapFactory.decodeResource(getResources(), R.drawable.vendetta);
                        MaskImage maskImage4 = new MaskImage(vendetta, 100, 1);
                        EventBus.getDefault().post(maskImage4);
                        break;
                    case R.id.jigSaw:
                        Bitmap jigsaw = BitmapFactory.decodeResource(getResources(), R.drawable.jigsaw);
                        MaskImage maskImage5 = new MaskImage(jigsaw, 100, -100);
                        EventBus.getDefault().post(maskImage5);
                        break;
                    case R.id.jason:
                        Bitmap jason = BitmapFactory.decodeResource(getResources(), R.drawable.jason);
                        MaskImage maskImage6 = new MaskImage(jason, -50, -100);
                        EventBus.getDefault().post(maskImage6);
                        break;
                    case R.id.glasses1:
                        Bitmap glasses = BitmapFactory.decodeResource(getResources(), R.drawable.glassess);
                        MaskImage maskImage7 = new MaskImage(glasses, 1, 1);
                        EventBus.getDefault().post(maskImage7);
                        break;
                    case R.id.glasses2:
                        Bitmap glasses2 = BitmapFactory.decodeResource(getResources(), R.drawable.sunglasses);
                        MaskImage maskImage8 = new MaskImage(glasses2, 1, 1);
                        EventBus.getDefault().post(maskImage8);
                        break;
                    case R.id.shutterBtn:
                        captureView(mCameraSource);
                }
            }
        }
