package com.ubtechinc.alpha.mini.ui.codemao

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.ubtech.utilcode.utils.LogUtils
import com.ubtechinc.alpha.mini.R
import com.ubtechinc.alpha.mini.utils.CleanLeakUtils


class VideoPlayerActivity : AppCompatActivity() {

    lateinit var videoPlayer: StandardGSYVideoPlayer

    internal var orientationUtils: OrientationUtils? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)
        init()
    }

    private fun init() {
        videoPlayer = findViewById<View>(R.id.video_player) as StandardGSYVideoPlayer
        var mUrl: String? = null
        try {
            mUrl = intent.getStringExtra("url")
            LogUtils.d("url = $mUrl")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        videoPlayer.setUp(mUrl, true, "")

        //        //增加封面
        //        ImageView imageView = new ImageView(this);
        //        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //        imageView.setImageResource(R.mipmap.xxx1);
        //        videoPlayer.setThumbImageView(imageView);
        //增加title
        videoPlayer.titleTextView.visibility = View.VISIBLE
        //设置返回键
        videoPlayer.backButton.visibility = View.VISIBLE
        //设置旋转
        orientationUtils = OrientationUtils(this, videoPlayer)
        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        videoPlayer.fullscreenButton.setOnClickListener { orientationUtils!!.resolveByClick() }
        //是否可以滑动调整
        videoPlayer.setIsTouchWiget(true)
        //设置返回按键功能
        videoPlayer.backButton.setOnClickListener { onBackPressed() }
        videoPlayer.startPlayLogic()
    }


    override fun onPause() {
        super.onPause()
        videoPlayer.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        videoPlayer.onVideoResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
        if (orientationUtils != null)
            orientationUtils!!.releaseListener()
        CleanLeakUtils.fixInputMethodManagerLeak(this)
    }

    override fun onBackPressed() {
        //先返回正常状态
//        if (orientationUtils!!.screenType == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//            videoPlayer.fullscreenButton.performClick()
//            return
//        }
        //释放所有
        videoPlayer.setVideoAllCallBack(null)
        super.onBackPressed()
    }
}
