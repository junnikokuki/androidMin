/*
 UBTECH Android Client, Constants
 Copyright (c) 2014 UBTECH Tech Company Limited
 http://www.ubtechinc.net/
 */

package com.ubtechinc.alpha.mini.utils;

import android.os.Environment;

/**
 * [数据库相关]
 *
 * @author zengdengyi
 * @version 1.0
 * @date 2014-7-7
 **/

public class Constants {

    //业务数据库缓存目录
    public final static String PIC_DCIM_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/";
    public final static String ORIGIN_PATH   = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ubtdownload/camera/origin/";
    public final static String THUMB_PATH    = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ubtdownload/camera/.thumbnail/";
    public final static String HD_PATH       = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ubtdownload/camera/hd/";
    public final static String watermarkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ubtdownload/camera/watermark/";
    public final static String CACHE_PATH    = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ubtdownload/camera/";

    public static final int REFRESH_PHOTO = 20060;
    public static final String APP_KEY = "2503915313";



    /**
     * 网络监听
     *
     * @author hjy
     *         created at 2016/11/25 12:03
     */
    public class NetWork {
        //移动：Moblie
        public static final String MOBLIE = "Moblie";
        // Wifi:Wifi
        public static final String WIFI = "Wifi";
        //当前是wifi状态
        public static final int IS_WIFI = 0x001;
        //当前是Mobile状态
        public static final int IS_MOBILE = 0x002;
        //都没有连接
        public static final int NO_CONNECTION = 0x003;
        public static final int CONNECTED = 0x00a;

        public static final int NOT_RESPONSE = 0x004;

    }

}
