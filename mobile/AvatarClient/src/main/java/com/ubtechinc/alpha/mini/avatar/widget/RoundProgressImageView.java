package com.ubtechinc.alpha.mini.avatar.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.ubtechinc.alpha.mini.avatar.R;

/**
 * @作者：liudongyang
 * @日期: 18/5/28 11:25
 * @描述:
 */

public class RoundProgressImageView extends android.support.v7.widget.AppCompatTextView {


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
    private int mcircleWidth = 2;

    private AnimationListener listener;

    public RoundProgressImageView(Context context) {
        super(context);
    }

    public RoundProgressImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initVariable();
    }

    public RoundProgressImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initVariable();
    }

    public void setAnmiationListener(AnimationListener listener) {
        this.listener = listener;
    }


    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.RoundProgressImageView, 0, 0);
        mRadius = typeArray.getDimension(R.styleable.RoundProgressImageView_rg_radius, 40);
        mStrokeWidth = typeArray.getDimension(R.styleable.RoundProgressImageView_strokeWidth, 10);
        mRingColor = typeArray.getColor(R.styleable.RoundProgressImageView_ringColor, 0xFF00c1de);

        mRingRadius = mRadius + mStrokeWidth / 2 - mcircleWidth / 2;
    }

    private void initVariable() {
        //最外层圆形画笔
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(mRingColor);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(mcircleWidth);

        //动态圆弧画笔
        mRingPaint = new Paint();
        mRingPaint.setAntiAlias(true);
        mRingPaint.setColor(mRingColor);
        mRingPaint.setStyle(Paint.Style.STROKE);

        //动态弧形的宽度
        mRingPaint.setStrokeWidth(mStrokeWidth);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        mXCenter = getWidth() / 2;
        mYCenter = getHeight() / 2;
        //画出最外层的圆
        canvas.drawCircle(mXCenter, mYCenter, mRadius + mStrokeWidth, mCirclePaint);

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


    public void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofInt(0, 1).setDuration(2000);
        setVisibility(View.VISIBLE);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                setProgress(fraction * 100);
            }

        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(View.INVISIBLE);
                if (listener != null) {
                    listener.onAnmiationEnd();
                }
            }
        });
        animator.start();
    }

    public interface AnimationListener {
        public void onAnmiationEnd();
    }

}
