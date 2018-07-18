package com.ubtechinc.alpha.mini.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.ubtech.utilcode.utils.SizeUtils;
import com.ubtechinc.alpha.mini.entity.Contact;
import com.ubtechinc.alpha.mini.entity.bean.BasePinYinTagBean;

import java.util.List;

/**
 * 有分类title的 ItemDecoration
 * Created by zhangxutong .
 * Date: 16/08/28
 */

public class TitleItemDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "TitleItemDecoration";
    private List<? extends BasePinYinTagBean> mDatas;
    private Paint mPaint;
    private Rect mBounds;//用于存放测量文字Rect

    private int mTitleHeight;//title的高
    private static int COLOR_TITLE_BG = Color.parseColor("#f2f2f2");
    private static int COLOR_TITLE_FONT = Color.parseColor("#6b6b6b");
    private static int mTitleFontSize;//title字体大小


    public TitleItemDecoration(List<Contact> datas) {
        super();
        mDatas = datas;
        mPaint = new Paint();
        mBounds = new Rect();
        mTitleHeight = SizeUtils.dp2px(24);
        mTitleFontSize = SizeUtils.dp2px(15);
        mPaint.setTextSize(mTitleFontSize);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int position = params.getViewLayoutPosition();
            position --;
            //我记得Rv的item position在重置时可能为-1.保险点判断一下吧
            if (position > -1) {
                if (position == 0) {//等于0肯定要有title的
                    Log.d(TAG, "onDraw -- pos " + (position + 1));
                    drawTitleArea(c, left, right, child, params, position);

                } else {//其他的通过判断
                    if (null != mDatas.get(position).getFirstLetterPy() && !mDatas.get(position).getFirstLetterPy().equals(mDatas.get(position - 1).getFirstLetterPy())) {
                        //不为空 且跟前一个tag不一样了，说明是新的分类，也要title
                        Log.d(TAG, "onDraw -- pos " + (position + 1));
                        drawTitleArea(c, left, right, child, params, position);
                    } else {
                        //none
                    }
                }
            }
        }
    }

    /**
     * 绘制Title区域背景和文字的方法
     *
     * @param c
     * @param left
     * @param right
     * @param child
     * @param params
     * @param position
     */
    private void drawTitleArea(Canvas c, int left, int right, View child, RecyclerView.LayoutParams params, int position) {//最先调用，绘制在最下层
        mPaint.setColor(COLOR_TITLE_BG);
        c.drawRect(left, child.getTop() - params.topMargin - mTitleHeight, right, child.getTop() - params.topMargin, mPaint);
        mPaint.setColor(COLOR_TITLE_FONT);


        mPaint.getTextBounds(mDatas.get(position).getFirstLetterPy(), 0, mDatas.get(position).getFirstLetterPy().length(), mBounds);
        c.drawText(mDatas.get(position).getFirstLetterPy(), child.getPaddingLeft(), child.getTop() - params.topMargin - (mTitleHeight / 2 - mBounds.height() / 2), mPaint);
    }

    @Override
    public void onDrawOver(Canvas c, final RecyclerView parent, RecyclerView.State state) {//最后调用 绘制在最上层
        int pos = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();
        Log.d(TAG, "pos : " + pos);
        pos --;
        if (pos <= -1){
            return;
        }

        String tag = mDatas.get(pos).getFirstLetterPy();
        View child = parent.getChildAt(0);

        if(child == null) {
            Log.d(TAG, " pos " + pos + " size : " + parent.getChildCount());
            return;
        }

        mPaint.setColor(COLOR_TITLE_BG);
        c.drawRect(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getRight() - parent.getPaddingRight(), parent.getPaddingTop() + mTitleHeight, mPaint);
        mPaint.setColor(COLOR_TITLE_FONT);
        mPaint.getTextBounds(tag, 0, tag.length(), mBounds);
        c.drawText(tag, child.getPaddingLeft(),
                parent.getPaddingTop() + mTitleHeight - (mTitleHeight / 2 - mBounds.height() / 2),
                mPaint);

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        Log.d(TAG, "getItemOffsets -- position " + position);
        position --;
        //我记得Rv的item position在重置时可能为-1.保险点判断一下吧
        if (position > -1) {
            if (position == 0) {//等于0肯定要有title的
                Log.d(TAG, "getItemOffsets -- position " + (position + 1));
                outRect.set(0, mTitleHeight, 0, 0);
            } else {//其他的通过判断
                if (null != mDatas.get(position).getFirstLetterPy() && !mDatas.get(position).getFirstLetterPy().equals(mDatas.get(position - 1).getFirstLetterPy())) {
                    outRect.set(0, mTitleHeight, 0, 0);//不为空 且跟前一个tag不一样了，说明是新的分类，也要title
                } else {
                    outRect.set(0, 0, 0, 0);
                }
            }
        }
    }

}
