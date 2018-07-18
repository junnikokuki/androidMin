package com.ubtechinc.alpha.mini.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @作者：liudongyang
 * @日期: 18/3/2 17:24
 * @描述: 加入能够禁止ViewPager滑动的功能
 */

public class ControlScrollableViewPager extends ViewPager {

    private boolean isForbidScoll = false;

    public ControlScrollableViewPager(Context context) {
        super(context);
    }

    public ControlScrollableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setForbidScoll(boolean isForbidScoll) {
        this.isForbidScoll = isForbidScoll;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isForbidScoll){
            return false;
        }else{
            return super.onInterceptTouchEvent(ev);
        }
    }


}
