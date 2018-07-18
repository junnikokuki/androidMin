package com.ubtechinc.alpha.mini.ui.codemao.listener

import android.content.Context
import android.os.Bundle

/**
 * @Deseription
 * @Author tanghongyu
 * @Time 2018/5/17 20:59
 */
interface ICodeMaoUIControl {

    fun closeBlocklyWindow()
    fun requestJS(js : String)
    /**
     * 蓝牙已断开警告
     */
    fun showBluetoothDisconnetedWarning()

    /**
     * 蓝牙已关闭警告
     */
    fun showBluetoothCloseWarning()

    /**
     * 断开机器人提示
     */
    fun showDisconnectRobotHint()


    fun showConnectRobotSuccess(robotSn : String?)

    fun showConnectRobotFail()

    fun showRobotOffline()

    /**
     * 根据不同类型显示不同UI
     */
    fun showView(status : Int)

    fun startOpenBle()

    fun startBindRobot()

    fun skipToSearchBle(data: Bundle)

    fun showLoadingDialog()

    fun dismissLoadingDialog()

    fun showRobotDisconnectSuccess()

    fun showSkillStartFailDialog(msg:String)

    fun showUpdateDialog()
    fun showFirtTimeDialog()
    fun startPlayVideo(url : String)
    fun getContext() : Context
    fun showUploadWindow(content : String)
}