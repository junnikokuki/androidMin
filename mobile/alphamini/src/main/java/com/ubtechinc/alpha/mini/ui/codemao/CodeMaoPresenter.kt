package com.ubtechinc.alpha.mini.ui.codemao

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.clj.fastble.IConnectionControl
import com.ubtech.utilcode.utils.LogUtils
import com.ubtech.utilcode.utils.SPUtils
import com.ubtech.utilcode.utils.StringUtils
import com.ubtech.utilcode.utils.Utils
import com.ubtechinc.alpha.mini.common.IBlocklyControl
import com.ubtechinc.alpha.mini.constants.BusinessConstants
import com.ubtechinc.alpha.mini.constants.SPConstants
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive
import com.ubtechinc.alpha.mini.ui.PageRouter
import com.ubtechinc.alpha.mini.ui.codemao.listener.ICodeMaoPresenter
import com.ubtechinc.alpha.mini.ui.codemao.listener.ICodeMaoUIControl
import com.ubtechinc.alpha.mini.utils.FastClickUtil
import com.ubtechinc.bluetooth.UbtBluetoothManager
import com.ubtechinc.codemaosdk.MiniApi
import com.ubtechinc.codemaosdk.bean.UbtBleDevice
import com.ubtechinc.codemaosdk.interfaces.IRobotBleControl
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission

/**
 * @Deseription 编程猫页面逻辑处理
 * @Author tanghongyu
 * @Time 2018/5/19 14:33
 */
class CodeMaoPresenter : ICodeMaoPresenter {


    override fun startRender(url: String) {
        iBlocklyControl.startRender(url)
    }

    override fun onCreate() {

    }

    var mIsEnterToConnectDevice = false
    //是否正在连接机器人
    var mIsConnectingRobot = false
    var mCurrentConnectedDevice: UbtBleDevice? = null
    val HANDLE_TYPE_CHECK_CONDITION = 1
    val HANDLE_TYPE_BLUETOOTH_MANAGE = 2
    var mHandlerType = HANDLE_TYPE_CHECK_CONDITION
    var mIsSkillStarted  = false
    private var isDisconnectRobot = false
    override fun handleNewIntent(newIntent: Intent?) {

            var bundle = newIntent?.extras

            when (bundle?.getInt(PageRouter.INTENT_DATA_ENTER_TYPE)) {
                BusinessConstants.CODING_ENTER_TYPE_CONNECT_PURPOSE_DEVICE -> {
                    mIsEnterToConnectDevice = true
                    LogUtils.d("handleNewIntent type connect purpose devices ")
                    var device = bundle.getParcelable<UbtBleDevice>(PageRouter.INTENT_DATA_PURPOSE_DEVICE)
                    connectMini(device)
                }
                BusinessConstants.CODING_ENTER_TYPE_CONNECTED -> {
                    mCurrentConnectedDevice = bundle.getParcelable(PageRouter.INTENT_DATA_CONNECTED_DEIVCE)
                    LogUtils.d("enter is connected " + mCurrentConnectedDevice?.sn)
                    iBlocklyControl.reportRobotBleStatus(true)
                    iBlocklyControl.getRegisterFaces()
                    mIsSkillStarted = true
//                    iBlocklyControl.registerRobotSensors()

                }
                BusinessConstants.CODING_ENTER_TYPE_DISCONNECT -> {
                    iBlocklyControl.reportRobotBleStatus(false)
                    iBlocklyControl.unregisterRobotSensor()
                    iBlocklyControl.unregisterPhoneSensor()
                    mCurrentConnectedDevice = null
                }
            }
    }

