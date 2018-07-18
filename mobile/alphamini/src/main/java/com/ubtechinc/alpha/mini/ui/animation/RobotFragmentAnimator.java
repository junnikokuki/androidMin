package com.ubtechinc.alpha.mini.ui.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

import com.ubtechinc.alpha.mini.databinding.FragmentRobotBinding;
import com.ubtechinc.alpha.mini.entity.RobotInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2017/11/23.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 机器人页面动画效果
 */

public class RobotFragmentAnimator {

    private FragmentRobotBinding binding;

    private IRobotFragAnimationListner listner;

    private IRobotSwitchAnimationListener switchAnimationListener;

    public RobotFragmentAnimator(FragmentRobotBinding binding){
        this.binding = binding;
    }

    public void setAnimationListener(IRobotFragAnimationListner listener){
        this.listner = listener;
    }
    public void setSwitchAnimationListener(IRobotSwitchAnimationListener listener){
        this.switchAnimationListener = listener;
    }


    public void startRobotAnimation(){
        robotFragRobotStageAnima();
        robotStateJumpInAnima(1000);
        robotFragShowSkillAnima(true, 2000);
    }

    public void connectedRobotAnimation(){
        robotStateJumpInAnima(0);
        robotFragShowSkillAnima(false, 500);
    }

    public void switchRobotAnimation(RobotInfo robotInfo){
        robotStateJumpOutAnima(robotInfo);
        robotFragDismissSkillAnima();
    }

    private void robotFragRobotStageAnima(){
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(binding.ivRobot, "alpha", 0f , 1f);
        ObjectAnimator animator3= ObjectAnimator.ofFloat(binding.ivRobot, "scaleY", 0.7f, 1f);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(binding.ivRobot, "scaleX", 0.7f, 1f);
        ArrayList<Animator> animators = new ArrayList<>();
        animators.add(animator2);
        animators.add(animator3);
        animators.add(animator4);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000);
        animatorSet.playTogether(animators);
        animatorSet.start();
    }

    private void robotStateJumpInAnima(int delayTime){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(binding.rlState,"translationY",-400, 0);
        objectAnimator.setDuration(1000).setStartDelay(delayTime);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                listner.onTopStateAnimationStart();
            }
        });
        objectAnimator.start();
    }

    /**
     * RobotFragment界面进场动画时候，需要在技能Item回调结束以后，再开始调用initData()初始化,其他情况则不需要
     * @param needEndCallback 是否需要技能item的回调
     */
    private void robotFragShowSkillAnima(final boolean needEndCallback, int delayTime){
        setSkillItemInVisiable();
        ObjectAnimator videoItem = ObjectAnimator.ofFloat(binding.skillAvatar,"scaleY",0, 1);
        videoItem.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                listner.onSkillAnimationStart();
                binding.skillAvatar.setVisibility(View.VISIBLE);
            }
        });
        ObjectAnimator skillItem = ObjectAnimator.ofFloat(binding.skillCall,"scaleY",0, 1);
        skillItem.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                binding.skillCall.setVisibility(View.VISIBLE);
            }
        });
        ObjectAnimator albumItem = ObjectAnimator.ofFloat(binding.skillGallery,"scaleY",0, 1);
        albumItem.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                binding.skillGallery.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (needEndCallback){
                    listner.onRobotFragAnimationEnd();
                }
            }
        });
        skillItem.setStartDelay(100);
        albumItem.setStartDelay(200);
        List<Animator> animators = new ArrayList<>();
        animators.add(videoItem);
        animators.add(skillItem);
        animators.add(albumItem);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(300).setStartDelay(delayTime);
        animatorSet.playTogether(animators);
        animatorSet.start();
    }

    private void setSkillItemInVisiable() {
        binding.skillAvatar.setVisibility(View.INVISIBLE);
        binding.skillCall.setVisibility(View.INVISIBLE);
        binding.skillGallery.setVisibility(View.INVISIBLE);
    }

    public void robotFragDismissSkillAnima(){
        List<Animator> animators = new ArrayList<>();
        ObjectAnimator videoItem = ObjectAnimator.ofFloat(binding.skillAvatar,"scaleY", 1,0);
        ObjectAnimator skillItem = ObjectAnimator.ofFloat(binding.skillCall,"scaleY", 1,0);
        ObjectAnimator albumItem = ObjectAnimator.ofFloat(binding.skillGallery,"scaleY", 1,0);
        skillItem.setStartDelay(100);
        albumItem.setStartDelay(200);
        animators.add(videoItem);
        animators.add(skillItem);
        animators.add(albumItem);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(300);
        animatorSet.playTogether(animators);
        animatorSet.start();
    }

    public void robotStateJumpOutAnima(final RobotInfo info){
        ObjectAnimator imageObject = ObjectAnimator.ofFloat(binding.rlState, "translationY", 0, -400);
        imageObject.setDuration(500);
        imageObject.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                switchAnimationListener.onRobotSwitchAnimationEnd(info);
            }
        });
        imageObject.start();
    }


    public interface IRobotFragAnimationListner{
        public void onSkillAnimationStart();

        public void onTopStateAnimationStart();

        public void onRobotFragAnimationEnd();
    }

    public interface IRobotSwitchAnimationListener{
        void onRobotSwitchAnimationEnd(RobotInfo info);
    }

}
