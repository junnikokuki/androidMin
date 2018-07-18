package com.ubtechinc.alpha.mini.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ubtechinc.alpha.mini.R;


/**
 * @作者：liudongyang
 * @日期: 18/5/28 11:25
 * @描述:
 */

public class ProgressImageView extends android.support.v7.widget.AppCompatTextView {

    private String TAG = getClass().getSimpleName();
    // 画最外边圆环的画笔
    private Paint mCirclePaint;
    // 画圆环的画笔
    private Paint mRingPaint;
    // 圆环颜色
    private int mRingColor;
    // 半径
    private float mRadius;
    // 圆环半径
    private float mRingRadius;
    // 圆环宽度
    private float mStrokeWidth;
    // 圆心x坐标
    private int mXCenter;
    // 圆心y坐标
    private int mYCenter;
    // 总进度
    private int mTotalProgress = 100;
    // 当前进度
    private float mProgress;
    // 最外层圆的宽度
    private int mCircleWidth = 10;

    private int mPlayTime_ms = 5000;


    private AnimationListener listener;

    public ProgressImageView(Context context) {
        super(context);
    }

    public ProgressImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initVariable();
    }

    public ProgressImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initVariable();
    }

    public void setAnmiationListener(AnimationListener listener) {
        this.listener = listener;
    }


    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs != null){
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ProgressImageView);
            mRadius = ta.getDimension(R.styleable.ProgressImageView_rg_radius, 30);
            mStrokeWidth = ta.getDimension(R.styleable.ProgressImageView_strokeWidth, 10);
            mRingColor = ta.getColor(R.styleable.ProgressImageView_ringColor, 0xFF00c1de);
            ta.recycle();
        }
        mRingRadius = mRadius;
    }

    private void initVariable() {
        //最外层圆形画笔
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(ContextCompat.getColor(getContext(), R.color.text_gray));
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(mCircleWidth);

        //动态圆弧画笔
        mRingPaint = new Paint();
        mRingPaint.setAntiAlias(true);
        mRingPaint.setColor(ContextCompat.getColor(getContext(), R.color.white));
        mRingPaint.setStyle(Paint.Style.STROKE);

        //动态弧形的宽度
        mRingPaint.setStrokeWidth(mStrokeWidth);
    }

    public void setRadius(int wide){
        mRingRadius = wide;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mXCenter = getWidth() / 2;
        mYCenter = getHeight() / 2;
        //画出最外层的圆
        canvas.drawCircle(mXCenter, mYCenter, mRadius, mCirclePaint);

        //动态画圆环
        if (mProgress > 0) {
            RectF oval = new RectF();
            oval.left = (mXCenter - mRingRadius);
            oval.top = (mYCenter - mRingRadius);
            oval.right = mRingRadius * 2 + (mXCenter - mRingRadius);
            oval.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);
            canvas.drawArc(oval, -90, (mProgress / mTotalProgress) * 360, false, mRingPaint);
        }
    }

    public void setProgress(float progress) {
        mProgress = progress;
        invalidate();
    }


    public void setMode(boolean isPoliceMode) {
        if (isPoliceMode) {
            mCirclePaint.setColor(getResources().getColor(R.color.text_gray));
            mCirclePaint.setAlpha((int) (255 * 0.5));
            mRingPaint.setColor(ContextCompat.getColor(getContext(), R.color.blue_btn_normal));
        } else {
            mCirclePaint.setColor(getResources().getColor(R.color.text_gray));
            mCirclePaint.setAlpha((int) (255 * 0.5));
            mRingPaint.setColor(ContextCompat.getColor(getContext(), R.color.white));
        }
    }

    public void setPlayTime(int time){
        this.mPlayTime_ms = time;
    }

    private ValueAnimator mValueAnimator;
    public void startAnimation() {
        mValueAnimator = ValueAnimator.ofInt(0, 1).setDuration(mPlayTime_ms);
        setVisibility(View.VISIBLE);

        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                setProgress(fraction * 100);
                Log.i(TAG, "onAnimationUpdate: " + fraction);
            }

        });
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(View.INVISIBLE);
                if (listener != null) {
                    listener.onAnmiationEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                setVisibility(View.INVISIBLE);
                if (listener != null) {
                    listener.onAnmiationCancel();
                }
            }
        });
        mValueAnimator.start();
    }

    public void cancelAnimation(){
        if (mValueAnimator != null && mValueAnimator.isRunning()){
            mValueAnimator.cancel();
        }
    }

    public interface AnimationListener {
        void onAnmiationEnd();

        void onAnmiationCancel();
    }

}
