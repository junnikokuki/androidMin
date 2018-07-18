package com.ubtechinc.alpha.mini.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @作者：liudongyang
 * @日期: 18/5/3 16:25
 * @描述: 首页箭头
 */

public class HomePageArrowImageView extends android.support.v7.widget.AppCompatImageView {

    public HomePageArrowImageView(Context context) {
        super(context);
    }

    public HomePageArrowImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    public HomePageArrowImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private float downY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downY = event.getRawY();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getRawY() - downY) > 10){//如果用户从箭头位置滑动,则可能出现画不动的
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
        }
        return super.onTouchEvent(event);
    }


}
