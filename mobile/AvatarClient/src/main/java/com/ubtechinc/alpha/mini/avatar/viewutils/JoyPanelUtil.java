package com.ubtechinc.alpha.mini.avatar.viewutils;

import android.app.Activity;
import android.view.View;

import com.ubtechinc.alpha.mini.avatar.R;
import com.ubtechinc.alpha.mini.avatar.widget.HalfJoyStickView;
import com.ubtechinc.alpha.mini.avatar.widget.JoyStickView2;

public class JoyPanelUtil {

    public static final String TAG = "JoyPanelUtil";

    private Activity activity;

    private JoyStickView2 headerControlPanel;
    private HalfJoyStickView walkingControlPanel;

    private JoyControlListener joyControlListener;

    private boolean isLandScape  = false;

    public JoyPanelUtil(Activity activity,JoyControlListener joyControlListener, boolean isLandScape){
        this.activity = activity;
        this.isLandScape = isLandScape;
        this.joyControlListener = joyControlListener;
        init();
    }

    private void init(){
        headerControlPanel  = (JoyStickView2) activity.findViewById(R.id.control_panel);
        walkingControlPanel = (HalfJoyStickView) activity.findViewById(R.id.half_panel);
        headerControlPanel.setLandScape(isLandScape);
        walkingControlPanel.setLandSpcape(isLandScape);
        initListener();
    }

    public void disableWalkingPanel(){
        walkingControlPanel.setViewEnable(false);
        walkingControlPanel.setAlpha(0.3f);
    }

    public void enableWalkingPanel(){
        walkingControlPanel.setViewEnable(true);
        walkingControlPanel.setAlpha(1f);
    }

    public void disablePanel(){
        walkingControlPanel.setViewEnable(false);
        walkingControlPanel.setAlpha(0.3f);
        headerControlPanel.setViewEnable(false);
        headerControlPanel.setAlpha(0.3f);
    }


    public void enablePanel(){
        walkingControlPanel.setViewEnable(true);
        walkingControlPanel.setAlpha(1f);
        headerControlPanel.setViewEnable(true);
        headerControlPanel.setAlpha(1f);
    }


    public void enableHPanel(){
        walkingControlPanel.setViewEnable(true);
        walkingControlPanel.setAlpha(1f);
        headerControlPanel.setViewEnable(true);
        headerControlPanel.setAlpha(1f);
    }

    public void enableHWalkingPanel(){
        walkingControlPanel.setViewEnable(true);
        walkingControlPanel.setAlpha(1f);
    }

    private void initListener(){
        headerControlPanel.setOnJoystickMoveListener(new JoyStickView2.OnJoystickMoveListener(){

            @Override
            public void onValueChanged(int angle, int power, int direction) {
                if(joyControlListener != null){
                    joyControlListener.onHeaderControl(direction);
                }
            }
        }, 1000);
        walkingControlPanel.setOnJoystickMoveListener(new HalfJoyStickView.OnJoystickMoveListener() {
            @Override
            public void onValueChanged(int angle, int power, int direction) {
                if(joyControlListener != null){
                    joyControlListener.onWalkControl(direction);
                }
            }
        }, 1000);
        headerControlPanel.setOnJoystickTouchListener(new JoyStickView2.OnJoystickTouchListener() {
            @Override
            public void onDown() {
                if(joyControlListener != null){
                    joyControlListener.onHeaderTouch(true);
                }
            }

            @Override
            public void onUp() {
                if(joyControlListener != null){
                    joyControlListener.onHeaderTouch(false);
                }
            }
        });
        walkingControlPanel.setOnJoystickTouchListener(new HalfJoyStickView.OnJoystickTouchListener() {
            @Override
            public void onDown() {
                if(joyControlListener != null){
                    joyControlListener.onWalkTouch(true);
                }
            }

            @Override
            public void onUp() {
                if(joyControlListener != null){
                    joyControlListener.onWalkTouch(false);
                }
            }
        });
    }



    public View getHeadControl(){
        return headerControlPanel;
    }

    public View getWalkControl(){
        return walkingControlPanel;
    }

    public interface JoyControlListener{

        public void onHeaderControl(int direction);

        public void onWalkControl(int direction);

        public void onHeaderTouch(boolean down);

        public void onWalkTouch(boolean down);
    }
}
