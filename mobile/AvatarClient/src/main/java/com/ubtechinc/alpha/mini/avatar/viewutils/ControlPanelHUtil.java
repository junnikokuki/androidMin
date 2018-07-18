package com.ubtechinc.alpha.mini.avatar.viewutils;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ubtechinc.alpha.AvatarControl;
import com.ubtechinc.alpha.mini.avatar.AvatarActivity;
import com.ubtechinc.alpha.mini.avatar.AvatarControlListener;
import com.ubtechinc.alpha.mini.avatar.R;
import com.ubtechinc.alpha.mini.avatar.adapter.AvatarActionAdapter;
import com.ubtechinc.alpha.mini.avatar.widget.AvatarActionModel;

import java.util.List;

/**
 * Created by ubt on 2017/9/21.
 */

public class ControlPanelHUtil implements View.OnClickListener {

    public static final String TAG = "ControlPanelHUtil";

    private Activity activity;

    private JoyPanelUtil joyPanelUtil;

    private ActionPanelHUtil panelHUtil;

    private AvatarControlListener avatarControlListener;

    private ImageView ivClose;
    private TextView  tvStand;
    private ImageView ivMic;
    private LinearLayout llMic;
    private TextView  tvCamera;
    private boolean onMicEnable;


    public ControlPanelHUtil(Activity activity, AvatarControlListener avatarControlListener) {
        this.activity = activity;
        this.avatarControlListener = avatarControlListener;
        init();
    }

    private void init() {
        panelHUtil = new ActionPanelHUtil(activity, avatarControlListener, this);

        joyPanelUtil = new JoyPanelUtil(activity, new JoyPanelUtil.JoyControlListener() {
            @Override
            public void onHeaderControl(int direction) {
                System.out.println("head control" + direction);
                if (avatarControlListener != null) {
                    avatarControlListener.onHeaderControl(direction);
                }
            }

            @Override
            public void onWalkControl(int direction) {
                if (avatarControlListener != null) {
                    avatarControlListener.onWalkControl(direction);
                }
            }

            @Override
            public void onHeaderTouch(boolean down) {

            }

            @Override
            public void onWalkTouch(boolean down) {
                if (down){
                    panelHUtil.disableActions();
                    tvStand.setAlpha(0.3f);
                    tvStand.setClickable(false);
                }else{
                    panelHUtil.enableActions();
                    tvStand.setAlpha(1f);
                    tvStand.setClickable(true);
                }
            }

        }, true);
        initListener();
    }

    private void initListener() {
        ivClose = activity.findViewById(R.id.iv_close);
        tvStand = activity.findViewById(R.id.tv_stand);
        ivMic   = activity.findViewById(R.id.iv_mic);
        llMic   = activity.findViewById(R.id.ll_mic);
        tvCamera= activity.findViewById(R.id.tv_camera);
        ivClose.setOnClickListener(this);
        tvStand.setOnClickListener(this);
        llMic.setOnClickListener(this);
        tvCamera.setOnClickListener(this);
    }


    public View getHeadController() {
        return joyPanelUtil.getHeadControl();
    }

    public View getWalkController() {
        return joyPanelUtil.getWalkControl();
    }


    public void disableAllControlPanel(){
        joyPanelUtil.disablePanel();
        tvStand.setAlpha(0.3f);
        tvStand.setClickable(false);
        llMic.setAlpha(0.3f);
        llMic.setClickable(false);
        tvCamera.setAlpha(0.3f);
        tvCamera.setClickable(false);
        panelHUtil.disableActions();
    }

    public void enableAllControlPanel(){
        joyPanelUtil.enableHPanel();
        tvStand.setAlpha(1f);
        tvStand.setClickable(true);
        llMic.setAlpha(1f);
        llMic.setClickable(true);
        tvCamera.setAlpha(1f);
        tvCamera.setClickable(true);
        panelHUtil.enableActions();
    }


    public void disablePartControl(){
        joyPanelUtil.disableWalkingPanel();
        tvStand.setAlpha(0.3f);
        tvStand.setClickable(false);
    }

    public void enablePartControl(){
        joyPanelUtil.enableHWalkingPanel();
        tvStand.setAlpha(1f);
        tvStand.setClickable(true);
    }

    public void setMicEnableDrawble(){
        ivMic.setImageLevel(1);
    }

    public void setMicDisbleDrawble(){
        ivMic.setImageLevel(0);
    }

    public void setMicState(boolean onMicEnable){
        this.onMicEnable = onMicEnable;
    }

    public void volumeIndication(final int totalVolume){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (onMicEnable) {
                    int level = totalVolume + 1;
                    ivMic.setImageLevel(level);
                } else {
                    ivMic.setImageLevel(0);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_close){
            ((AvatarActivity)activity).exit(false);
        }else if(v.getId() == R.id.tv_camera){
            avatarControlListener.doTakePhoto();
        }else if (v.getId() == R.id.ll_mic){
            avatarControlListener.doMicStatusChange();
        }else if(v.getId() == R.id.tv_stand){
            avatarControlListener.doStand();
        }
    }
}
