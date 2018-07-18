package com.ubtechinc.alpha.mini.avatar.common;

import android.util.Log;

public class Tools {

    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1000) {
            Log.d("time", "timeD==" + timeD);
            return true;
        }
        lastClickTime = time;
        return false;
    }


}
