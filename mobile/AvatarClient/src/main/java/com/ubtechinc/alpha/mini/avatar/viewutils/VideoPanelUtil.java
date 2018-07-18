package com.ubtechinc.alpha.mini.avatar.viewutils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.ubtechinc.alpha.mini.avatar.AvatarActivity;
import com.ubtechinc.alpha.mini.avatar.R;
import com.ubtechinc.alpha.mini.avatar.utils.DensityUtil;
import com.ubtechinc.alpha.mini.avatar.widget.ILiveSdkRootView;

import java.util.Timer;
import java.util.TimerTask;

public class VideoPanelUtil {

    public static final String TAG = "VideoPanelUtil";

    private Activity activity;

    private SurfaceView videoView;
    private ImageView screenShotView;
    private View screenShotAnimationView;

    private ObjectAnimator screenshotAnimation;
    private AnimatorSet screenshotViewAnimation;

    private Timer animationTimer;

    MediaPlayer shootMP;

    private int mVideoWidth;
    private int mVideoHeight;

    float screenshotAnimValue = 0;

    private VideoPanelListener videoPanelListener;

    public VideoPanelUtil(Activity activity, VideoPanelListener listener) {
        this.activity = activity;
        this.videoPanelListener = listener;
        init();
    }

    public void setVideoView(SurfaceView videoView) {
        this.videoView = videoView;
    }

    //TODO
    public void screenshot() {
        if (videoView.getClass() == ILiveSdkRootView.class) {
            ((ILiveSdkRootView) videoView).setSheetScreenListener(new ILiveSdkRootView.SheetScreenListener() {
                @Override
                public void onSuccess(final Bitmap bitmap) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            playPose();
                            if (animationTimer != null) {
                                animationTimer.cancel();
                            }
                            screenShotView.setImageBitmap(bitmap);
                            startScreenshotAnimation();
                        }
                    });
                }
            });
        }
    }

    private void init() {
        screenShotView = (ImageView) activity.findViewById(R.id.photo_iv);
        screenShotAnimationView = activity.findViewById(R.id.take_photo_animation_view);

        mVideoWidth = activity.getWindowManager().getDefaultDisplay().getWidth();
    }

    /**
     * 播放拍照声音
     */
    private void playPose() {
        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

        if (volume != 0) {
            if (shootMP == null)
                shootMP = MediaPlayer.create(activity, R.raw.pose);
            if (shootMP != null)
                shootMP.start();
        }
    }


    private void startPhotoViewAnimation() {
        if (animationTimer != null) {
            animationTimer.cancel();
        }
        if (screenshotViewAnimation != null) {
            screenshotViewAnimation.cancel();
        }
        animationTimer = new Timer();
        animationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        screenShotView.setVisibility(View.VISIBLE);
                        ObjectAnimator animatorX = ObjectAnimator.ofFloat(screenShotView, "scaleX", 1f, 0f);
                        ObjectAnimator animatorY = ObjectAnimator.ofFloat(screenShotView, "scaleY", 1f, 0f);
                        screenShotView.setPivotX(mVideoWidth);
                        screenShotView.setPivotY(mVideoHeight - DensityUtil.dp2px(activity, 15));
                        screenshotViewAnimation = new AnimatorSet();
                        screenshotViewAnimation.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                screenShotView.setVisibility(View.GONE);
                                if (videoPanelListener != null) {
                                    videoPanelListener.onScreenshot();
                                }
                                ((AvatarActivity)activity).showTopMessage(activity.getString(R.string.robot_save_photo));
                                screenshotViewAnimation = null;
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        screenshotViewAnimation.setDuration(1500);
                        //两个动画同时执行
                        screenshotViewAnimation.playTogether(animatorX, animatorY);
                        screenshotViewAnimation.start();
                    }
                });
                animationTimer = null;
            }
        }, 500);


    }

    private void startScreenshotAnimation() {
        if (screenshotAnimation != null) {
            screenshotAnimation.cancel();
            screenShotAnimationView.setVisibility(View.GONE);
        }
        screenShotAnimationView.setVisibility(View.VISIBLE);
        screenshotAnimation = ObjectAnimator.ofFloat(screenShotAnimationView, "alpha", 0f, 1f, 0.0f);
        screenshotAnimation.setDuration(500);
        screenshotAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.d("VIDEOUTIL", "onAnimationUpdate");
                float animationValue = (float) animation.getAnimatedValue();
                if (screenshotAnimValue < animationValue) {
                    screenshotAnimValue = animationValue;
                }

                if (screenshotAnimValue > animationValue && screenShotView.getVisibility() == View.GONE) {
                    screenshotAnimValue = animationValue;
                    startPhotoViewAnimation();
                }
            }
        });
        screenshotAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                screenShotAnimationView.setVisibility(View.GONE);
                startPhotoViewAnimation();
                screenshotAnimation = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        screenshotAnimation.start();
    }

    public interface VideoPanelListener {

        public void onScreenshot();
    }

}