    private var mIsReadyNextStep: Boolean = false
    override fun checkRunCondition(checkRunConditionCallback: ICodeMaoPresenter.CheckRunConditionCallback) {
        when {
            FastClickUtil.isFastClick() -> return
            isCodeMaoReady() -> {
                if (isRobotOnline(MiniApi.get().connectedRobot.sn)) {
                    MiniApi.get().controlRunningProgram(true, object : IConnectionControl.IControlCallback {
                        override fun onSuccess() {
                            checkRunConditionCallback.onSuccess()
                            if (!SPUtils.get().getBoolean(SPConstants.IS_SHOWED_CODING_DIALOG)) {
                                SPUtils.get().put(SPConstants.IS_SHOWED_CODING_DIALOG, true)
                                iCodeMaoUIControl.showFirtTimeDialog()
                            }
                        }

                        override fun onFail(code: Int) {

                            iCodeMaoUIControl.showSkillStartFailDialog(CodeMaoErrorCode.getErrorMsg(code))
                        }

                    })

                } else {
                    checkRunConditionCallback.onFail()
                    iBlocklyControl.reportRobotBleStatus(false)
                    iCodeMaoUIControl.showRobotOffline()
                    mCurrentConnectedDevice = null
                    mIsConnectingRobot = false

                }
            }else -> {
                checkRunConditionCallback.onFail()
                if (UbtBluetoothManager.getInstance().isOpenBluetooth && AndPermission.hasPermission(Utils.getContext(), *Permission.LOCATION)) {

                    if (MyRobotsLive.getInstance().robotCount == 0) {//未绑定机器人,跳转页面去绑定

                        iCodeMaoUIControl.startBindRobot()


                    } else {

                        if(MiniApi.get().isRobotConnected) {//如果蓝牙已连接但是Skill未启动，启动Skill
                            startSkill()
                        }else{
                            mHandlerType = HANDLE_TYPE_CHECK_CONDITION

                            prepareScan()
                        }



                    }

                } else {
                    iCodeMaoUIControl.startOpenBle()
                }
            }
        }

    }
    override fun stopRunProgram() {

        MiniApi.get().controlRunningProgram(false, object : IConnectionControl.IControlCallback {
            override fun onSuccess() {
                LogUtils.i("stopRunProgram success")
            }

            override fun onFail(code: Int) {

                LogUtils.w("stopRunProgram onFail code = $code")
            }

        })


    }
    override fun disconnectRobot() {
        isDisconnectRobot = true
        MiniApi.get().disconnectRobot()
    }


    override fun bluetoothManage() {
        if (FastClickUtil.isFastClick()) return

        if (isCodeMaoReady()) {
            mHandlerType = HANDLE_TYPE_BLUETOOTH_MANAGE
            prepareScan()


        } else {
            mHandlerType = HANDLE_TYPE_CHECK_CONDITION
            if (UbtBluetoothManager.getInstance().isOpenBluetooth && AndPermission.hasPermission(Utils.getContext(), *Permission.LOCATION)) {

                if (MyRobotsLive.getInstance().robotCount == 0) {//未绑定机器人,跳转页面去绑定

                    iCodeMaoUIControl.startBindRobot()


                } else {
                    if(MiniApi.get().isRobotConnected) {//如果蓝牙已连接但是Skill未启动，启动Skill
                        startSkill()
                    }else{
                        prepareScan()
                    }

                }

            } else {
                iCodeMaoUIControl.startOpenBle()
            }

        }
    }


    lateinit var iBlocklyControl: IBlocklyControl
    lateinit var iCodeMaoUIControl: ICodeMaoUIControl

    constructor(iCodeMaoUIControl: ICodeMaoUIControl) {
        this.iCodeMaoUIControl = iCodeMaoUIControl
        iBlocklyControl = BlocklyControlImpl(this)
    }

    override fun getContext(): Context {
        return iCodeMaoUIControl.getContext()
    }

    override fun closeBlocklyWindow() {
        iCodeMaoUIControl.closeBlocklyWindow()
    }

    override fun requestJS(js: String) {

        iCodeMaoUIControl.requestJS(js)
    }

