package com.ubtechinc.alpha.mini.ui.codemao

import com.ubtechinc.alpha.mini.common.IStarTrekControl
import com.ubtechinc.alpha.mini.common.StarTrekControlImpl
import com.ubtechinc.alpha.mini.ui.codemao.listener.IStartTrekPresenter
import com.ubtechinc.alpha.mini.ui.codemao.listener.IStartTrekUIControl

/**
 * @Deseription
 * @Author tanghongyu
 * @Time 2018/7/5 17:23
 */
class StartTrekPresenter constructor(iStartTrekUIControl : IStartTrekUIControl) : IStartTrekPresenter {


    private var iStartTrekUIControl: IStartTrekUIControl = iStartTrekUIControl
    var iStarTrekControl: IStarTrekControl = StarTrekControlImpl(this)

    override fun startPlayVideo(url: String) {
        iStartTrekUIControl.startPlayVideo(url)
    }

    override fun requestJS(js: String) {

        iStartTrekUIControl.requestJS(js)
    }

}