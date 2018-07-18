package com.ubtechinc.alpha.mini.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.ubtechinc.alpha.mini.R;

/**
 * Created by ubt on 2018/4/21.
 */

public class CustomSmallProgressBar extends ProgressBar {

    public CustomSmallProgressBar(Context context) {
        this(context, null);
    }

    public CustomSmallProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSmallProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        if (android.os.Build.VERSION.SDK_INT > 22) {//android 6.0替换clip的加载动画
            final Drawable drawable =  getContext().getApplicationContext().getResources().getDrawableForDensity(R.drawable.loading_image_small_v6,getContext().getResources().getDisplayMetrics().densityDpi);
            setIndeterminateDrawable(drawable);
        }
    }
}
