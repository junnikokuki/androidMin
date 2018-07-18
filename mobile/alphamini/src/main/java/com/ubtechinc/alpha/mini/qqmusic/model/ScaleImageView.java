package com.ubtechinc.alpha.mini.qqmusic.model;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

/**
 * @author htoall
 * @Description: 比例控件
 * @date 2018/4/21 下午8:17
 * @copyright TCL-MIE
 */
public class ScaleImageView extends android.support.v7.widget.AppCompatImageView{

    private static final String TAG = "ScaleImageView";

    public ScaleImageView(Context context) {
        super(context);
    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = width * 54 / 96;
        setMeasuredDimension(width, height);
        Log.i(TAG, " width: " + width + " height : " + height);
    }
}
