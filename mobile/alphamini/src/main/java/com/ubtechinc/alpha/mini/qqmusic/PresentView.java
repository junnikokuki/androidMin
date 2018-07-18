package com.ubtechinc.alpha.mini.qqmusic;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationSet;
import android.widget.ImageView;

/**
 * @author htoall
 * @Description: 礼物View，包含动画出现和动画消失功能
 * @date 2018/4/21 下午2:18
 * @copyright TCL-MIE
 */
@SuppressLint("AppCompatCustomView")
public class PresentView extends ImageView {

    private static final String TAG = "PresentView";
    private boolean isShow;
    // 礼物出现的位置
    private int initX, initY;

    // 礼物消失的位置
    private int disappearX, disappearY;

    private AnimationSet animationSet;

    public PresentView(Context context) {
        super(context);
    }

    public PresentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PresentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean toShow() {
        Log.d(TAG, " toShow : ");
        setVisibility(View.VISIBLE);
        if(!isShow) {
            Log.e(TAG, " toShow -- isShow: " + isShow);
            isShow = true;
            ObjectAnimator objectAnimator0 = ObjectAnimator.ofFloat(this, "alpha", 0, 1);
            objectAnimator0.setDuration(500);
            ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(this, "translationY", -50, 0);
            objectAnimator1.setDuration(500);
            ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(this, "translationY", 0, -20, 0);
            objectAnimator2.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimator2.setDuration(1000);
            objectAnimator2.setStartDelay(500);
            ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(this, "rotation", 0,-30, 30, -30, 0);
            objectAnimator3.setDuration(500);
            objectAnimator3.setRepeatCount(1);
            objectAnimator3.setStartDelay(500);
            objectAnimator3.start();
            objectAnimator0.start();
            objectAnimator1.start();
            objectAnimator2.start();
            return true;
        }
        return false;
    }

    public void toHide() {
        Log.e(TAG, " toHide -- isShow: " + isShow);
        if(isShow) {
            isShow = false;
            ObjectAnimator objectAnimator0 = ObjectAnimator.ofFloat(this, "alpha", 1, 0);
            objectAnimator0.setDuration(500);
            ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(this, "translationY", 0, 500);
            objectAnimator1.setDuration(500);
            ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(this, "translationX", 0, 500);
            objectAnimator2.setDuration(500);
            objectAnimator0.start();
            objectAnimator1.start();
            objectAnimator2.start();
        }
    }

    public void setInitX(int initX) {
        this.initX = initX;
    }

    public void setInitY(int initY) {
        this.initY = initY;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.e(TAG, " onLayout initX : " + initX + " initY : " + initY + " width : " + getWidth() + " height : " + getHeight());
        super.onLayout(changed, initX, initY, initX + getWidth(), initY + getHeight());
    }

    public void setDisappearX(int disappearX) {
        this.disappearX = disappearX;
    }

    public void setDisappearY(int disappearY) {
        this.disappearY = disappearY;
    }


}
