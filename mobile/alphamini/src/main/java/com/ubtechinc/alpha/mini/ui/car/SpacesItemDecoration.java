package com.ubtechinc.alpha.mini.ui.car;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by riley.zhang on 2018/7/6.
 */

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpace;
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = mSpace;
    }

    public SpacesItemDecoration(int space) {
        mSpace = space;
    }
}
