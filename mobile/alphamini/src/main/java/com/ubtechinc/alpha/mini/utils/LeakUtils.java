package com.ubtechinc.alpha.mini.utils;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.ubtechinc.alpha.mini.BuildConfig;

public class LeakUtils {

    private static RefWatcher refWatcher;

    public static void init(Application application){
        if(BuildConfig.DEBUG){
            refWatcher = LeakCanary.install(application);
        }
    }

    public static void watch(Object object){
        if(BuildConfig.DEBUG){
            if(refWatcher != null){
                refWatcher.watch(object);
            }
        }
    }



}
