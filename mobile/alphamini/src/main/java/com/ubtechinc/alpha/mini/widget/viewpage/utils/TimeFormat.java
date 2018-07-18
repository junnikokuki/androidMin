package com.ubtechinc.alpha.mini.widget.viewpage.utils;

/**
 * Created by junsheng.chen on 2018/7/12.
 */
public class TimeFormat {

    public static String getTime(long duration) {
        long min = (duration / 1000) / 60;
        long sec = (duration / 1000) % 60;
        return getType(min) + ":" + getType(sec);
    }


    private static String getType(long time) {
        return time < 10 ? "0" + time : String.valueOf(time);
    }
}
