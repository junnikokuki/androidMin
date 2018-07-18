package com.ubtechinc.alpha.mini.common;

import android.view.View;

import com.ubtechinc.alpha.mini.constants.Constants;
import com.ubtechinc.alpha.mini.ui.utils.Utils;

/**
 * Created by ubt on 2017/9/30.
 */

public abstract class AnalysisClickListener implements View.OnClickListener {

    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    @Override
    public void onClick(View view) {
        if(!Utils.isFastDoubleClick(view.getId())) {
            if (Constants.analysisBtn.get(view.getId()) == null) {
                onClick(view, false);
            } else {
                onClick(view, true);
            }
        }


    }

    public abstract void onClick(View view, boolean reported);
}
