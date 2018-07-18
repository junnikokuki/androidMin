package com.ubtechinc.alpha.mini.avatar.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLException;

import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import io.agora.rtc.video.ViEAndroidGLES20;

/**
 * Created by ubt on 2017/6/8.
 * 支持截取bitmap的surfaceView
 */

public class SheetscreenView extends ViEAndroidGLES20  {

    private SheetScreenListener mListener;
    private boolean mIsTake = false;
    public SheetscreenView(Context context) {
        super(context);
    }

    public void setSheetScreenListener(SheetScreenListener listener){
        mIsTake = true;
        mListener = listener;
    }
    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        if(mIsTake){
            Bitmap bitmap = createBitmapFromGLSurface(0,0,getWidth(),getHeight(),gl);
            mListener.onSuccess(bitmap);
            mIsTake = false;
        }


    }

    public interface SheetScreenListener{
        void onSuccess(Bitmap bitmap);
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
}
