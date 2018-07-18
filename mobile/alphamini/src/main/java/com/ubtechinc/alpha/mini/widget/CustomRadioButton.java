package com.ubtechinc.alpha.mini.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;
import android.util.Log;

import com.ubtechinc.alpha.mini.R;

/**
 * Created by junsheng.chen on 2018/7/4.
 */
public class CustomRadioButton extends AppCompatRadioButton {

    private boolean mHasUnread;
    private Bitmap mRedPoint;
    private Paint mPaint;

    public CustomRadioButton(Context context) {
        super(context);
        init(context);
    }

    public CustomRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mHasUnread = false;
        // 取 drawable 的长宽
        Drawable drawable = getResources().getDrawable(R.drawable.shape_red_point, null);
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        mRedPoint = bitmap;
        mPaint = new Paint();
    }

    public void notifyUnRead(boolean hasUnread) {
        mHasUnread = hasUnread;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mHasUnread) {

            canvas.save();

            canvas.translate((getMeasuredWidth() - mRedPoint.getWidth()) * 0.6f, 0);
            canvas.drawBitmap(mRedPoint, 0, 0, mPaint);

            canvas.restore();
        }

    }
}
