package com.ubtechinc.alpha.mini.avatar.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubtechinc.alpha.mini.avatar.R;

/**
 * Created by panxiaohe on 16/3/9.
 */
public class FingerFlowViewManager {

    private static FingerFlowViewManager instance;
    private View mDragView;

    private FingerFlowViewManager() {
        super();
    }

    public static FingerFlowViewManager getInstance() {
        if (instance == null) {
            instance = new FingerFlowViewManager();
        }
        return instance;
    }

    public WindowManager init(Context context) {
        if (figerFlowWindowManager == null) {
            figerFlowWindowManager = (WindowManager) context.getSystemService(
                    Context.WINDOW_SERVICE);
        }
        return figerFlowWindowManager;
    }

    public void setUp(Context context, Bitmap bitmap, String text,int x, int y) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        WindowManager.LayoutParams windowParams = getLayoutParams();

        windowParams.x = x + width / 2;

        windowParams.y = y + height / 2;

        mDragView = LayoutInflater.from(context).inflate(R.layout.dragview_layout, null);
        ImageView imageView = (ImageView) mDragView.findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
        TextView mTextView = (TextView) mDragView.findViewById(R.id.textView);
        mTextView.setText(text);
        // 设置想要的大小
        int newWidth = 300;
        int newHeight = 400;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(0.9f, 0.9f);
        Bitmap mbitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        getDrawImageView(context).setImageBitmap(mbitmap);

        init(context).addView(mDragView, windowParams);
    }

    public ImageView getDrawImageView(Context context) {

        if (drawImageView == null) {
            drawImageView = new ImageView(context);

        }

        return drawImageView;
    }

    public void updatePosition(int x, int y) {

        WindowManager.LayoutParams windowParams = getLayoutParams();

        windowParams.x = x + mDragView.getWidth() / 2;

        windowParams.y = y ;

        figerFlowWindowManager.updateViewLayout(mDragView, windowParams);
    }

    public void remove() {
        figerFlowWindowManager.removeView(mDragView);
        drawImageView = null;
    }


    private WindowManager figerFlowWindowManager;

    private ImageView drawImageView;

    private WindowManager.LayoutParams windowParams;

    private WindowManager.LayoutParams getLayoutParams() {
        if (windowParams == null) {
            windowParams = new WindowManager.LayoutParams();
            windowParams.gravity = Gravity.TOP | Gravity.START;
            windowParams.height = LayoutParams.WRAP_CONTENT;
            windowParams.width = LayoutParams.WRAP_CONTENT;
            windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            windowParams.format = PixelFormat.TRANSLUCENT;
            windowParams.windowAnimations = 0;
            windowParams.alpha = 0.8f;
        }
        return windowParams;
    }


    public static void onDestory() {
        instance = null;
    }
}
