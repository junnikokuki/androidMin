package com.ubtechinc.alpha.mini.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @作者：liudongyang
 * @日期: 18/7/9 11:13
 * @描述:
 */
public class CarControlImageView extends android.support.v7.widget.AppCompatImageView {

    public CarControlImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CarControlImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                setAlpha(0.4f);
                break;
            case MotionEvent.ACTION_UP:
                setAlpha(1f);
                break;
        }
        return super.onTouchEvent(event);
    }
}
