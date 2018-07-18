package com.ubtechinc.alpha.mini.widget.recyclerview.listener;


import com.ubtechinc.alpha.mini.widget.recyclerview.helper.StateViewHelper;
import com.ubtechinc.alpha.mini.widget.recyclerview.item.BaseItemState;

/**
 * 状态页中的点击Listener
 * Created by free46000 on 2017/4/23.
 *
 * @see BaseItemState
 * @see StateViewHelper
 */
@FunctionalInterface
public interface OnStateClickListener {
    void onStateClick();
}
