package com.ubtechinc.alpha.mini.ui.codemao.listener

import android.content.Context
import android.content.Intent

/**
 * @Deseription
 * @Author tanghongyu
 * @Time 2018/5/17 20:59
 */
interface ICodeMaoPresenter : IBasePresenter {

    fun closeBlocklyWindow()
    fun requestJS(js : String)
    fun onPhoneKeyBack()
    fun bluetoothManage()
    fun disconnectRobot()
    fun checkRunCondition(checkRunConditionCallback: CheckRunConditionCallback)
    fun handleNewIntent(newIntent : Intent?)

    fun startPlayVideo(url:String)
    fun stopRunProgram()
    fun getContext() : Context

    interface CheckRunConditionCallback {
        fun onSuccess()
        fun onFail()
    }

    fun startRender(url: String)
    fun upload(context: String)


}