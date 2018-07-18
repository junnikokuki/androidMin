package com.ubtechinc.alpha.mini.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.ubtechinc.alpha.mini.R;

/**
 * @Date: 2018/2/2.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class MiniDividerDecoration extends RecyclerView.ItemDecoration {

    private int dividerHeight;

    private int color;

    private Paint dividerPaint;

    private IDividerCallBack callBack;

    private int padding;


    public MiniDividerDecoration(Context ctx, IDividerCallBack callBack) {
        dividerPaint = new Paint();

        this.callBack = callBack;

        dividerHeight = (int) ctx.getResources().getDimension(R.dimen.divider_height);

        color = ContextCompat.getColor(ctx, R.color.color_contacts_divider_line);

        dividerPaint.setColor(color);
    }

    public MiniDividerDecoration(Context ctx, int padding,IDividerCallBack callBack) {
        dividerPaint = new Paint();

        this.callBack = callBack;

        this.padding = padding;

        dividerHeight = (int) ctx.getResources().getDimension(R.dimen.divider_height);

        color = ContextCompat.getColor(ctx, R.color.color_contacts_divider_line);

        dividerPaint.setColor(color);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft() + padding;
        int right = parent.getWidth() - parent.getPaddingRight() - padding;

        for (int i = 0; i < childCount - 1; i++) {
            View view = parent.getChildAt(i);
            int pos = parent.getChildAdapterPosition(view);
            float top = view.getBottom();
            float bottom = view.getBottom() + dividerHeight;
            if (callBack.needDivideLineAdjustToPosition(pos)){
                c.drawRect(left, top, right, bottom, dividerPaint);
            }
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int pos = parent.getChildAdapterPosition(view);

        if (callBack.needDivideLineAdjustToPosition(pos)){
            outRect.bottom = dividerHeight;
        }else{
            outRect.bottom = 0;
        }
    }

    public interface IDividerCallBack{
        boolean needDivideLineAdjustToPosition(int position);
    }




}
