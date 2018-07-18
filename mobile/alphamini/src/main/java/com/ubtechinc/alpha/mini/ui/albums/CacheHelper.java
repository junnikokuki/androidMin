package com.ubtechinc.alpha.mini.ui.albums;


import android.content.Context;
import android.util.Log;

/**
 * @作者：liudongyang
 * @日期: 18/5/2 18:43
 * @描述: 相册缓存的工具
 */

public class CacheHelper {

    private static String TAG = "CacheHelper";

    private static final String HDPATH      = "/camera/hd/";
    private static final String THUMBPATH   = "/camera/.thumbnail/";
    private static final String ORIGINPATH  = "/camera/origin/";
    private static final String CACHEPATH   = "/camera/";
    private static final String WATERMARKPATH   = "/camera/watermark/";


    private static String ROOT_PATH;

    private static CacheHelper helper;

    private CacheHelper(){

    }
    public static CacheHelper getInstance(){
        if (helper == null){
            synchronized (CacheHelper.class){
                if (helper == null){
                    helper = new CacheHelper();
                }
            }
        }
        return helper;
    }


    public void init(Context ctx){
        ROOT_PATH = ctx.getExternalCacheDir().getAbsolutePath() + "/ubtdownload";
    }

    public static String getRootPath() {
        return ROOT_PATH;
    }

    public static String getThumbPath(){
        return new StringBuffer(ROOT_PATH).append(THUMBPATH).toString();
    }

    public static String getThumbPath(String fileName){
        if (ROOT_PATH == null){
            Log.e(TAG, "root path is null , have to call init");
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(ROOT_PATH).append(THUMBPATH).append(fileName);
        return buffer.toString();
    }

    public static String getHDPath(){
        return new StringBuffer(ROOT_PATH).append(HDPATH).toString();
    }

    public static String getItemHDPath(String fileName){
        if (ROOT_PATH == null){
            Log.e(TAG, "root path is null , have to call init");
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(ROOT_PATH).append(HDPATH).append(fileName);
        return buffer.toString();
    }


    public static String getOrginPath(){
        return new StringBuffer(ROOT_PATH).append(ORIGINPATH).toString();
    }


    public static String getItemOriginPath(String fileName){
        if (ROOT_PATH == null){
            Log.e(TAG, "root path is null , have to call init");
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(ROOT_PATH).append(ORIGINPATH).append(fileName);
        return buffer.toString();
    }

    public static String getWaterMarkPath(){
        if (ROOT_PATH == null){
            Log.e(TAG, "root path is null , have to call init");
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(ROOT_PATH).append(WATERMARKPATH);
        return buffer.toString();
    }

    public static String getWaterMarkPath(String fileName){
        if (ROOT_PATH == null){
            Log.e(TAG, "root path is null , have to call init");
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(ROOT_PATH).append(WATERMARKPATH).append(fileName);
        return buffer.toString();
    }

    public static String getCachePath(){
        if (ROOT_PATH == null){
            Log.e(TAG, "root path is null , have to call init");
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(ROOT_PATH).append(CACHEPATH);
        return buffer.toString();
    }



}
