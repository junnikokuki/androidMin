package com.ubtechinc.alpha.mini.common

import android.webkit.JavascriptInterface
import com.ubtech.utilcode.utils.LogUtils

/**
 * @Deseription
 * @Author tanghongyu
 * @Time 2018/7/5 17:16
 */
class StarTrekJsInterface constructor(iStarTrekControl: IStarTrekControl) {
    private var iStarTrekControl: IStarTrekControl = iStarTrekControl
    @JavascriptInterface
    fun startPlayVideo(url : String) {
        //TODO 连接机器人
        LogUtils.d( "startPlayVideo url = $url")
        iStarTrekControl.startPlayVideo(url)

    }
}