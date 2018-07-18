package com.ubtechinc.alpha.mini.avatar.viewutils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubtechinc.alpha.mini.avatar.R;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class TipLayUtil {

    private static final Integer TYPE_POWER = 1;
    private static final Integer TYPE_TAKE_PHOTO = 2;
    private static final Integer TYPE_MIC_STATUS = 3;

//    private ViewStub tipsStub;

    private View tipsContainer;
    private TextView tipsText;
    private View     tipsIcon;
    private TextView tipsText2;
    private ImageView closeBtn;

    private Activity activity;

    private HashMap<Integer, OnCloseListener> onCloseListeners = new HashMap<>(3);

    private Timer animTimer;

    private Integer currentType = -1;

    private boolean powerClosed = true; //电量提示是否关闭或者未开始显示
    private int currentPowerValue = 0;
    private boolean isCurrentLowpower;
    private boolean isCurrentCharging;

    public TipLayUtil(Activity activity) {
        this.activity = activity;
        init();
    }

    private void init() {
//        tipsStub = (ViewStub) activity.findViewById(R.id.tips_stub);
    }

    private void initView() {
//        tipsContainer = tipsStub.inflate();
        tipsText = (TextView) activity.findViewById(R.id.tip_text);
        tipsIcon = activity.findViewById(R.id.tips_icon);
        tipsText2 = (TextView) activity.findViewById(R.id.tip_text_2);
        closeBtn = (ImageView) activity.findViewById(R.id.tip_close_btn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipsContainer.setVisibility(View.GONE);
                if (animTimer != null) {
                    animTimer.cancel();
                    animTimer = null;
                }
                OnCloseListener onCloseListener = onCloseListeners.get(currentType);
                if (onCloseListener != null) {
                    onCloseListener.onClose();
                    onCloseListeners.remove(currentType);
                    if (currentType == TYPE_POWER) {
                        powerClosed = true;
                    }
                    if (!powerClosed) {
                        currentType = TYPE_POWER;
                    } else {
                        currentType = -1;
                    }
                }


            }
        });
    }

    public void showPowerTips(int value,boolean isLowpower,boolean isCharging, OnCloseListener onCloseListener) {
        showPowerTips(value,isLowpower,isCharging);
        this.onCloseListeners.put(TYPE_POWER, onCloseListener);

    }


    private void showPowerTips(int value,boolean isLowpower,boolean isCharging) {
        currentPowerValue = value;
        isCurrentCharging = isCharging;
        isCurrentLowpower = isLowpower;
        if (currentType != -1 && currentType != TYPE_POWER) {
            powerClosed = false;
            return;
        }
        currentType = TYPE_POWER;
        powerClosed = false;
        String text2 = "";
        if(isCharging){
            text2 = activity.getString(R.string.home_battery_charge);
        }else if(isLowpower){
            text2 = "" + value + "%";
        }
        if (TextUtils.isEmpty(text2)) {
            if (tipsContainer != null) {
                tipsContainer.setVisibility(View.GONE);
            }
            return;
        }
        showTips(activity.getString(R.string.avatar_page_battery), text2);
        tipsContainer.setBackgroundResource(R.color.alexa_input_nomal_color);
        tipsContainer.setAlpha(0.9f);
        tipsText.setTextColor(activity.getResources().getColor(R.color.black));
        tipsText.setAlpha(0.2f);
        tipsContainer.setTranslationY(0);

    }

    public void showOfflineTips() {
        showTips(activity.getString(R.string.network_disconnect), null);
        tipsContainer.setBackgroundResource(R.color.alexa_text_color_red_bg);
        tipsIcon.setVisibility(View.VISIBLE);
        tipsText.setTextColor(activity.getResources().getColor(R.color.alexa_text_color_red));
        tipsText.setAlpha(1f);
    }

    public void showTakePhotoTips() {
        currentType = TYPE_TAKE_PHOTO;
        showTips(activity.getString(R.string.avatar_save_photo), null);
        tipsContainer.setBackgroundResource(R.color.alexa_bg_press_color);
        tipsText.setTextColor(activity.getResources().getColor(R.color.avatar_model_stop_btn_color));
        tipsText.setAlpha(1f);
        startMsgAnimal();
    }

    public void showMicTips() {
        currentType = TYPE_MIC_STATUS;
        showTips(activity.getString(R.string.avatar_robot_voice), null);
        tipsContainer.setBackgroundResource(R.color.alexa_bg_press_color);
        tipsText.setTextColor(activity.getResources().getColor(R.color.avatar_model_stop_btn_color));
        tipsText.setAlpha(1f);
        startMsgAnimal();
    }

    public void showTips(String text1, String text2) {
        if (tipsContainer == null) {
            initView();
        }
        tipsContainer.setVisibility(View.VISIBLE);
        tipsIcon.setVisibility(View.GONE);
        tipsText.setText(text1);
        if (!TextUtils.isEmpty(text2)) {
            tipsText2.setText(text2);
            tipsText2.setVisibility(View.VISIBLE);
        } else {
            tipsText2.setVisibility(View.INVISIBLE);
        }
    }

    public void close() {

    }

    private void startMsgAnimal() {
        tipsContainer.setVisibility(View.VISIBLE);
        final ObjectAnimator anim = ObjectAnimator.ofFloat(tipsContainer, "translationY", -tipsContainer.getHeight(), 0);
        anim.setDuration(1000);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (animTimer != null) {
                    animTimer.cancel();
                }
                animTimer = new Timer();
                animTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        closeMsgAnimal();
                    }
                }, 3000);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    private void closeMsgAnimal() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tipsContainer.setVisibility(View.VISIBLE);
                ObjectAnimator anim = ObjectAnimator.ofFloat(tipsContainer, "translationY", 0, -tipsContainer.getHeight());
                anim.setDuration(1000);
                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!powerClosed) {
                            currentType = -1;
                            showPowerTips(currentPowerValue,isCurrentLowpower,isCurrentLowpower);
                        } else {
                            currentType = -1;
                            tipsContainer.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                anim.start();
            }
        });

    }


    public interface OnCloseListener {
        public void onClose();
    }
}
