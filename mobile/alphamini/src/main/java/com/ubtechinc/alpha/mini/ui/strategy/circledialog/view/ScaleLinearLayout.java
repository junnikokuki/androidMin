package com.ubtechinc.alpha.mini.ui.strategy.circledialog.view;

import android.content.Context;
import android.widget.LinearLayout;

import com.ubtechinc.alpha.mini.ui.strategy.circledialog.scale.ScaleLayoutConfig;


/**
 * Created by hupei on 2017/3/29.
 */
class ScaleLinearLayout extends LinearLayout {

    public ScaleLinearLayout(Context context) {
        super(context);
        ScaleLayoutConfig.init(context.getApplicationContext());
    }
}
