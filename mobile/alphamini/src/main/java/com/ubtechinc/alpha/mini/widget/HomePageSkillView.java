package com.ubtechinc.alpha.mini.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ubtech.utilcode.utils.ScreenUtils;
import com.ubtech.utilcode.utils.SizeUtils;
import com.ubtechinc.alpha.mini.R;

/**
 * @Date: 2017/12/28.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class HomePageSkillView extends ViewGroup {

    private String TAG = getClass().getSimpleName();

    private static final int mScreenHight = ScreenUtils.getScreenHeight();
    private static final int mScreenWide = ScreenUtils.getScreenWidth();

    private static final int mMarginMenu = SizeUtils.dp2px(30);//中间与旁边的skill高度的差距
    private static final int mInterSpace = SizeUtils.dp2px(12);//skill上下间隔

    private static final int SCROLL_BOTTOM_ID = 1001;
    private static final int SCROLL_TOP_ID = 1002;
    private static final int SCROLL_IDEL_ID = 1003;
    private static final int SHOWALL_START_ANIMATION_ID = 1004;

    private static final long SCROLL_ANIMATION_TOTAL_TIME_MS = 400;//滑动花费的所有时间
    private static final long SHOW_ALL_CYCLE_TOTAL_TIME_MS = 800;//所有的技能按钮的上下浮动的时间


    private boolean mIsDoingAnmation = false;

    private boolean mIsShowBg = false;

    private static int mSkillItemHeight = -1;
    private static int mSkillItemWide = -1;

    private static int mSkillHeighPlusBottoMargin;

    private static int mMinSlop;

    private static float mCurrentY;          //当前的控件的坐标(以第左边第一个控件为准)
    private static float mStartY;            //开始的Y轴坐标
    private static float mDesY;              //结束的Y轴坐标
    private static float mMinMoveLength;     //触发动画的最小距离

    private float y1;
    private float y2;
    private float deltY;
    private float yDown;
    private float yUp;
    private float totalY;


    private ImageView llClose;
    private View vSpace;
    private ImageView ivShowAll;

    private Handler mHandler;

    public void startAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(ivShowAll, "translationY", 0f, -15f, 0f);
        animator.setDuration(SHOW_ALL_CYCLE_TOTAL_TIME_MS);
        animator.start();
        mHandler.sendEmptyMessageDelayed(SHOWALL_START_ANIMATION_ID, 5000);
    }

    public interface OnBackgroundListener {
        public void onPageStepListener(float percent);

        public void dismissBackGround();

        public void trigerShowUpBackground();
    }

    private OnBackgroundListener listener;

    public HomePageSkillView(Context context) {
        super(context);
        initView();
    }

    public HomePageSkillView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public HomePageSkillView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setOnBackgroundListener(OnBackgroundListener listener) {
        this.listener = listener;
    }


    /**
     * 要求所有的孩子测量自己的大小，然后根据这些孩子的大小完成自己的尺寸测量
     */
    @SuppressLint("NewApi")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 计算出所有的childView的宽和高
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        //第一次赋值的时候，statusbar的高度没有算进去

        if (mSkillItemHeight == -1 && getChildCount() > 0) {//初始化Item的宽高

            mSkillItemHeight = getChildAt(0).getMeasuredHeight();

            mSkillItemWide = getChildAt(0).getMeasuredWidth();

            mSkillHeighPlusBottoMargin = SizeUtils.dp2px(30) + mSkillItemHeight;

        }

        getMoveTotalLength();
    }


    @Override
    protected void onLayout(boolean changed, int l, final int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getId() == R.id.ll_close) {
                child.layout(0, getMeasuredHeight() - SizeUtils.dp2px(55), mScreenWide, getMeasuredHeight());
            } else if (child.getId() == R.id.v_space) {
                child.layout(0, 0, mScreenWide, (int) mStartY);
            } else if (child.getId() == R.id.iv_showall) {
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();

                int left = mScreenWide / 2 - childWidth / 2;

                int top = (getMeasuredHeight() - mSkillItemHeight - SizeUtils.dp2px(8) - childHeight);

                child.layout(left, top, left + childWidth, top + childHeight);
            } else {
                int currentRow = i / 3;

                if (i % 3 == 0) {
                    int top = (getMeasuredHeight() - mSkillHeighPlusBottoMargin) + currentRow * (mSkillItemHeight + mInterSpace);

                    int bottom = (getMeasuredHeight() - mSkillHeighPlusBottoMargin) + currentRow * (mSkillItemHeight + mInterSpace) + mSkillItemHeight;

                    child.layout(SizeUtils.dp2px(12), top, SizeUtils.dp2px(12) + mSkillItemWide, bottom);
                } else if (i % 3 == 1) {
                    int left = mScreenWide / 2 - mSkillItemWide / 2;

                    int top = (getMeasuredHeight() - mSkillItemWide) + currentRow * (mSkillItemHeight + mInterSpace);

                    child.layout(left, top, left + mSkillItemWide, top + mSkillItemHeight);
                } else {
                    int top = (getMeasuredHeight() - mSkillHeighPlusBottoMargin) + currentRow * (mSkillItemHeight + mInterSpace);

                    int bottom = (getMeasuredHeight() - mSkillHeighPlusBottoMargin) + currentRow * (mSkillItemHeight + mInterSpace) + mSkillItemHeight;

                    child.layout(mScreenWide - mSkillItemWide - SizeUtils.dp2px(12), top, mScreenWide - SizeUtils.dp2px(12), bottom);
                }
            }
        }

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getId() == R.id.ll_close) {
                llClose = (ImageView) child;
                child.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scrollSkillItem(SCROLL_BOTTOM_ID, true);
                    }
                });
            } else if (child.getId() == R.id.v_space) {
                vSpace = child;
                child.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scrollSkillItem(SCROLL_BOTTOM_ID, true);
                    }
                });
            } else if (child.getId() == R.id.iv_showall) {
                ivShowAll = (ImageView) child;
                startShowAllAnimation();
                child.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.trigerShowUpBackground();
                        scrollSkillItem(SCROLL_TOP_ID, true);
                        showPartSkill();
                        vSpace.setClickable(true);
                    }
                });
            }
        }
    }


    private void initView() {
        mMinMoveLength = (float) mScreenHight * 0.05f;
        mMinSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SHOWALL_START_ANIMATION_ID:
                        startAnimation();
                        break;
                }
            }
        };
    }


    public void scrollSkillItem(int ways, boolean trigerMove) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i).getId() == R.id.ll_close) {
                continue;
            }
            if (getChildAt(i).getId() == R.id.v_space) {
                scrollSideSkillItem(ways, i, trigerMove);
                continue;
            }
            if (i % 3 == 1) {
                scrollMiddleSkillItem(ways, i, trigerMove);
            } else {
                scrollSideSkillItem(ways, i, trigerMove);
            }
        }
    }


    private void scrollSideSkillItem(final int ways, int index, boolean trigerMove) {
        float startTranslationY = mCurrentY - mStartY;
        final float endTranslationY = locateSkillTranslationY(ways, trigerMove, false);

        ObjectAnimator animation = ObjectAnimator.ofFloat(getChildAt(index), "translationY", startTranslationY, endTranslationY);
        animation.setDuration(SCROLL_ANIMATION_TOTAL_TIME_MS);
        if (index == 0) {
            animation.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    mIsDoingAnmation = true;
                    vSpace.setClickable(false);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsDoingAnmation = false;
                    vSpace.setClickable(true);
                    if (endTranslationY == 0) {
                        mCurrentY = mStartY;//记录一下当前位置
                        listener.dismissBackGround();
                        startShowAllAnimation();
                        dismissPartSkill();
                        mIsShowBg = false;
                        vSpace.setClickable(false);
                        ivShowAll.setClickable(true);
                    } else {
                        mIsShowBg = true;
                        mCurrentY = mDesY;
                    }
                }
            });
            animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float values = (float) animation.getAnimatedValue();
                    float percent = Math.abs(values) / totalY;
                    listener.onPageStepListener(percent);
                    if (percent != 0) {
                        ivShowAll.setVisibility(View.GONE);
                    } else {
                        ivShowAll.setVisibility(View.VISIBLE);
                    }
                    if (percent > 0.95f) {
                        llClose.setVisibility(View.VISIBLE);
                    } else {
                        llClose.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
        animation.start();
    }


    private float locateSkillTranslationY(int ways, boolean trigerMove, boolean isMiddleSkill) {

        float sideTranslationY = mDesY - mStartY;

        float middleTranslation = mDesY - mStartY - mMarginMenu;

        if (ways == SCROLL_IDEL_ID) {
            if (mCurrentY > (mStartY - totalY / 2)) {
                return isMiddleSkill ? middleTranslation : sideTranslationY;
            } else {
                return 0;
            }
        }

        if (ways == SCROLL_TOP_ID) {//滑动到顶部
            if (isMiddleSkill) {
                return trigerMove ? middleTranslation : 0;
            } else {
                return trigerMove ? sideTranslationY : 0;
            }
        } else {//滑动到底部
            if (isMiddleSkill) {
                return trigerMove ? 0 : middleTranslation;
            } else {
                return trigerMove ? 0 : sideTranslationY;
            }
        }

    }


    private void scrollMiddleSkillItem(int ways, int index, boolean trigerMove) {
        float translation = mCurrentY - mStartY;//相对于开始位置的偏移量
        float percent = Math.abs(translation) / totalY;

        float curTranslationY = translation - mMarginMenu * percent;
        float endTranslationY = locateSkillTranslationY(ways, trigerMove, true);

        ObjectAnimator objectAnamtor = ObjectAnimator.ofFloat(getChildAt(index), "translationY", curTranslationY, endTranslationY);
        objectAnamtor.setDuration(SCROLL_ANIMATION_TOTAL_TIME_MS);
        objectAnamtor.start();
    }


    private void getMoveTotalLength() {
        int skillCount = getChildCount() - 3;
        int totalRow;
        if (skillCount % 3 == 0) {
            totalRow = skillCount / 3;
        } else {
            totalRow = skillCount / 3 + 1;
        }

        mStartY = getMeasuredHeight() - mSkillHeighPlusBottoMargin;

        mDesY = getMeasuredHeight() - SizeUtils.dp2px(60) - (totalRow - 1) * SizeUtils.dp2px(30) - totalRow * mSkillItemHeight;

        mCurrentY = mStartY;

        totalY = mStartY - mDesY;

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                y1 = event.getRawY();
                yDown = event.getRawY();//记录滑动趋势
                break;
            case MotionEvent.ACTION_MOVE:
                mIsShowBg = true;
                y2 = event.getRawY();
                deltY = y2 - y1;
                y1 = y2;
                if (Math.abs(y2 - yDown) > mMinSlop) {//如果Y2(滑动事件的当前坐标相对于down事件的坐标，如果大于最小滑动，表示viewgroup需要拦截Move事件,由自己的TouchEvent处理)
                    Log.i(TAG, "onInterceptTouchEvent: 事件已经被拦截了");
                    caculatorCurrentY(deltY);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onInterceptTouchEvent(event);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mIsShowBg && event.getRawY() < mStartY) {//没有背景的情况下并且，不在滑动区域，则时间不去响应
            return false;
        }
        if (mIsDoingAnmation) {//如果正在动画过程中，消费掉所有事件
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                y1 = event.getRawY();
                yDown = event.getRawY();//记录滑动趋势
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "onTouchEvent: MotionEvent.ACTION_MOVE");
                y2 = event.getRawY();
                deltY = y2 - y1;
                y1 = y2;
                caculatorCurrentY(deltY);
                break;
            case MotionEvent.ACTION_UP:
                if (!checkFinishiMove()) {//没有完成了滑动
                    yUp = event.getRawY();
                    float moveLength = yUp - yDown;
                    if (Math.abs(moveLength) > mMinMoveLength) {//大于5%触发滑动
                        Log.i(TAG, "onTouchEvent: 大于5%触发滑动");
                        int ways;
                        if (moveLength > 0) {
                            ways = SCROLL_BOTTOM_ID;
                        } else if (moveLength < 0) {
                            ways = SCROLL_TOP_ID;
                        } else {
                            ways = SCROLL_IDEL_ID;
                        }
                        scrollSkillItem(ways, true);
                    } else {//小于5%触发滑动,弹回去
                        Log.i(TAG, "onTouchEvent: 小于5%未触发滑动");
                        int ways;
                        if (moveLength > 0) {
                            ways = SCROLL_BOTTOM_ID;
                        } else if (moveLength < 0) {
                            ways = SCROLL_TOP_ID;
                        } else {
                            ways = SCROLL_IDEL_ID;
                        }
                        scrollSkillItem(ways, false);
                    }
                } else {
                    Log.d(TAG, "完成了滑动");
                }
                break;

        }
        return true;
    }

    private boolean checkFinishiMove() {
        boolean flag;
        if (mCurrentY < mStartY && mCurrentY > mDesY) {
            flag = false;
        } else {
            flag = true;
        }
        return flag;
    }

    private void caculatorCurrentY(float deltY) {
        mCurrentY = mCurrentY + deltY;
        listener.trigerShowUpBackground();

        ivShowAll.setClickable(false);
        showPartSkill();
        stopShowAllAnimation();

        mIsShowBg = true;//滑动过程中是显示背景的

        if (mCurrentY <= mDesY) {//滑动到了终点
            mIsShowBg = true;
            vSpace.setClickable(true);
            mCurrentY = mDesY;
        } else if (mCurrentY >= mStartY) {//滑动到了起点
            mIsShowBg = false;
            mCurrentY = mStartY;
            dismissPartSkill();
            listener.dismissBackGround();
            startShowAllAnimation();
            vSpace.setClickable(false);
            ivShowAll.setClickable(true);
        }

        float translation = mCurrentY - mStartY;//相对于开始位置的偏移量
        float percent = Math.abs(translation) / totalY;
        listener.onPageStepListener(percent);
        if (percent > 0.95f) {//先通过0.95这规避
            llClose.setVisibility(View.VISIBLE);
        } else {
            llClose.setVisibility(View.INVISIBLE);
        }

        if (percent != 0) {
            ivShowAll.setVisibility(View.GONE);
        } else {
            ivShowAll.setVisibility(View.VISIBLE);
        }


        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i).getId() == R.id.ll_close) {
                continue;
            }
            if (getChildAt(i).getId() == R.id.v_space) {
                getChildAt(i).setTranslationY(translation);
            }
            if (i % 3 == 1) {
                float middle = translation - mMarginMenu * percent;
                getChildAt(i).setTranslationY(middle);
            } else {
                getChildAt(i).setTranslationY(translation);
            }
        }
    }

    private void showPartSkill() {
        int skillcount = getChildCount() - 3;
        if (skillcount <= 3) {
            return;
        }
        for (int i = 3; i < 4; i++) {
            View view = getChildAt(i);
            view.setVisibility(View.VISIBLE);
        }
    }

    private void dismissPartSkill() {
        int skillcount = getChildCount() - 3;
        if (skillcount <= 3) {
            return;
        }
        for (int i = 3; i < 4; i++) {
            View view = getChildAt(i);
            view.setVisibility(View.INVISIBLE);
        }
    }

    public void setScroll2Bottom() {
        scrollSkillItem(SCROLL_BOTTOM_ID, true);
    }

    public void startShowAllAnimation() {
        mHandler.sendEmptyMessageDelayed(SHOWALL_START_ANIMATION_ID, 5000);
    }

    public void stopShowAllAnimation() {
        if (mHandler.hasMessages(SHOWALL_START_ANIMATION_ID)) {
            mHandler.removeMessages(SHOWALL_START_ANIMATION_ID);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mHandler.removeMessages(SHOWALL_START_ANIMATION_ID);
        mHandler = null;
        super.onDetachedFromWindow();
    }
}
