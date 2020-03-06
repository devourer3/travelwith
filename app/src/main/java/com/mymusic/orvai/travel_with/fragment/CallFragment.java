/*
 *  Copyright 2015 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package com.mymusic.orvai.travel_with.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mymusic.orvai.travel_with.R;
import com.mymusic.orvai.travel_with.Activity.CallActivity;

import org.webrtc.RendererCommon.ScalingType;

/**
 * Fragment for call control.
 * CallActivity 에 뜨는 전화끊기, 카메라 회전, 음소거, 화면크기 조절 등의 메뉴 창 화면
 */
public class CallFragment extends Fragment {
    private TextView contactView;
    private ImageButton cameraSwitchButton;
    private ImageButton videoScalingButton;
    private ImageButton toggleMuteButton;
    private OnCallEvents callEvents;
    private ScalingType scalingType;
    private boolean videoCallEnabled = true;

    /**
     * Call control interface for container activity.
     */
    public interface OnCallEvents {
        void onCallHangUp();

        void onCameraSwitch();

        void onVideoScalingSwitch(ScalingType scalingType);

        boolean onToggleMic();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View controlView = inflater.inflate(R.layout.fragment_call, container, false);

        // Create UI controls.
        contactView = controlView.findViewById(R.id.contact_name_call);
        ImageButton disconnectButton = controlView.findViewById(R.id.button_call_disconnect);
        cameraSwitchButton = controlView.findViewById(R.id.button_call_switch_camera);
        videoScalingButton = controlView.findViewById(R.id.button_call_scaling_mode);
        toggleMuteButton = controlView.findViewById(R.id.button_call_toggle_mic);

        // Add buttons click events.
        disconnectButton.setOnClickListener(view -> callEvents.onCallHangUp());

        cameraSwitchButton.setOnClickListener(view -> callEvents.onCameraSwitch());

        videoScalingButton.setOnClickListener(view -> {
            if (scalingType == ScalingType.SCALE_ASPECT_FILL) {
                videoScalingButton.setBackgroundResource(R.drawable.ic_action_full_screen);
                scalingType = ScalingType.SCALE_ASPECT_FIT;
            } else {
                videoScalingButton.setBackgroundResource(R.drawable.ic_action_return_from_full_screen);
                scalingType = ScalingType.SCALE_ASPECT_FILL;
            }
            callEvents.onVideoScalingSwitch(scalingType);
        });
        scalingType = ScalingType.SCALE_ASPECT_FILL;

        toggleMuteButton.setOnClickListener(view -> {
            boolean enabled = callEvents.onToggleMic();
            toggleMuteButton.setAlpha(enabled ? 1.0f : 0.3f);
        });

        return controlView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        if (args != null) {
            String contactName = args.getString(CallActivity.EXTRA_ROOM_ID);
//            contactView.setText(contactName); // 화면
            videoCallEnabled = args.getBoolean(CallActivity.EXTRA_VIDEO_CALL, true);
        }
        if (!videoCallEnabled) {
            cameraSwitchButton.setVisibility(View.INVISIBLE);
        }
    }

    // TODO(sakal): Replace with onAttach(Context) once we only support API level 23+.
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callEvents = (OnCallEvents) activity;
    }
}
