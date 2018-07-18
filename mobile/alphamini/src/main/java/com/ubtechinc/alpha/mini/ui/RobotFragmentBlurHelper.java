package com.ubtechinc.alpha.mini.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.view.View;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @Date: 2018/3/5.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class RobotFragmentBlurHelper {


    /**
     * 截图，耗时0.1~0.3，会造成卡顿
     * @param rootView  RobotFragment的根View
     */
    public static void screenShot(final View rootView, final IScreenShotCallBack callBack){

        Bitmap screateShot = null;
        Bitmap resultBitmap = null;
        try{
            rootView.setDrawingCacheEnabled(true);
            rootView.buildDrawingCache();
            //创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
            screateShot = rootView.getDrawingCache();
            if (screateShot != null){
                resultBitmap = ThumbnailUtils.extractThumbnail(screateShot, screateShot.getWidth()/2, screateShot.getHeight()/2);
            }
        }catch (Exception e){
            
        } catch (OutOfMemoryError error){
            if (screateShot != null && !screateShot.isRecycled()) {
                screateShot.recycle();
            }
            System.gc();
        } finally {
            if (screateShot != null && !screateShot.isRecycled()) {
                screateShot.recycle();
            }
            rootView.setDrawingCacheEnabled(false);
            rootView.destroyDrawingCache();
        }
        callBack.onGenerateScreenBitmap(resultBitmap);
    }




    public interface IScreenShotCallBack{
        public void onGenerateScreenBitmap(Bitmap screenBitmap);
    }
}
