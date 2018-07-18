package com.ubtechinc.alpha.mini.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ubtechinc.alpha.mini.avatar.R;


/**
 * @作者：liudongyang
 * @日期: 18/7/10 14:16
 * @描述:
 */
public class PlayerButton extends FrameLayout {

    private ProgressImageView ivProgress;

    private ImageView ivIconImg;

    public PlayerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public PlayerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.car_words_item_player_layout, this, true);
        findViews();
    }

    private void findViews() {
        ivProgress = findViewById(R.id.rp_progress);
        ivIconImg = findViewById(R.id.iv_player_button);
    }

    public void setCallback(ProgressImageView.AnimationListener animattionCallback) {
        ivProgress.setAnmiationListener(animattionCallback);
    }

    public void stopPlay() {
        ivIconImg.setVisibility(View.VISIBLE);
    }

    public void cancel(){
        ivIconImg.setVisibility(View.VISIBLE);
        ivProgress.cancelAnimation();
    }

    public void startPlay() {
        ivIconImg.setVisibility(View.GONE);
        ivProgress.startAnimation();
    }

    public void setMode(boolean isPoliceMode) {
        if (isPoliceMode){
            ivIconImg.setImageResource(R.drawable.ic_car_play_police);
        }else{
            ivIconImg.setImageResource(R.drawable.ic_car_play);
        }
        ivProgress.setMode(isPoliceMode);
    }


    public void setPlayTime(int time){
        ivProgress.setPlayTime(time);
    }

}
