package com.ubtechinc.alpha.mini.utils;

import android.graphics.drawable.Drawable;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.ubtechinc.alpha.mini.AlphaMiniApplication;

/**
 * 在非安卓组件类中获取资源的方法.
 */

public class ResourceUtils {

    public static String getString(@StringRes int id) {
        return AlphaMiniApplication.getInstance().getResources().getString(id);
    }

    public static String getString(@StringRes int id,Object...objects) {
        return AlphaMiniApplication.getInstance().getResources().getString(id,objects);
    }

    public static String[] getStringArray(@ArrayRes int id) {
        return AlphaMiniApplication.getInstance().getResources().getStringArray(id);
    }

    public static Drawable getDrawable(@DrawableRes int id){
        return AlphaMiniApplication.getInstance().getResources().getDrawable(id);
    }

    public static int getColor(@ColorRes int id){
        return AlphaMiniApplication.getInstance().getResources().getColor(id);
    }
}
