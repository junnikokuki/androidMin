package com.ubtechinc.alpha.mini.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * @作者：liudongyang
 * @日期: 18/5/2 16:24
 * @描述:
 */

public class CacheUtils {

    private String mPath;

    public CacheUtils(Context ctx) {
        mPath = ctx.getExternalCacheDir().getAbsolutePath();
        Log.i("0502", "CacheDir:  " +  ctx.getCacheDir().getPath());
        Log.i("0502", "getExternalCacheDir:  " +  mPath);
        Log.i("0502", "externalStorage: " + Environment.getExternalStorageDirectory().getAbsolutePath());
    }


    public String getPath() {
        return mPath;
    }


}
