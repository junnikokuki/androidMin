package com.ubtechinc.alpha.mini.common;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by ubt on 2017/11/2.
 */

public class ListDecoration extends RecyclerView.ItemDecoration {

    private int mSpace;

    private boolean needFirst;

    public ListDecoration(int space) {
        this(space,false);
    }

    public ListDecoration(int space, boolean needFirst){
        this.mSpace = space;
        this.needFirst = needFirst;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if(!needFirst){
            if (parent.getChildAdapterPosition(view) != 0) {
                outRect.top = mSpace;
            }
        }else{
            outRect.top = mSpace;
        }

    }
}
