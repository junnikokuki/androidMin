package com.ubtechinc.alpha.mini.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

/**
 * @作者：liudongyang
 * @日期: 18/3/28 18:49
 * @描述:
 */

public class TestFrameLayout extends FrameLayout {

    public TestFrameLayout(@NonNull Context context) {
        super(context);
    }

    public TestFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TestFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void setVisibility(int visibility) {
        Throwable throwable = new Throwable();
        Log.d("0328", " stack : " + Log.getStackTraceString(throwable));
        super.setVisibility(visibility);
    }
}
