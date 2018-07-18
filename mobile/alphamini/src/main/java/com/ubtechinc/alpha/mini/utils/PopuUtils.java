package com.ubtechinc.alpha.mini.utils;

import android.os.Build;
import android.view.View;
import android.view.Window;
import android.widget.PopupWindow;

/**
 * Created by ubt on 2018/3/9.
 */

public class PopuUtils {


    public static void show(PopupWindow popupWindow, View anchor, int xoff, int yoff){
        popupWindow.setFocusable(false);
        popupWindow.update();
        popupWindow.showAsDropDown(anchor, xoff, yoff);
        fullScreenImmersive(popupWindow.getContentView());
        popupWindow.setFocusable(true);
        popupWindow.update();
    }


    public static void fullScreenImmersive(Window window) {
        if (window != null) {
            fullScreenImmersive(window.getDecorView());
        }
    }

    public static void fullScreenImmersive(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            view.setSystemUiVisibility(uiOptions);
        }
    }
}