    private fun jumpToConnect() {
        LogUtils.d(" jumpToConnect ")
        when (mHandlerType) {
            HANDLE_TYPE_CHECK_CONDITION -> {
                var purposeDevice = CodingBleHelper.getInstance().getPurposeDevice(CodingBleHelper.AUTOCONNECT_MIDDLE_DISTANCE)
                when {
                    purposeDevice != null -> {//附近只搜索到一个机器人，准备连接
                        connectMini(purposeDevice)
                    }
                    CodingBleHelper.getInstance().hasDevices() -> //搜索到多个
                    {
                        LogUtils.d("have more than one devices")
                        var bundle = Bundle()
                        bundle.putBoolean(PageRouter.INTENT_KEY_HASDEVICES, true)
                        bundle.putInt(PageRouter.INTENT_DATA_ENTER_TYPE, BusinessConstants.SEARCH_ENTER_TYPE_SEARCH_DEVICE)
                        iCodeMaoUIControl.skipToSearchBle(bundle)
                        mIsReadyNextStep = false
                        iCodeMaoUIControl.dismissLoadingDialog()
                    }


                    else -> //没有设备,提醒用户重新搜索
                    {
                        var bundle = Bundle()
                        bundle.putBoolean(PageRouter.INTENT_KEY_HASDEVICES, false)
                        bundle.putInt(PageRouter.INTENT_DATA_ENTER_TYPE, BusinessConstants.SEARCH_ENTER_TYPE_SEARCH_DEVICE)

                        iCodeMaoUIControl.skipToSearchBle(bundle)
                        mIsReadyNextStep = false
                        iCodeMaoUIControl.dismissLoadingDialog()
                    }

                }
            }
            HANDLE_TYPE_BLUETOOTH_MANAGE -> {
                iCodeMaoUIControl.dismissLoadingDialog()
                CodingBleHelper.getInstance().stopScan()
                var purposeDevice = CodingBleHelper.getInstance().getPurposeDevice(CodingBleHelper.AUTOCONNECT_MIDDLE_DISTANCE)
                when {
                    purposeDevice != null -> {//附近只搜索到一个机器人

                        if (mCurrentConnectedDevice != null && StringUtils.isEquals(purposeDevice.sn, mCurrentConnectedDevice?.sn)) {//相同则提示断开
                            iCodeMaoUIControl.showDisconnectRobotHint()
                        } else {//与当前机器人不同，则开始跳转切换

                            var bundle = Bundle()
                            bundle.putInt(PageRouter.INTENT_DATA_ENTER_TYPE, BusinessConstants.SEARCH_ENTER_TYPE_SWITCH_DEVICE)
                            bundle.putParcelable(PageRouter.INTENT_DATA_CONNECTED_DEIVCE, mCurrentConnectedDevice)
                            iCodeMaoUIControl.skipToSearchBle(bundle)
                            LogUtils.d("bluetoothManage have more than one devices current device = " + mCurrentConnectedDevice?.sn)
                        }

                    }
                    CodingBleHelper.getInstance().hasDevices() -> //搜索到多个
                    {
                        LogUtils.d("bluetoothManage have more than one device = " + mCurrentConnectedDevice?.sn)
                        var bundle = Bundle()
                        bundle.putInt(PageRouter.INTENT_DATA_ENTER_TYPE, BusinessConstants.SEARCH_ENTER_TYPE_SWITCH_DEVICE)
                        bundle.putParcelable(PageRouter.INTENT_DATA_CONNECTED_DEIVCE, mCurrentConnectedDevice)
                        iCodeMaoUIControl.skipToSearchBle(bundle)
                    }
                    else -> //没有设备,提醒是否断开
                    {
                        iCodeMaoUIControl.showDisconnectRobotHint()
                    }

                }
            }
        }


    }


    private var connectingResultCallback = object : IRobotBleControl.ConnectingResultCallback {


        override fun onConnectFail() {
            LogUtils.d("onConnectFail")
            iCodeMaoUIControl.dismissLoadingDialog()
            iCodeMaoUIControl.showConnectRobotFail()
            mIsConnectingRobot = false
        }

        override fun onConnected(ubtBleDevice: UbtBleDevice) {
            LogUtils.d("onConnected = " + ubtBleDevice?.sn + " currentIMRobotId =  " + MyRobotsLive.getInstance().currentRobotId.value)
            mCurrentConnectedDevice = ubtBleDevice
            mIsConnectingRobot = false
            startSkill()
        }

        override fun onDisconnected() {
            LogUtils.d("onDisconnected")
            if (isRobotConnected(mCurrentConnectedDevice)) {//当前页面，没有连接则不提示。
                mIsConnectingRobot = false
                iBlocklyControl.reportRobotBleStatus(false)
                mCurrentConnectedDevice = null
                if (isDisconnectRobot) {//手动断开
                    iCodeMaoUIControl.showRobotDisconnectSuccess()
                } else {
                    iCodeMaoUIControl.showBluetoothDisconnetedWarning()
                }
            }
            iBlocklyControl.unregisterPhoneSensor()
            mIsSkillStarted = false


        }
    }



