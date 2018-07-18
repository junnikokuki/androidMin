package com.tcl.joylockscreen.view.blur;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by jiahui.chen on 2016/3/16.
 */
public class BlurUtil {

    /**
     * Load genius jni file
     */
    static {
        System.loadLibrary("ImageBlur");
    }

    /**
     * Blur Image By Pixels
     *
     * @param img Img pixel array
     * @param w Img width
     * @param h Img height
     * @param r Blur radius
     */
    protected static native void blurPixels(int[] img, int w, int h, int r);

    /**
     * Blur Image By Bitmap
     *
     * @param bitmap Img Bitmap
     * @param r Blur radius
     */
    protected static native void blurBitmap(Bitmap bitmap, int r);



    private static Bitmap buildBitmap(Bitmap bitmap, boolean canReuseInBitmap) {
        // If can reuse in bitmap return this or copy
        Bitmap rBitmap;
        if (bitmap == null || canReuseInBitmap || bitmap.getConfig() == null) {
            rBitmap = bitmap;
        } else {
            rBitmap = bitmap.copy(bitmap.getConfig(), true);
        }
        return (rBitmap);
    }

    /**
     * StackBlur By Jni Bitmap
     *
     * @param original Original Image
     * @param radius Blur radius
     * @param canReuseInBitmap Can reuse In original Bitmap
     * @return Image Bitmap
     */
    public static Bitmap blurNatively(Bitmap original, int radius, boolean canReuseInBitmap) {
        if (radius < 1) {
            return null;
        }
        Bitmap bitmap = buildBitmap(original, canReuseInBitmap);

        // Return this none blur
        if (radius == 1) {
            return bitmap;
        }

        // Jni BitMap Blur
        blurBitmap(bitmap, radius);

        return (bitmap);
    }

    /**
     * StackBlur By Jni Pixels
     *
     * @param original Original Image
     * @param radius Blur radius
     * @param canReuseInBitmap Can reuse In original Bitmap
     * @return Image Bitmap
     */
    public static Bitmap blurNativelyPixels(Bitmap original, int radius, boolean canReuseInBitmap) {
        if (radius < 1) {
            return null;
        }

        Bitmap bitmap = buildBitmap(original, canReuseInBitmap);

        // Return this none blur
        if (radius == 1) {
            return bitmap;
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        // Jni Pixels Blur
        blurPixels(pix, w, h, radius);

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }


    public static void blurNativelyPixelsHelper(final Bitmap original, final int radius, final boolean canReuseInBitmap, final IBlurCallBack callBack){
        if (radius < 1) {
            callBack.onFailed("radiu  is wrong , can't < 1");
            return;
        }
        final Bitmap bitmap = buildBitmap(original, canReuseInBitmap);

        if (radius == 1) {
            callBack.onCreateBlurBitmap(bitmap);
            return;
        }


        new Thread(){
            @Override
            public void run() {
                // Return this none blur
                if(bitmap == null){
                    return;
                }
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                int[] pix = new int[w * h];
                bitmap.getPixels(pix, 0, w, 0, 0, w, h);
                blurPixels(pix, w, h, radius);

                bitmap.setPixels(pix, 0, w, 0, 0, w, h);

                callBack.onCreateBlurBitmap(bitmap);
            }
        }.start();
    }

    public interface IBlurCallBack{
        public void onCreateBlurBitmap(Bitmap newBitmap);

        public void onFailed(String errorMsg);
    }
}
