package com.ubtechinc.alpha.mini.avatar.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.ubtechinc.alpha.mini.avatar.R;


/**
 * @作者：liudongyang
 * @日期: 18/6/7 14:59
 * @描述:
 */

public class AvatarImageView extends android.support.v7.widget.AppCompatImageView {

    // 画最外边圆环的画笔
    private Paint mCirclePaint;

    public AvatarImageView(Context context) {
        super(context);
    }

    public AvatarImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initVariable();
    }


    public AvatarImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVariable();
    }


    private void initVariable() {
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(getResources().getColor(R.color.white));
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(5);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float mXCenter = getWidth() / 2;
        float mYCenter = getHeight() / 2;
        //画出最外层的圆
        canvas.drawCircle(mXCenter, mYCenter, mXCenter - 3, mCirclePaint);
    }
}