    private fun isRobotConnected(bleDevice: UbtBleDevice?): Boolean {

        if (bleDevice == null) {
            return false
        }
        if (MiniApi.get().isRobotConnected) {
            return true
        }
        return false


    }

    private fun connectMini(bleDevice: UbtBleDevice) {

        CodingBleHelper.getInstance().stopScan()
        if (isRobotOnline(bleDevice!!.sn)) {//预先校验机器人，如果在线，则搜索该机器人的编程猫蓝牙，并连接
            iCodeMaoUIControl.showLoadingDialog()
            mIsConnectingRobot = true
            MiniApi.get().connectRobot(bleDevice, connectingResultCallback)

        } else {
            iCodeMaoUIControl.dismissLoadingDialog()
            iCodeMaoUIControl.showRobotOffline()
        }


    }

    private fun startSkill() {
        MiniApi.get().handshake(object : IConnectionControl.IControlCallback {
            override fun onSuccess() {
                connectedSuccess()
            }

            override fun onFail(code: Int) {
                LogUtils.w("switchCodingModel fail code = $code" )
                iCodeMaoUIControl.dismissLoadingDialog()
                if (code == 404) {
                    iCodeMaoUIControl.showUpdateDialog()
                } else {
                    iCodeMaoUIControl.showSkillStartFailDialog(CodeMaoErrorCode.getErrorMsg(code))
                }
            }

        })
    }

    private fun connectedSuccess() {
        mIsSkillStarted = true
        iCodeMaoUIControl.dismissLoadingDialog()
        if (!StringUtils.isEquals(mCurrentConnectedDevice?.sn, MyRobotsLive.getInstance().currentRobotId.value)) {
            iCodeMaoUIControl.showConnectRobotSuccess(mCurrentConnectedDevice?.sn)
        } else if (MyRobotsLive.getInstance().robotCount == 1) {//只有一个机器人的情况
            iCodeMaoUIControl.showConnectRobotSuccess("")
        }

        iBlocklyControl.registerRobotSensors()
        iBlocklyControl.registerPhoneSensors()
        iBlocklyControl.reportRobotBleStatus(true)
        iBlocklyControl.getRegisterFaces()
        MyRobotsLive.getInstance().setSelectToRobotId(mCurrentConnectedDevice!!.sn)
    }


    override fun onResume() {

        MiniApi.get().registerRobotBleStateListener(connectingResultCallback)
        if (isCodeMaoReady()) {

            iBlocklyControl.registerPhoneSensors()
            iBlocklyControl.registerRobotSensors()

        }


    }

    private fun prepareScan() {
        iCodeMaoUIControl.showLoadingDialog()
        if (UbtBluetoothManager.getInstance().isOpenBluetooth && AndPermission.hasPermission(Utils.getContext(), *Permission.LOCATION) && !mIsConnectingRobot) {//默认情况下开启扫描
            CodingBleHelper.getInstance().startScan(BusinessConstants.SCAN_TYPE_MINI, MyRobotsLive.getInstance().robots.data)
            CodingBleHelper.getInstance().setBluetoothScanListener {
                if (mIsReadyNextStep) {
                    iCodeMaoUIControl.showLoadingDialog()
                    jumpToConnect()
                }
            }
        }

        if (CodingBleHelper.getInstance().isReadyToNextStep) {
            jumpToConnect()
        } else {
            mIsReadyNextStep = true
        }

    }


    override fun onPause() {
        LogUtils.d("onPause")
        CodingBleHelper.getInstance().stopScan()
        MiniApi.get().unRegisterRobotBleStateListener(connectingResultCallback)
    }

    override fun onDestory() {
        iBlocklyControl.unregisterPhoneSensor()
        MiniApi.get().disconnectRobot()
    }

    override fun onPhoneKeyBack() {
        iBlocklyControl.reportPhoneKeyBack()
    }


    private fun isRobotOnline(robotSn: String): Boolean {

        var isRobotOnline = true
        try {
            isRobotOnline = MyRobotsLive.getInstance().getRobotById(robotSn).isOnline
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isRobotOnline
    }

    private fun isCodeMaoReady() : Boolean {
        return MiniApi.get().isRobotConnected && mIsSkillStarted
    }

    override fun startPlayVideo(url: String) {
        iCodeMaoUIControl.startPlayVideo(url)
    }

    override fun upload(content : String) {
        iCodeMaoUIControl.showUploadWindow(content)
    }

}