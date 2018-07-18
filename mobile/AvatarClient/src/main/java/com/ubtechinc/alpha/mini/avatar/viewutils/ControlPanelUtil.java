package com.ubtechinc.alpha.mini.avatar.viewutils;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import com.ubtechinc.alpha.mini.avatar.AvatarActivity;
import com.ubtechinc.alpha.mini.avatar.AvatarControlListener;
import com.ubtechinc.alpha.mini.avatar.R;
import com.ubtechinc.alpha.mini.avatar.widget.AvatarActionModel;

import java.util.List;

/**
 * Created by ubt on 2017/9/1.
 */

public class ControlPanelUtil implements ActionPanelUtil.IActionExcuteListener{

    public static final String TAG = "ControlPanelUtil";

    private Activity activity;

    private JoyPanelUtil joyPanelUtil;
    private BottomLayUtil bottomLayUtil;

    private List<AvatarActionModel> favActionList;

    private AvatarControlListener avatarControlListener;

    private ActionPanelUtil mActionPanelUtil;

    public ControlPanelUtil(Activity activity, AvatarControlListener avatarControlListener, List<AvatarActionModel> favActionList) {
        this.activity = activity;
        this.avatarControlListener = avatarControlListener;
        this.favActionList = favActionList;
        init();
    }


    private void init() {
        mActionPanelUtil  = new ActionPanelUtil(activity, favActionList, avatarControlListener);
        mActionPanelUtil.setActionListener(this);

        joyPanelUtil = new JoyPanelUtil(activity, new JoyPanelUtil.JoyControlListener() {
            @Override
            public void onHeaderControl(int direction) {
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
                if (down) {
                    mActionPanelUtil.disableHeaderAction();
                    bottomLayUtil.disableStandBtn();
                } else {
                    mActionPanelUtil.enableAllAction();
                    bottomLayUtil.enableStandBtn();
                }
            }
        }, false);
        bottomLayUtil = new BottomLayUtil(activity, avatarControlListener);

        initListener();
    }


    private ImageView mImageView;
    private void initListener() {
        mImageView = activity.findViewById(R.id.iv_img);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AvatarActivity)activity).exit(false);
            }
        });
    }


    public void onAudioVolumeIndication(int volume) {
        bottomLayUtil.volumeIndication(volume);
    }


    public void enableMic(boolean enable) {
        bottomLayUtil.enableMicStatus(enable);
    }

    public View getHeadController() {
        return joyPanelUtil.getHeadControl();
    }

    public View getWalkController() {
        return joyPanelUtil.getWalkControl();
    }


    public void disableAllControlPanel(){
        joyPanelUtil.disablePanel();
        bottomLayUtil.disableBottomBtn();
        mActionPanelUtil.disableAllAction();
    }

    public void enableAllControlPanel(){
        joyPanelUtil.enablePanel();
        bottomLayUtil.enableBottomBtn();
        mActionPanelUtil.enableAllAction();
    }

    @Override
    public void onActionStart() {
        joyPanelUtil.disableWalkingPanel();
        bottomLayUtil.disableStandBtn();
    }

    @Override
    public void onActionStop() {
        joyPanelUtil.enableWalkingPanel();
        bottomLayUtil.enableStandBtn();
    }
}
