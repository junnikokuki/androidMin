package com.ubtechinc.alpha.mini.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.ubtechinc.alpha.mini.R;

/**
 * @作者：liudongyang
 * @日期: 18/6/29 17:34
 * @描述:
 */
public class TipRadioButton extends android.support.v7.widget.AppCompatRadioButton {

    private boolean isTipOn = false;

    private Dot mDot;

    private class Dot {

        int color;

        int radius;

        int marginTop;
        int marginRight;

        Dot() {
            float density = getContext().getResources().getDisplayMetrics().density;
            radius = (int) (5 * density);
            marginRight = (int) (8 * density);

            color = getContext().getResources().getColor(R.color.btn_red);
        }

    }

    public boolean isTipOn() {
        return isTipOn;
    }

    public TipRadioButton(Context context) {
        super(context);
        init();
    }

    public TipRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TipRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mDot = new Dot();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isTipOn) {
            float cx = getWidth() - mDot.marginRight - mDot.radius;
            float cy = mDot.marginTop + mDot.radius;

            Drawable drawableTop = getCompoundDrawables()[1];
            if (drawableTop != null) {
                int drawableTopWidth = drawableTop.getIntrinsicWidth();
                if (drawableTopWidth > 0) {
                    int dotLeft = getWidth() / 2 + drawableTopWidth / 2;
                    cx = dotLeft + mDot.radius;
                }
            }

            Paint paint = getPaint();
            //save
            int tempColor = paint.getColor();

            paint.setColor(mDot.color);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(cx, cy, mDot.radius, paint);

            //restore
            paint.setColor(tempColor);
        }
    }

    public void setTipOn(boolean tip) {
        this.isTipOn = tip;
        invalidate(); // 重新绘制
    }
}
