/*
 *  Copyright 2015 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package com.mymusic.orvai.travel_with.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

import com.mymusic.orvai.travel_with.R;
import com.mymusic.orvai.travel_with.WebRTC.AppRTCAudioManager;
import com.mymusic.orvai.travel_with.WebRTC.AppRTCClient;
import com.mymusic.orvai.travel_with.WebRTC.DirectRTCClient;
import com.mymusic.orvai.travel_with.WebRTC.FirebaseRTCClient;
import com.mymusic.orvai.travel_with.WebRTC.PeerConnectionClient;
import com.mymusic.orvai.travel_with.WebRTC.UnhandledExceptionHandler;
import com.mymusic.orvai.travel_with.WebRTC.WebSocketRTCClient;
import com.mymusic.orvai.travel_with.fragment.CallFragment;

import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.FileVideoCapturer;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon.ScalingType;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoFileRenderer;
import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * Activity for peer connection call setup, call waiting
 * and call view.
 */
public class CallActivity extends Activity implements AppRTCClient.SignalingEvents,
        PeerConnectionClient.PeerConnectionEvents,
        CallFragment.OnCallEvents {
    private static final String TAG = "CallRTCClient";

    /**
     * ConnectActivity 클래스로부터 넘어온 인텐트의 Key 값들
     */
    public static final String EXTRA_ROOM_ID = "ROOM_ID";
    public static final String EXTRA_URL_PARAMETERS = "URL_PARAMETERS";
    public static final String EXTRA_LOOPBACK = "LOOPBACK";
    public static final String EXTRA_VIDEO_CALL = "VIDEO_CALL";
    public static final String EXTRA_SCREEN_CAPTURE = "SCREEN_CAPTURE";
    public static final String EXTRA_CAMERA2 = "CAMERA2";
    public static final String EXTRA_VIDEO_WIDTH = "VIDEO_WIDTH";
    public static final String EXTRA_VIDEO_HEIGHT = "VIDEO_HEIGHT";
    public static final String EXTRA_VIDEO_FPS = "VIDEO_FPS";
    public static final String EXTRA_VIDEO_BITRATE = "VIDEO_BITRATE";
    public static final String EXTRA_VIDEO_CODEC = "VIDEO_CODEC";
    public static final String EXTRA_HW_CODEC_ENABLED = "HW_CODEC";
    public static final String EXTRA_CAPTURE_TO_TEXTURE_ENABLED = "CAPTURE_TO_TEXTURE";
    public static final String EXTRA_FLEX_FEC_ENABLED = "FLEX_FEC";
    public static final String EXTRA_AUDIO_BITRATE = "AUDIO_BITRATE";
    public static final String EXTRA_AUDIO_CODEC = "AUDIO_CODEC";
    public static final String EXTRA_NO_AUDIO_PROCESSING_ENABLED = "NO_AUDIO_PROCESSING";
    public static final String EXTRA_AEC_DUMP_ENABLED = "AEC_DUMP";
    public static final String EXTRA_OPENSLES_ENABLED = "OPENSLES";
    public static final String EXTRA_DISABLE_BUILT_IN_AEC = "DISABLE_BUILT_IN_AEC";
    public static final String EXTRA_DISABLE_BUILT_IN_AGC = "DISABLE_BUILT_IN_AGC";
    public static final String EXTRA_DISABLE_BUILT_IN_NS = "DISABLE_BUILT_IN_NS";
    public static final String EXTRA_DISABLE_WEBRTC_AGC_AND_HPF = "DISABLE_WEBRTC_GAIN_CONTROL";
    public static final String EXTRA_TRACING = "TRACING";
    public static final String EXTRA_CMDLINE = "CMDLINE";
    public static final String EXTRA_RUNTIME = "RUNTIME";
    public static final String EXTRA_VIDEO_FILE_AS_CAMERA = "VIDEO_FILE_AS_CAMERA";
    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE = "SAVE_REMOTE_VIDEO_TO_FILE";
    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH = "SAVE_REMOTE_VIDEO_TO_FILE_WIDTH";
    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT = "SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT";
    public static final String EXTRA_USE_VALUES_FROM_INTENT = "USE_VALUES_FROM_INTENT";
    public static final String EXTRA_DATA_CHANNEL_ENABLED = "DATA_CHANNEL_ENABLED";
    public static final String EXTRA_ORDERED = "ORDERED";
    public static final String EXTRA_MAX_RETRANSMITS_MS = "MAX_RETRANSMITS_MS";
    public static final String EXTRA_MAX_RETRANSMITS = "MAX_RETRANSMITS";
    public static final String EXTRA_PROTOCOL = "PROTOCOL";
    public static final String EXTRA_NEGOTIATED = "NEGOTIATED";
    public static final String EXTRA_ID = "ID";
    public static final String EXTRA_USE_LEGACY_AUDIO_DEVICE = "USE_LEGACY_AUDIO_DEVICE";

    private static final int CAPTURE_PERMISSION_REQUEST_CODE = 1;

    // List of mandatory application permissions.
    private static final String[] MANDATORY_PERMISSIONS = {"android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.RECORD_AUDIO", "android.permission.INTERNET"};

    // Peer connection statistics callback period in ms.

    private static class ProxyVideoSink implements VideoSink {
        private VideoSink target;

        @Override
        synchronized public void onFrame(VideoFrame frame) {
            if (target == null) {
                Logging.d(TAG, "Dropping frame in proxy because target is null.");
                return;
            }

            target.onFrame(frame);
        }

        synchronized public void setTarget(VideoSink target) {
            this.target = target;
        }
    }

    private final ProxyVideoSink remoteProxyRenderer = new ProxyVideoSink();
    private final ProxyVideoSink localProxyVideoSink = new ProxyVideoSink();
    @Nullable
    private PeerConnectionClient peerConnectionClient = null;
    @Nullable
    private AppRTCClient appRtcClient;
    @Nullable
    private AppRTCClient.SignalingParameters signalingParameters;
    @Nullable
    private AppRTCAudioManager audioManager = null; // 오디오 매니저
    @Nullable
    private SurfaceViewRenderer pipRenderer; // 상대방 화면 서피스뷰
    @Nullable
    private SurfaceViewRenderer fullscreenRenderer; // 내 화면 서피스뷰
    @Nullable
    private VideoFileRenderer videoFileRenderer;
    private final List<VideoSink> remoteSinks = new ArrayList<>();
    private Toast logToast;
    private boolean commandLineRun;
    private boolean activityRunning;
    private AppRTCClient.RoomConnectionParameters roomConnectionParameters;
    @Nullable
    private PeerConnectionClient.PeerConnectionParameters peerConnectionParameters;
    private boolean iceConnected;
    private boolean isError;
    private boolean callControlFragmentVisible = true;
    private long callStartedTimeMs = 0;
    private boolean micEnabled = true;
    private boolean screencaptureEnabled = false;
    private static Intent mediaProjectionPermissionResultData;
    private static int mediaProjectionPermissionResultCode;
    // True if local view is in the fullscreen renderer.
    private boolean isSwappedFeeds;

    // Controls
    private CallFragment callFragment;

    @Override
    // TODO(bugs.webrtc.org/8580): LayoutParams.FLAG_TURN_SCREEN_ON and
    // LayoutParams.FLAG_SHOW_WHEN_LOCKED are deprecated.
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new UnhandledExceptionHandler(this));

        // Set window styles for fullscreen-window size. Needs to be done before
        // adding content.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_KEEP_SCREEN_ON
                | LayoutParams.FLAG_SHOW_WHEN_LOCKED | LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(getSystemUiVisibility());
        setContentView(R.layout.activity_call);

        iceConnected = false; // 상대방과 연결이 될 경우 true로 바뀜
        signalingParameters = null;

        // Create UI controls.
        pipRenderer = findViewById(R.id.pip_video_view); // 영상통화가 연결이 되었을 때 상대방의 카메라 화면
        fullscreenRenderer = findViewById(R.id.fullscreen_video_view); // 자신의 스마트폰 카메라화면
        callFragment = new CallFragment(); // 클릭했을 때 뜨는 메뉴 프래그먼트

        // Show/hide call control fragment on view click.
        // 그냥 화면을 화면을 클릭했을 때, callFragment가 띄워지거나(Show) 다시 누르면 사라짐(Hide)
        View.OnClickListener listener = view -> toggleCallControlFragmentVisibility();

        // Swap feeds on pip view click.
        // 상대방의 화면을 클릭했을 때, 나의 카메라화면과 상대방의 카메라 화면위치 바뀜(fullscreenRenderer <--> pipRenderer)
        pipRenderer.setOnClickListener(view -> setSwappedFeeds(!isSwappedFeeds));
        fullscreenRenderer.setOnClickListener(listener);
        remoteSinks.add(remoteProxyRenderer);

        final Intent intent = getIntent();
        final EglBase eglBase = EglBase.create();

        // Create video renderers.
        pipRenderer.init(eglBase.getEglBaseContext(), null); // 상대방 카메라 화면 생성
        pipRenderer.setScalingType(ScalingType.SCALE_ASPECT_FIT); // 상대방 화면 크기조절
        String saveRemoteVideoToFile = intent.getStringExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE); // 통화를 저장여부 인텐트

        // When saveRemoteVideoToFile is set we save the video from the remote to a file.
        // 통화내역을 저장하겠다고 했을 때.
        if (saveRemoteVideoToFile != null) {
            int videoOutWidth = intent.getIntExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, 0);
            int videoOutHeight = intent.getIntExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, 0);
            try {
                videoFileRenderer = new VideoFileRenderer(
                        saveRemoteVideoToFile, videoOutWidth, videoOutHeight, eglBase.getEglBaseContext());
                remoteSinks.add(videoFileRenderer);
            } catch (IOException e) {
                throw new RuntimeException(
                        "Failed to open video file for output: " + saveRemoteVideoToFile, e);
            }
        }
        fullscreenRenderer.init(eglBase.getEglBaseContext(), null); // 내 화면 설정
        fullscreenRenderer.setScalingType(ScalingType.SCALE_ASPECT_FILL); // 내 화면 크기조절
        pipRenderer.setZOrderMediaOverlay(true);
        pipRenderer.setEnableHardwareScaler(true /* enabled */);
        fullscreenRenderer.setEnableHardwareScaler(false /* enabled */);
        // Start with local feed in fullscreen and swap it to the pip when the call is connected.
        // 상대방과 연결 되었을 때 내 화면의 전체화면과 상대방의 화면이 스왑 됨
        setSwappedFeeds(true /* isSwappedFeeds */);

        // Check for mandatory permissions.
        // 퍼미션 확인
        for (String permission : MANDATORY_PERMISSIONS) {
            if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                logAndToast("퍼미션: " + permission + " 이 허용되지 않았습니다. 확인해 주세요.");
                setResult(RESULT_CANCELED);
                finish();
                return;
            }
        }

        // rooMUri는 https://appr.tc을 의미함.
        Uri roomUri = intent.getData();
        if (roomUri == null) {
            logAndToast(getString(R.string.missing_url));
            Log.e(TAG, "인텐트로부터 전달받은 URL은 잘못되었습니다.");
            setResult(RESULT_CANCELED); // 취소 결과를 보냄
            finish();
            return;
        }

        // Get Intent parameters.
        // 인텐트로부터 전달받은 영상통화 방 번호
        String roomId = intent.getStringExtra(EXTRA_ROOM_ID);
        Log.d(TAG, "Room ID: " + roomId);
        if (roomId == null || roomId.length() == 0) { // 만약 전달받은 URL이 잘못된 주소라면, 오류메시지를
            logAndToast(getString(R.string.missing_url));
            Log.e(TAG, "Incorrect room ID in intent!");
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        boolean loopback = intent.getBooleanExtra(EXTRA_LOOPBACK, false); // 루프백 통화일 때
        boolean tracing = intent.getBooleanExtra(EXTRA_TRACING, false); // 디버그 모드일 때

        int videoWidth = intent.getIntExtra(EXTRA_VIDEO_WIDTH, 0); // 가로
        int videoHeight = intent.getIntExtra(EXTRA_VIDEO_HEIGHT, 0); // 세로

        screencaptureEnabled = intent.getBooleanExtra(EXTRA_SCREEN_CAPTURE, false); // 스크린 캡쳐 기능(Screen-Sharing 기능, 카메라 말고 내 스마트폰 화면 보여주기). 기본은 false
        // If capturing format is not specified for screencapture, use screen resolution.
        if (screencaptureEnabled && videoWidth == 0 && videoHeight == 0) {
            DisplayMetrics displayMetrics = getDisplayMetrics();
            videoWidth = displayMetrics.widthPixels;
            videoHeight = displayMetrics.heightPixels;
        }
        PeerConnectionClient.DataChannelParameters dataChannelParameters = null;
        if (intent.getBooleanExtra(EXTRA_DATA_CHANNEL_ENABLED, false)) { // 데이터 채널 사용 여부
            dataChannelParameters = new PeerConnectionClient.DataChannelParameters( // 데이터 채널 파라미터
                    intent.getBooleanExtra(EXTRA_ORDERED, true),
                    intent.getIntExtra(EXTRA_MAX_RETRANSMITS_MS, -1),
                    intent.getIntExtra(EXTRA_MAX_RETRANSMITS, -1),
                    intent.getStringExtra(EXTRA_PROTOCOL),
                    intent.getBooleanExtra(EXTRA_NEGOTIATED, false),
                    intent.getIntExtra(EXTRA_ID, -1));
        }


        // PeerConnectionClient 생성하기 위해 peerConnection 파라미터들을 생성
        // 다음은 WebRTC Settings에 있는 설정들을 기반으로 받은 인텐트 값들을 PeerConnectionClient로 보내어, 영상통화를 하기 위함
        peerConnectionParameters = new PeerConnectionClient.PeerConnectionParameters(
                intent.getBooleanExtra(EXTRA_VIDEO_CALL, true), // 1. VIDEOCALL 여부
                loopback, //2. 루프백 통화 여부
                tracing, // 3. tracing(세팅-디버그)여부
                videoWidth, // 4. 가로크기
                videoHeight, // 5. 세로크기
                intent.getIntExtra(EXTRA_VIDEO_FPS, 0), // 6. fps
                intent.getIntExtra(EXTRA_VIDEO_BITRATE, 0), // 7. 비디오 최대 bitrate
                intent.getStringExtra(EXTRA_VIDEO_CODEC), // 8. 비디오코덱 종류(V8, V9, H.264(baseline/High)
                intent.getBooleanExtra(EXTRA_HW_CODEC_ENABLED, true), // 9. 비디오 하드웨어 코덱 가속 여부
                intent.getBooleanExtra(EXTRA_FLEX_FEC_ENABLED, false), // 10. Flexible FEC 스트림 여부(비디오 스트림 패킷 전송방법)
                intent.getIntExtra(EXTRA_AUDIO_BITRATE, 0), // 11. 오디오 비트레이트
                intent.getStringExtra(EXTRA_AUDIO_CODEC), // 12. 오디오코덱
                intent.getBooleanExtra(EXTRA_NO_AUDIO_PROCESSING_ENABLED, false), // 13. 오디오프로세싱(오디오 신호 변경)
                intent.getBooleanExtra(EXTRA_AEC_DUMP_ENABLED, false), // 14. 오디오 디버깅을 위한 aecdump파일 생성여부
                intent.getBooleanExtra(EXTRA_OPENSLES_ENABLED, false), // 15. 소리 음질 향상(2D, 3D)을 위한 OpenSLES의 사용여부
                intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_AEC, false), // 16. AEC(Agnostic echo cancelation, 음성의 반향 제거) 사용여부
                intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_AGC, false), // 17. AGC(Automatic gain Control) 음량 자동 조정 사용여부
                intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_NS, false), // 18. NS(Noise Suppression, 소음 제거) 사용 여부
                intent.getBooleanExtra(EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, false), // 19. AGC, HPF(High-Pass Filter, 저음 차단 필터)의 사용 여부
                intent.getBooleanExtra(EXTRA_USE_LEGACY_AUDIO_DEVICE, false), // 20. 레거시 오디오 디바이스 사용여부
                dataChannelParameters); // 21. 데이터 채널 파라미터들(313 라인)

        commandLineRun = intent.getBooleanExtra(EXTRA_CMDLINE, false);
        int runTimeMs = intent.getIntExtra(EXTRA_RUNTIME, 0);

        Log.d(TAG, "VIDEO_FILE: '" + intent.getStringExtra(EXTRA_VIDEO_FILE_AS_CAMERA) + "'");


        // todo ☆★☆★ 여기가 제일 가장 중요한 부분 ☆★☆★
        // Create connection client. Use DirectRTCClient if room name is an IP otherwise use the standard WebSocketRTCClient.
        // 소켓 연결 클라이언트를 만듭니다. 방 이름이 IP 인 경우(그냥 상대방 IP면) DirectRTCClient를 사용하고 그렇지 않으면(시그널 서버를 사용하면) 표준 WebSocketRTCClient를 사용.
        // 방 이름이 firebase 인 경우, FirebaseRTCClient를 사용.


        appRtcClient = new FirebaseRTCClient(this);

