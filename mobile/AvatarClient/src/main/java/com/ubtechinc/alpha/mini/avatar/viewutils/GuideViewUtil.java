package com.ubtechinc.alpha.mini.avatar.viewutils;

import android.app.Activity;
import android.view.View;

import com.blog.www.guideview.Component;
import com.blog.www.guideview.Guide;
import com.blog.www.guideview.GuideBuilder;
import com.ubtechinc.alpha.mini.avatar.widget.GuideViewComponent;
import com.ubtechinc.alpha.mini.avatar.widget.GuideViewComponent2;

/**
 * @author：wululin
 * @date：2017/8/29 10:53
 * @modifier：ubt
 * @modify_date：2017/8/29 10:53
 * [A brief description]
 * avatar显示新手指引工具类
 */

public class GuideViewUtil {
    private Step mShowStep;
    private Guide mGuide2;
    private Guide mGuide1;
    private Activity mActivity;

    private GuideViewFinishListener finishListener;

    public GuideViewUtil(Activity activity) {
        this.mActivity = activity;
        mShowStep = Step.ONE;
    }

    public void showAvatarGudieView1(View view1, final View view2, GuideViewFinishListener finishListener) {
        this.finishListener = finishListener;
        if (mShowStep == Step.ONE) {
            mShowStep = Step.ONE;
            final GuideViewComponent guideViewComponent = new GuideViewComponent();
            GuideBuilder builder = new GuideBuilder();

            builder.setTargetView(view1)
                    .setAlpha(200)
                    .setHighTargetGraphStyle(Component.CIRCLE)
                    .setOverlayTarget(false)
                    .setOutsideTouchable(true);
            builder.setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
                @Override
                public void onShown() {

                }

                @Override
                public void onDismiss() {

                }
            });
            builder.addComponent(guideViewComponent);
            mGuide1 = builder.createGuide();
            mGuide1.setShouldCheckLocInWindow(true);
            mGuide1.setNeedStatusBar(false);
            mGuide1.show(mActivity);
            guideViewComponent.setGetItListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGuide1.dismiss();
                    mShowStep = Step.TWO;
                    shwoAvatarGudieView2(view2);
                }
            });
        } else if (mShowStep == Step.TWO) {
            shwoAvatarGudieView2(view2);
        }
    }


    public void shwoAvatarGudieView2(View view2) {

        GuideBuilder builder1 = new GuideBuilder();
        builder1.setTargetView(view2)
                .setAlpha(200)
                .setHighTargetGraphStyle(Component.CIRCLE)
                .setOverlayTarget(false)
                .setOutsideTouchable(true);
        builder1.setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
            @Override
            public void onShown() {
            }

            @Override
            public void onDismiss() {
                mShowStep = Step.THREE;
            }
        });
        GuideViewComponent2 guideViewComponent = new GuideViewComponent2();

        builder1.addComponent(guideViewComponent);
        mGuide2 = builder1.createGuide();
        mGuide2.setShouldCheckLocInWindow(true);
        mGuide2.setNeedStatusBar(false);
        mGuide2.show(mActivity);
        guideViewComponent.setGetItListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGuide2.dismiss();
                if (finishListener != null) {
                    finishListener.onFinish();
                }
            }
        });
    }

    public void dismissGuideView1() {
        if (mGuide1 != null) {
            mGuide1.dismiss();
        }
    }

    public void dismissGuideView2() {
        if (mGuide2 != null) {
            mGuide2.dismiss();
        }
    }

    public Step getShowStep() {
        return mShowStep;
    }

    public enum Step {
        ZERO, ONE, TWO, THREE
    }

    public interface GuideViewFinishListener {
        public void onFinish();
    }
}
