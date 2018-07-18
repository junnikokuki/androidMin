package com.ubtechinc.alpha.mini.avatar.viewutils;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;

import com.ubtechinc.alpha.mini.avatar.R;

public class BottomLayUtil implements View.OnClickListener {

    private Activity activity;

    private OnBottomBtnClickListener listener;
    private View bottomLay;
    private boolean onMicEnable = false;
    private ImageView volumeIcon;
    private View takePhotoBtn;
    private View micBtn;
    private View stand;


    public BottomLayUtil(Activity activity, OnBottomBtnClickListener listener) {
        this.activity = activity;
        this.listener = listener;
        init();
    }

    private void init() {
        bottomLay = activity.findViewById(R.id.bottom_bar_rl);
        initView();
    }

    private void initView(){
        volumeIcon   = activity.findViewById(R.id.voice_ib);
        takePhotoBtn = activity.findViewById(R.id.ll_camera);
        micBtn =  activity.findViewById(R.id.ll_mic);
        stand  =  activity.findViewById(R.id.ll_stand);
        stand.setOnClickListener(this);
        micBtn.setOnClickListener(this);
        takePhotoBtn.setOnClickListener(this);
    }

    public void show(){
        bottomLay.setVisibility(View.VISIBLE);
    }

    public void hidden(){
        if(bottomLay != null){
            bottomLay.setVisibility(View.GONE);
            volumeIcon.setImageLevel(0);
        }
    }

    public void disableStandBtn(){
        stand.setAlpha(0.3f);
        stand.setClickable(false);
    }

    public void disableBottomBtn(){
        takePhotoBtn.setClickable(false);
        takePhotoBtn.setAlpha(0.3f);
        micBtn.setClickable(false);
        micBtn.setAlpha(0.3f);
        stand.setClickable(false);
        stand.setAlpha(0.3f);
    }

    public void enableBottomBtn(){
        takePhotoBtn.setClickable(true);
        takePhotoBtn.setAlpha(1f);
        micBtn.setClickable(true);
        micBtn.setAlpha(1f);
        stand.setClickable(true);
        stand.setAlpha(1f);
    }

    public void enableStandBtn(){
        stand.setAlpha(1f);
        stand.setClickable(true);
    }

    public void volumeIndication(final int totalVolume) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (onMicEnable) {
                    int level = totalVolume + 1;
                    volumeIcon.setImageLevel(level);
                } else {
                    volumeIcon.setImageLevel(0);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            if (R.id.ll_mic == view.getId()) {
                listener.doMicStatusChange();
            } else if (R.id.ll_camera == view.getId()) {
                listener.doTakePhoto();
            } else if (R.id.ll_stand == view.getId()){
                listener.doStand();
            }
        }
    }

    public void enableMicStatus(boolean enable){
        onMicEnable = enable;
        if(onMicEnable){
            volumeIcon.setImageLevel(1);
        }else{
            volumeIcon.setImageLevel(0);
        }
    }


    public interface OnBottomBtnClickListener {
        public void doStand();

        public void doTakePhoto();

        public void doMicStatusChange();
    }
}
