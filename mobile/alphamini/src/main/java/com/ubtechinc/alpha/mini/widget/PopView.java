package com.ubtechinc.alpha.mini.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;

/**
 * @Date: 2017/12/1.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 用于外部(底部或者顶部)飞入屏幕.
 * 实践的过程中发现，如果设置为Gone，或者延迟加载(ViewStub)
 * 第一次的效果有问题，因为Gone的话，roootView.getHeight() = 0
 *
 */

public abstract class PopView {

    protected View mRootView;

    protected Context mCtx;

    private long mShowDuration = 500;

    private long mDismissDuration = 500;

    public enum PopWays{
        Bottom , Top;
    }

    private PopWays ways = PopWays.Bottom;

    private IAnmamtionListener listener;

    private IDissmissAnmaionListener mDismissListener;

    private IStartAnmaionListener mStartListener;

    private IAnimationUpdataListener mUpdataListener;

    public PopView(View view){
        this.mRootView = view;
        mCtx = view.getContext();
        setViewContent();
    }


    public void setPopways(PopWays ways){
        this.ways = ways;
    }


    abstract protected void setViewContent();


    public void setmShowDuration(long mShowDuration){
        this.mShowDuration = mShowDuration;
    }

    public void setmDismissDuration(long mDismissDuration){
        this.mDismissDuration = mDismissDuration;
    }

    public void addAnimationListener(IAnmamtionListener listener){
        this.listener = listener;
    }

    public void addDismissAnimationListener(IDissmissAnmaionListener listener){
        this.mDismissListener = listener;
    }

    public void addStartAnimationListener(IStartAnmaionListener listener){
        this.mStartListener = listener;
    }

    public void addAnimationUpdataListener(IAnimationUpdataListener listener){
        this.mUpdataListener = listener;
    }

    public void show(){
        mRootView.setVisibility(View.VISIBLE);
        ObjectAnimator animator;
        if (ways == PopWays.Bottom){
            animator = ObjectAnimator.ofFloat(mRootView, "translationY", mRootView.getHeight(), 0);
        }else{
            animator = ObjectAnimator.ofFloat(mRootView, "translationY", -mRootView.getHeight(), 0);
        }
        animator.setDuration(mShowDuration);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (listener != null){
                    listener.animationStart();
                }
                if (mStartListener != null){
                    mStartListener.animationStart();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener != null){
                    listener.animationEnd();
                }
            }


        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mUpdataListener != null){
                    mUpdataListener.animationUpdate(animation.getAnimatedFraction());
                }
            }
        });
        animator.start();
    }

    public void dismiss(){
        ObjectAnimator animator;
        if (ways == PopWays.Bottom){
            animator = ObjectAnimator.ofFloat(mRootView, "translationY", 0, mRootView.getHeight());
        }else{
            animator = ObjectAnimator.ofFloat(mRootView, "translationY", 0, -mRootView.getHeight());
        }
        animator.setDuration(mDismissDuration);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (listener != null){
                    listener.animationStart();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mRootView.setVisibility(View.GONE);
                if (listener != null){
                    listener.animationEnd();
                }

                if (mDismissListener != null){
                    mDismissListener.animationEnd();
                }
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mUpdataListener != null){
                    mUpdataListener.animationUpdate(animation.getAnimatedFraction());
                }
            }
        });
        animator.start();
    }

    public interface IAnmamtionListener{
        void animationStart();

        void animationEnd();
    }

    public interface IDissmissAnmaionListener{
        void animationEnd();
    }

    public interface IStartAnmaionListener{
        void animationStart();
    }

    public interface IAnimationUpdataListener{
        void animationUpdate(float percent);
    }

}