//        if("firebase".equals(roomId)) { // 방 이름이 firebase 인 경우
//            appRtcClient = new FirebaseRTCClient(this);
//        } else if (loopback || !DirectRTCClient.IP_PATTERN.matcher(roomId).matches()) { // 루프백 또는 방 이름이 IP가 아닌경우
//            appRtcClient = new WebSocketRTCClient(this); // appRtcClient는 WebSocketRtcClient + WebSocketChannelClient을 사용하며
//        } else { // IP 패턴을 가진다면
//            Log.i(TAG, "Using DirectRTCClient because room name looks like an IP.");
//            appRtcClient = new DirectRTCClient(this); // appRtcClient는 TCPChannelClient + DirectRTCClient를 통해 만든다.
//        }


        // Create connection parameters.
        // 방 ID, 루프백 여부, RoomConnectionParameters(https://appr.tc / 방 번호 / 루프백 여부 / urlParameters(안쓰이는듯)를 만듦.
        String urlParameters = intent.getStringExtra(EXTRA_URL_PARAMETERS);
        roomConnectionParameters = new AppRTCClient.RoomConnectionParameters(roomUri.toString(), roomId, loopback, urlParameters);

        // Send intent arguments to fragments.
        // 프래그먼트로부터 받은 데이터를 bundle을 통해 받음
        callFragment.setArguments(intent.getExtras());

        // Activate call and HUD fragments and start the call.
        // callFragment(기본O)를 활성화 시킴
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.call_fragment_container, callFragment);
        ft.commit();

        // For command line execution run connection for <runTimeMs> and exit.
        // runTimeMs 이 0보다 큰 경우, 연결을 중단시킴
        if (commandLineRun && runTimeMs > 0) {
            (new Handler()).postDelayed(this::disconnect, runTimeMs);
        }

        // Create peer connection client.
        // 여태까지 작성한 모든 매개변수들(peerConnectionParameters, 및 CallActivity에서 발생하도록 함)을 PeerConnectionClient로 만듦.
        peerConnectionClient = new PeerConnectionClient(getApplicationContext(), eglBase, peerConnectionParameters, CallActivity.this);

        // peerConnectionClient를 매개변수로 options를 추가하여, peerConnectionFactory를 만듦
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        if (loopback) {
            options.networkIgnoreMask = 0;
        }
        peerConnectionClient.createPeerConnectionFactory(options);

        // screencapture 기능을 사용하지 않으면 screenResolution을 그냥 씀
        if (screencaptureEnabled) {
            startScreenCapture();
        } else {
            startCall(); // 통화연결 실시
        }
    }


    /**
     * 여기서부턴 주요 메소드들
     *
     * @return
     */

    // 단말기의 해상도 크기를 가져옴
    @TargetApi(17)
    private DisplayMetrics getDisplayMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics;
    }

    // 시스템 UI 비지블/인비지블 여부
    @TargetApi(19)
    private static int getSystemUiVisibility() {
        int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        return flags;
    }

    @TargetApi(21)
    private void startScreenCapture() {
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), CAPTURE_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != CAPTURE_PERMISSION_REQUEST_CODE)
            return;
        mediaProjectionPermissionResultCode = resultCode;
        mediaProjectionPermissionResultData = data;
        startCall();
    }

    private boolean useCamera2() {
        return Camera2Enumerator.isSupported(this) && getIntent().getBooleanExtra(EXTRA_CAMERA2, true);
    }

    private boolean captureToTexture() {
        return getIntent().getBooleanExtra(EXTRA_CAPTURE_TO_TEXTURE_ENABLED, false);
    }

    private @Nullable
    VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        Logging.d(TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        Logging.d(TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    @TargetApi(21)
    private @Nullable
    VideoCapturer createScreenCapturer() {
        if (mediaProjectionPermissionResultCode != Activity.RESULT_OK) {
            reportError("User didn't give permission to capture the screen.");
            return null;
        }
        return new ScreenCapturerAndroid(
                mediaProjectionPermissionResultData, new MediaProjection.Callback() {
            @Override
            public void onStop() {
                reportError("User revoked permission to capture the screen.");
            }
        });
    }

    // 액티비티 인터페이스
    @Override
    public void onStop() {
        super.onStop();
        activityRunning = false;
        // Don't stop the video when using screencapture to allow user to show other apps to the remote end.
        if (peerConnectionClient != null && !screencaptureEnabled) {
            peerConnectionClient.stopVideoSource();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        activityRunning = true;
        // Video is not paused for screencapture. See onPause.
        if (peerConnectionClient != null && !screencaptureEnabled) {
            peerConnectionClient.startVideoSource();
        }
    }

    @Override
    protected void onDestroy() {
        Thread.setDefaultUncaughtExceptionHandler(null);
        disconnect();
        if (logToast != null) {
            logToast.cancel();
        }
        activityRunning = false;
        super.onDestroy();
    }

    // CallFragment.OnCallEvents interface implementation.
    // 메뉴(CallFragment)에 대한 인터페이스
    @Override
    public void onCallHangUp() { // 전화 끊기
        disconnect();
    }

    @Override
    public void onCameraSwitch() { // 카메라 전면/후면 변경
        if (peerConnectionClient != null) {
            peerConnectionClient.switchCamera();
        }
    }

    @Override
    public void onVideoScalingSwitch(ScalingType scalingType) { // 화면 크기 조절
        fullscreenRenderer.setScalingType(scalingType);
    }

    @Override
    public boolean onToggleMic() { // 마이크 끄기
        if (peerConnectionClient != null) {
            micEnabled = !micEnabled;
            peerConnectionClient.setAudioEnabled(micEnabled);
        }
        return micEnabled;
    }

    // Helper functions.
    private void toggleCallControlFragmentVisibility() { // 화면을 눌렀을 때, 송출/수신 화면
        if (!iceConnected || !callFragment.isAdded()) {
            return;
        }
        // Show/hide call control fragment
        callControlFragmentVisible = !callControlFragmentVisible;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (callControlFragmentVisible) {
            ft.show(callFragment);
        } else {
            ft.hide(callFragment);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    private void startCall() { // 통화 시작 메소드(상대방 연결이 없는 경우)
        if (appRtcClient == null) {
            Log.e(TAG, "AppRTC client is not allocated for a call.");
            return;
        }
        callStartedTimeMs = System.currentTimeMillis();

        // Start room connection.
        // todo 여기가 매개변수(roomConnectionParameters)를 바탕으로 WebSocketRTCClient 에서 공통적으로 갖고있는 connectToRoom 메소드 호출
        logAndToast(getString(R.string.connecting_to, roomConnectionParameters.roomUrl));
        appRtcClient.connectToRoom(roomConnectionParameters);

        // Create and audio manager that will take care of audio routing, audio modes, audio device enumeration etc.
        // 오디오 라우팅, 오디오 모드, 오디오 장치 열거 등을 처리하는 오디오 관리자를 만듭니다.
        audioManager = AppRTCAudioManager.create(getApplicationContext());
        // Store existing audio settings and change audio mode to MODE_IN_COMMUNICATION for best possible VoIP performance.
        Log.d(TAG, "Starting the audio manager...");
        // This method will be called each time the number of available audio devices has changed.
        // 오디오 매니저 실행.
        audioManager.start(this::onAudioManagerDevicesChanged);
    }

    // 상대방이 연결했을 때. 이 메소드는 UI 스레드에서 실행해야 함.
    private void callConnected() {
        final long delta = System.currentTimeMillis() - callStartedTimeMs; // 지연시간
        Log.i(TAG, "Call connected: delay=" + delta + "ms");
        if (peerConnectionClient == null || isError) {
            Log.w(TAG, "Call is connected in closed or error state");
            return;
        }
        // 좌우반전
        setSwappedFeeds(false /* isSwappedFeeds */);
    }

    // This method is called when the audio manager reports audio device change,
    // e.g. from wired headset to speakerphone.
    private void onAudioManagerDevicesChanged(
            final AppRTCAudioManager.AudioDevice device, final Set<AppRTCAudioManager.AudioDevice> availableDevices) {
        Log.d(TAG, "onAudioManagerDevicesChanged: " + availableDevices + ", "
                + "selected: " + device);
        // TODO(henrika): add callback handler.
    }

    // Disconnect from remote resources, dispose of local resources, and exit.
    // 통화 종료 메소드
    private void disconnect() {
        activityRunning = false;
        remoteProxyRenderer.setTarget(null);
        localProxyVideoSink.setTarget(null);
        if (appRtcClient != null) {
            appRtcClient.disconnectFromRoom();
            appRtcClient = null;
        }
        if (pipRenderer != null) {
            pipRenderer.release();
            pipRenderer = null;
        }
        if (videoFileRenderer != null) {
            videoFileRenderer.release();
            videoFileRenderer = null;
        }
        if (fullscreenRenderer != null) {
            fullscreenRenderer.release();
            fullscreenRenderer = null;
        }
        if (peerConnectionClient != null) {
            peerConnectionClient.close();
            peerConnectionClient = null;
        }
        if (audioManager != null) {
            audioManager.stop();
            audioManager = null;
        }
        if (iceConnected && !isError) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    // 에러메시지와 함께 종료 다이얼로그 팝업
    private void disconnectWithErrorMessage(final String errorMessage) {
        if (commandLineRun || !activityRunning) {
            Log.e(TAG, "Critical error: " + errorMessage);
            disconnect();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getText(R.string.channel_error_title))
                    .setMessage(errorMessage)
                    .setCancelable(false)
                    .setNeutralButton(R.string.ok, (dialog, id) -> {
                        dialog.cancel();
                        disconnect();
                    })
                    .create()
                    .show();
        }
    }

    // Log |msg| and Toast about it.
    private void logAndToast(String msg) {
        Log.d(TAG, msg);
        if (logToast != null) {
            logToast.cancel();
        }
        logToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        logToast.show();
    }

    private void reportError(final String description) {
        runOnUiThread(() -> {
            if (!isError) {
                isError = true;
                disconnectWithErrorMessage(description);
            }
        });
    }

    // 세팅에서, 비디오 캡처를 했을 때 사용되는 메소드
    private @Nullable
    VideoCapturer createVideoCapturer() {
        final VideoCapturer videoCapturer;
        String videoFileAsCamera = getIntent().getStringExtra(EXTRA_VIDEO_FILE_AS_CAMERA);
        if (videoFileAsCamera != null) {
            try {
                videoCapturer = new FileVideoCapturer(videoFileAsCamera);
            } catch (IOException e) {
                reportError("Failed to open video file for emulated camera");
                return null;
            }
        } else if (screencaptureEnabled) {
            return createScreenCapturer();
        } else if (useCamera2()) {
            if (!captureToTexture()) {
                reportError(getString(R.string.camera2_texture_only_error));
                return null;
            }

            Logging.d(TAG, "Creating capturer using camera2 API.");
            videoCapturer = createCameraCapturer(new Camera2Enumerator(this));
        } else {
            Logging.d(TAG, "Creating capturer using camera1 API.");
            videoCapturer = createCameraCapturer(new Camera1Enumerator(captureToTexture()));
        }
        if (videoCapturer == null) {
            reportError("Failed to open camera");
            return null;
        }
        return videoCapturer;
    }

    // 내 스마트폰 화면과 상대방의 위치 바꾸는 메소드
    private void setSwappedFeeds(boolean isSwappedFeeds) {
        Logging.d(TAG, "setSwappedFeeds: " + isSwappedFeeds);
        this.isSwappedFeeds = isSwappedFeeds;
        localProxyVideoSink.setTarget(isSwappedFeeds ? fullscreenRenderer : pipRenderer);
        remoteProxyRenderer.setTarget(isSwappedFeeds ? pipRenderer : fullscreenRenderer);
        fullscreenRenderer.setMirror(isSwappedFeeds);
        pipRenderer.setMirror(!isSwappedFeeds);
    }


    // AppRTCClient.AppRTCSignalingEvents 인터페이스에 대한 콜백 메소드. WebSocket에서 불러낸 시그널 스레드들로, UI 스레드에서 동작해야 함
    // -----Implementation of AppRTCClient.AppRTCSignalingEvents ---------------
    // All callbacks are invoked from websocket signaling looper thread and
    // are routed to UI thread.
    private void onConnectedToRoomInternal(final AppRTCClient.SignalingParameters params) { //
        final long delta = System.currentTimeMillis() - callStartedTimeMs;

        signalingParameters = params;
        logAndToast("Creating peer connection, delay=" + delta + "ms");
        VideoCapturer videoCapturer = null;
        if (peerConnectionParameters.videoCallEnabled) {
            videoCapturer = createVideoCapturer();
        }
        peerConnectionClient.createPeerConnection(
                localProxyVideoSink, remoteSinks, videoCapturer, signalingParameters);

        if (signalingParameters.initiator) {
            logAndToast("Creating OFFER...");
            // Create offer. Offer SDP will be sent to answering client in PeerConnectionEvents.onLocalDescription event.
            peerConnectionClient.createOffer();
        } else {
            if (params.offerSdp != null) {
                peerConnectionClient.setRemoteDescription(params.offerSdp);
                logAndToast("Creating ANSWER...");
                // Create answer. Answer SDP will be sent to offering client in PeerConnectionEvents.onLocalDescription event.
                peerConnectionClient.createAnswer();
            }
            if (params.iceCandidates != null) {
                // Add remote ICE candidates from room.
                for (IceCandidate iceCandidate : params.iceCandidates) {
                    peerConnectionClient.addRemoteIceCandidate(iceCandidate);
                }
            }
        }
    }

    @Override
    public void onConnectedToRoom(final AppRTCClient.SignalingParameters params) {
        runOnUiThread(() -> onConnectedToRoomInternal(params));
    }


    @Override
    public void onRemoteDescription(final SessionDescription sdp) {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(() -> {
            if (peerConnectionClient == null) {
                Log.e(TAG, "Received remote SDP for non-initilized peer connection.");
                return;
            }
            logAndToast("Received remote " + sdp.type + ", delay=" + delta + "ms");
            peerConnectionClient.setRemoteDescription(sdp);
            if (!signalingParameters.initiator) {
                logAndToast("Creating ANSWER...");
                // Create answer. Answer SDP will be sent to offering client in
                // PeerConnectionEvents.onLocalDescription event.
                peerConnectionClient.createAnswer();
            }
        });
    }

    @Override
    public void onRemoteIceCandidate(final IceCandidate candidate) {
        runOnUiThread(() -> {
            if (peerConnectionClient == null) {
                Log.e(TAG, "Received ICE candidate for a non-initialized peer connection.");
                return;
            }
            peerConnectionClient.addRemoteIceCandidate(candidate);
        });
    }

    @Override
    public void onRemoteIceCandidatesRemoved(final IceCandidate[] candidates) {
        runOnUiThread(() -> {
            if (peerConnectionClient == null) {
                Log.e(TAG, "Received ICE candidate removals for a non-initialized peer connection.");
                return;
            }
            peerConnectionClient.removeRemoteIceCandidates(candidates);
        });
    }

    @Override
    public void onChannelClose() {
        runOnUiThread(() -> {
            logAndToast("Remote end hung up; dropping PeerConnection");
            disconnect();
        });
    }

    @Override
    public void onChannelError(final String description) {
        reportError(description);
    }

    // -----Implementation of PeerConnectionClient.PeerConnectionEvents.---------
    // Send local peer connection SDP and ICE candidates to remote party.
    // All callbacks are invoked from peer connection client looper thread and
    // are routed to UI thread.
    @Override
    public void onLocalDescription(final SessionDescription sdp) {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(() -> {
            if (appRtcClient != null) {
                logAndToast("Sending " + sdp.type + ", delay=" + delta + "ms");
                if (signalingParameters.initiator) {
                    appRtcClient.sendOfferSdp(sdp);
                } else {
                    appRtcClient.sendAnswerSdp(sdp);
                }
            }
            if (peerConnectionParameters.videoMaxBitrate > 0) {
                Log.d(TAG, "Set video maximum bitrate: " + peerConnectionParameters.videoMaxBitrate);
                peerConnectionClient.setVideoMaxBitrate(peerConnectionParameters.videoMaxBitrate);
            }
        });
    }

    @Override
    public void onIceCandidate(final IceCandidate candidate) {
        runOnUiThread(() -> {
            if (appRtcClient != null) {
                appRtcClient.sendLocalIceCandidate(candidate);
            }
        });
    }

    @Override
    public void onIceCandidatesRemoved(final IceCandidate[] candidates) {
        runOnUiThread(() -> {
            if (appRtcClient != null) {
                appRtcClient.sendLocalIceCandidateRemovals(candidates);
            }
        });
    }

    @Override
    public void onIceConnected() { // Ice 서버 연결
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(() -> {
            logAndToast("ICE connected, delay=" + delta + "ms");
            iceConnected = true;
            callConnected();
        });
    }

    @Override
    public void onIceDisconnected() {
        runOnUiThread(() -> {
            logAndToast("ICE disconnected");
            iceConnected = false;
            disconnect();
        });
    }

    @Override
    public void onPeerConnectionClosed() {
    }

    @Override
    public void onPeerConnectionError(final String description) {
        reportError(description);
    }
}
