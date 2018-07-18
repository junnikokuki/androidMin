package com.ubtechinc.alpha.mini.avatar;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ubtechinc.alpha.AlphaMessageOuterClass;
import com.ubtechinc.alpha.AvatarControl;
import com.ubtechinc.alpha.mini.avatar.robot.IAvatarRobot;
import com.ubtechinc.alpha.mini.avatar.rtc.IRtcManager;
import com.ubtechinc.alpha.mini.avatar.widget.HalfJoyStickView;
import com.ubtechinc.nets.http.ThrowableWrapper;

import static com.ubtechinc.alpha.AvatarControl.CommandType.ACTION;
import static com.ubtechinc.alpha.AvatarControl.CommandType.HEADER;
import static com.ubtechinc.alpha.AvatarControl.CommandType.WALK;
import static com.ubtechinc.alpha.mini.avatar.widget.JoyStickView2.BOTTOM;
import static com.ubtechinc.alpha.mini.avatar.widget.JoyStickView2.BOTTOM_LEFT;
import static com.ubtechinc.alpha.mini.avatar.widget.JoyStickView2.FRONT;
import static com.ubtechinc.alpha.mini.avatar.widget.JoyStickView2.FRONT_RIGHT;
import static com.ubtechinc.alpha.mini.avatar.widget.JoyStickView2.LEFT;
import static com.ubtechinc.alpha.mini.avatar.widget.JoyStickView2.LEFT_FRONT;
import static com.ubtechinc.alpha.mini.avatar.widget.JoyStickView2.RIGHT;
import static com.ubtechinc.alpha.mini.avatar.widget.JoyStickView2.RIGHT_BOTTOM;

/**
 * Created by ubt on 2017/9/21.
 */

public abstract class AvatarBaseFragment extends BaseAlexaFragememt {

    public static final String TAG = "AvatarBaseFragment";

    protected ViewGroup videoContainer;
    protected IRtcManager rtcManager;
    protected SurfaceView videoView;


    public void init(IRtcManager rtcManager) {
        this.rtcManager = rtcManager;
    }

    @Override
    public void initView() {
        Log.d(TAG, "initView");

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        start();
    }

    public void start(){
        if(getActivity() != null){
            videoContainer = (ViewGroup) getActivity().findViewById(R.id.surface_view_container);
            if (videoView == null) {
                videoView = rtcManager.createSurfaceView();
            }
            if (videoView != null) {
                if (videoView.getParent() == null) {
                    Log.d(TAG, "add view ");
                    videoContainer.addView(videoView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                }
                int rotation = 0;
                switch (getActivity().getResources().getConfiguration().orientation) {
                    case Configuration.ORIENTATION_PORTRAIT:
                        rotation = 0;
                        break;
                    case Configuration.ORIENTATION_LANDSCAPE:
                        rotation = 270;
                        break;
                    default:
                        break;
                }
                rtcManager.start(videoView, rotation);
            }
        }
    }

    public void restart(){
        if(getActivity() != null){
            videoContainer = (ViewGroup) getActivity().findViewById(R.id.surface_view_container);
            if (videoView == null) {
                videoView = rtcManager.createSurfaceView();
            }
            if (videoView != null) {
                if (videoView.getParent() == null) {
                    Log.d(TAG, "add view ");
                    videoContainer.addView(videoView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                }
                int rotation = 0;
                switch (getActivity().getResources().getConfiguration().orientation) {
                    case Configuration.ORIENTATION_PORTRAIT:
                        rotation = 0;
                        break;
                    case Configuration.ORIENTATION_LANDSCAPE:
                        rotation = 270;
                        break;
                    default:
                        break;
                }
                rtcManager.restart(videoView, rotation);
            }
        }
    }

    public void onAction(String action) {
        AvatarControl.ControlRequest.Builder builder = AvatarControl.ControlRequest.newBuilder();
        builder.setType(ACTION);
        builder.setActionName(action);
        ((AvatarActivity) getActivity()).sendActionMessage(builder);
    }

    public void onHeaderControl(int direction) {
        AvatarControl.ControlRequest.Builder builder = AvatarControl.ControlRequest.newBuilder();
        Log.i("0523", "onWalkControl: " + direction);
        builder.setType(HEADER);
        switch (direction) {
            case LEFT:
                builder.setDirection(AvatarControl.DIRECTION.LEFT);
                break;
            case LEFT_FRONT:
                builder.setDirection(AvatarControl.DIRECTION.UP_LEFT);
                break;
            case FRONT:
                builder.setDirection(AvatarControl.DIRECTION.UP);
                break;
            case FRONT_RIGHT:
                builder.setDirection(AvatarControl.DIRECTION.UP_RIGHT);
                break;
            case RIGHT:
                builder.setDirection(AvatarControl.DIRECTION.RIGHT);
                break;
            case RIGHT_BOTTOM:
                builder.setDirection(AvatarControl.DIRECTION.DOWN_RIGHT);
                break;
            case BOTTOM:
                builder.setDirection(AvatarControl.DIRECTION.DOWN);
                break;
            case BOTTOM_LEFT:
                builder.setDirection(AvatarControl.DIRECTION.DOWN_LEFT);
                break;
            default:
                builder.setDirection(AvatarControl.DIRECTION.STOP);
                break;
        }
        ((AvatarActivity) getActivity()).sendControlMessage(builder);
    }


    public void onWalkControl(int direction) {
        AvatarControl.ControlRequest.Builder builder = AvatarControl.ControlRequest.newBuilder();
        builder.setType(WALK);
        Log.i("0523", "onWalkControl: " + direction);
        switch (direction) {
            case HalfJoyStickView.FRONT:
                builder.setDirection(AvatarControl.DIRECTION.UP);
                break;
            case HalfJoyStickView.FRONT_RIGHT:
                builder.setDirection(AvatarControl.DIRECTION.UP_RIGHT);
                break;
            case HalfJoyStickView.LEFT:
                builder.setDirection(AvatarControl.DIRECTION.LEFT);
                break;
            case HalfJoyStickView.LEFT_FRONT:
                builder.setDirection(AvatarControl.DIRECTION.UP_LEFT);
                break;
            case HalfJoyStickView.RIGHT:
                builder.setDirection(AvatarControl.DIRECTION.RIGHT);
                break;
            default:
                builder.setDirection(AvatarControl.DIRECTION.STOP);
                break;
        }
        ((AvatarActivity) getActivity()).sendControlMessage(builder);
    }

    public void onScreenshot() {
        if (getActivity() != null) {
            ((AvatarActivity) getActivity()).onScreenshot();
        }
    }

    public void showToastMessage(String msg){
        ((AvatarActivity)getActivity()).showTopMessage(msg);
    }

    public abstract void onStandCommandReponse();

    public abstract View getHeaderControl();

    public abstract View getWalkControl();



    public void changeMicState(boolean enable) {
    }

    public abstract void onAudioVolumeIndication(int volume);


}
