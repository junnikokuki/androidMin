package com.ubtechinc.alpha.mini.avatar.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLException;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;

import com.tencent.ilivesdk.view.AVRootView;

import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;


public class ILiveSdkRootView extends AVRootView {

    public static final String TAG = "ILiveSdkRootView";

    private SheetScreenListener mListener;
    private OnFrameReceiveListener onFrameReceiveListener;
    private boolean mIsTake = false;

    public ILiveSdkRootView(Context context) {
        super(context);
    }

    public ILiveSdkRootView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setSheetScreenListener(SheetScreenListener listener) {
        mIsTake = true;
        mListener = listener;
    }

    public void setOnFrameReceiveListener(OnFrameReceiveListener listener) {
        onFrameReceiveListener = listener;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        if (onFrameReceiveListener != null) {
            onFrameReceiveListener.onFrameReceive();
        }
        if (mIsTake) {
            Bitmap bitmap = createBitmapFromGLSurface(0, 0, getWidth(), getHeight(), gl);
            mListener.onSuccess(bitmap);
            mIsTake = false;
        }
    }


    public interface SheetScreenListener {
        void onSuccess(Bitmap bitmap);
    }

    public interface OnFrameReceiveListener {
        void onFrameReceive();
    }

    private Bitmap createBitmapFromGLSurface(int x, int y, int w, int h, GL10 gl) {
        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);
        try {
            gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE,
                    intBuffer);
            int offset1, offset2;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    int texturePixel = bitmapBuffer[offset1 + j];
                    int blue = (texturePixel >> 16) & 0xff;
                    int red = (texturePixel << 16) & 0x00ff0000;
                    int pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            return null;
        }
        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG,"onKeyDown"+event);
        return super.onKeyDown(keyCode, event);
    }
}
