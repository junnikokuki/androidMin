package com.ubtechinc.alpha.mini.viewmodel;

/**
 * Created by hongjie.xiang on 2017/10/30.
 */

import android.widget.GridView;

public class PhotoGridView extends GridView {
    public PhotoGridView(android.content.Context context,
                         android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置不滚动
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 5,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }

}
