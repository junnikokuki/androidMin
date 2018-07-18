package com.ubtechinc.alpha.mini.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;


/**
 * Created by Administrator on 2016/6/29 0029.
 */
public class WaveLayout extends ViewGroup {

    private String TAG = getClass().getSimpleName();
    //宽度
    private int mWidth;
    //高度
    private int mHeight;
    //波纹view
    private WaveView waveView;


    public WaveLayout(Context context) {
        this(context, null);
    }

    public WaveLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public WaveLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof WaveView) {
                waveView = (WaveView) getChildAt(i);
                waveView.setDuration(1000);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureSize(widthMeasureSpec), measureSize(heightMeasureSpec));
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mWidth = mHeight = Math.min(mWidth, mHeight);
        //测量每个children
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    private int measureSize(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 500;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //设置水波纹位置
        if (waveView != null) {
            waveView.layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
        }
    }

}
