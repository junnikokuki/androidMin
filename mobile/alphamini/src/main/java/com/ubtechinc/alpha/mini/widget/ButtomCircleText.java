package com.ubtechinc.alpha.mini.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;

/**
 * Created by junsheng.chen on 2018/7/9.
 */
public class ButtomCircleText extends AppCompatTextView {

    private Paint mPaint;
    private int mSelectedOrder;
    private RectF mRectF;

    public ButtomCircleText(Context context) {
        super(context);
        init();
    }

    public ButtomCircleText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ButtomCircleText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mRectF = new RectF();
        setGravity(Gravity.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /*mPaint.reset();
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        canvas.drawCircle(getMeasuredWidth() / 2f,
                getMeasuredHeight() / 2f,
                getMeasuredWidth() / 2f - 2,
                mPaint);

        String seletedOrder = String.valueOf(mSelectedOrder);

        mPaint.reset();
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(25);
        mPaint.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetricsInt fm = mPaint.getFontMetricsInt();

        float baselineX =  getMeasuredHeight() / 2f + (fm.bottom - fm.top) / 2f - fm.bottom;
        float baselineY =  getMeasuredWidth() / 2f;
        canvas.drawText(seletedOrder, baselineX, baselineY, mPaint);*/

        super.onDraw(canvas);
        //创建一个RectF，用来限定绘制圆弧的范围

        //设置画笔的颜色
        mPaint.setColor(Color.BLACK);
        //设置画笔的样式，空心
        mPaint.setStyle(Paint.Style.STROKE);
        //设置抗锯齿
        mPaint.setAntiAlias(true);
        //设置画得一个半径，然后比较长和宽，以最大的值来确定长方形的长宽，确定半径
        int r = getMeasuredWidth() > getMeasuredHeight() ? getMeasuredWidth() : getMeasuredHeight();
        //如果设置的padding不一样绘制出来的是椭圆形。绘制的时候考虑padding
        //Log.i("边界", "宽度"+getMeasuredWidth()+"高度"+getMeasuredHeight()+"getPaddingLeft()"+getPaddingLeft()+"getPaddingTop"+getPaddingTop()+"getPaddingRight(): "+getPaddingRight()+"getPaddingBottom()"+getPaddingBottom());
        //当padding都为0的时候，绘制出来的就是RectF限定的区域就是一个正方形
        mRectF.set(getPaddingLeft(), getPaddingTop(), r - getPaddingRight(), r - getPaddingBottom());
        //绘制圆弧
        canvas.drawArc(mRectF, 0, 360, false, mPaint);

    }
}
