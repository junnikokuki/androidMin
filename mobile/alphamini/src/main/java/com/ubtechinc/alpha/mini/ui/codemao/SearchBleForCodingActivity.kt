package com.ubtechinc.alpha.mini.ui.codemao

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.clj.fastble.IConnectionControl
import com.ubtech.utilcode.utils.LogUtils
import com.ubtech.utilcode.utils.StringUtils

import com.ubtechinc.alpha.mini.R
import com.ubtechinc.alpha.mini.common.AnalysisClickListener
import com.ubtechinc.alpha.mini.common.BaseActivity
import com.ubtechinc.alpha.mini.constants.BusinessConstants
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive
import com.ubtechinc.alpha.mini.widget.MaterialDialog

import java.util.ArrayList

import com.ubtechinc.alpha.mini.entity.RobotInfo
import com.ubtechinc.alpha.mini.ui.PageRouter
import com.ubtechinc.alpha.mini.utils.FastClickUtil
import com.ubtechinc.codemaosdk.HandlerUtils
import com.ubtechinc.codemaosdk.MiniApi
import com.ubtechinc.codemaosdk.bean.UbtBleDevice
import com.ubtechinc.codemaosdk.interfaces.IRobotBleControl


/**
 * @author：wululin
 * @date：2017/10/13 16:08
 * @modifier：ubt
 * @modify_date：2017/10/13 16:08
 * [A brief description]
 * 蓝牙搜索和切换界面界面
 */

class SearchBleForCodingActivity : BaseActivity(), AdapterView.OnItemClickListener {
    /**
     * 扫描到的机器人列表
     */
    private val robotScanResultList = ArrayList<UbtBleDevice>()
    private lateinit var mMiniDeviceAdapter: CodingMiniDeviceAdapter
    private lateinit var mBackIv: ImageView
    private lateinit var mSearchWithRobotRl: LinearLayout
    private lateinit var mSearchingRobotLl: LinearLayout
    private lateinit var mSearchTimeOutRl: RelativeLayout
    private lateinit var mResultLv: ListView
    private lateinit var tvReSearch: TextView
    private lateinit var tvCannotFind: TextView
    private var mIsConnecting = false

    private lateinit var mPbmoredevice: ProgressBar
    private var mConnectedBleDevice: UbtBleDevice? = null
    private var mConnectingBleDevice: UbtBleDevice? = null
    private lateinit var mBindedRobots: List<RobotInfo>
    private val HANDLE_TYPE_SWITCH_DEVICE = 1
    private var HANDLE_TYPE_DISCONNECT_DEVICE = 2
    private var mHandleType = 0
    private var mState = ScanState.SCANNING
    internal var clickListener: AnalysisClickListener = object : AnalysisClickListener() {
        override fun onClick(view: View, reported: Boolean) {
            when (view.id) {
                R.id.tv_retry_search -> {

                    MiniApi.get().startScanRobot(scanResultCallback)

                }
                R.id.iv_close -> onBackPressed()
            }
        }
    }


    private enum class ScanState {
        SCANNING, SCANNING_WITH_ROBOT, SCANINIG_STOPSCAN, CONNECTING
    }

    private fun refreshSearchUI() {
        mSearchWithRobotRl.visibility = View.GONE
        mSearchTimeOutRl.visibility = View.GONE
        mSearchingRobotLl.visibility = View.GONE
        if (mState == ScanState.SCANNING) {//处于扫描状态
            mSearchingRobotLl.visibility = View.VISIBLE
        } else if (mState == ScanState.SCANNING_WITH_ROBOT) {//处于扫描搜索到机器人
            mSearchWithRobotRl.visibility = View.VISIBLE
        } else if (mState == ScanState.SCANINIG_STOPSCAN) {//扫描超时，停止扫描
            if (robotScanResultList.size > 0) {
                mSearchWithRobotRl.visibility = View.VISIBLE
            } else {
                mSearchTimeOutRl.visibility = View.VISIBLE
            }
        } else if (mState == ScanState.CONNECTING) {//连接状态
            mSearchWithRobotRl.visibility = View.VISIBLE
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coding_search_ble)
        initView()

        mBindedRobots = MyRobotsLive.getInstance().robots.data
        mMiniDeviceAdapter = CodingMiniDeviceAdapter(this, robotScanResultList)
        mResultLv.adapter = mMiniDeviceAdapter
        var bundle = intent.extras

        when (bundle?.getInt(PageRouter.INTENT_DATA_ENTER_TYPE)) {
            BusinessConstants.SEARCH_ENTER_TYPE_SWITCH_DEVICE -> {
                //从编程页过来，切换机器
                mConnectedBleDevice = bundle?.getParcelable(PageRouter.INTENT_DATA_CONNECTED_DEIVCE)
                mConnectedBleDevice?.let {
                    MiniApi.get().registerRobotBleStateListener(connectingResultCallback)
                    mState = ScanState.SCANNING_WITH_ROBOT
                    mMiniDeviceAdapter.setConnectedSN(mConnectedBleDevice?.sn)
                    robotScanResultList.add(mConnectedBleDevice!!)
                    mMiniDeviceAdapter.onNotifyDataSetChanged(robotScanResultList)
                    MiniApi.get().startScanRobot(scanResultCallback)
                }


            }
            BusinessConstants.SEARCH_ENTER_TYPE_SEARCH_DEVICE -> {
                //从其他页过来，连接机器
                var hasDevices = bundle.getBoolean(PageRouter.INTENT_KEY_HASDEVICES, false)
                if (hasDevices) {


                    mState = ScanState.SCANNING
                    MiniApi.get().startScanRobot(scanResultCallback)

                } else {

                    mState = ScanState.SCANINIG_STOPSCAN

                }
            }
        }


        refreshSearchUI()
    }
    override fun setStatuesBar() {
    }
    var scanResultCallback = object : IRobotBleControl.ScanResultCallback {
        override fun onScanStarted(boolean: Boolean) {
            if (boolean) {
                mState = ScanState.SCANNING
                refreshSearchUI()
            }

        }


        override fun onScanning(device: UbtBleDevice?) {
            Log.i(TAG, "scanning device = " + device?.sn)
            var isHas = false

            for (scanResult in robotScanResultList) {
                if (StringUtils.isEquals(scanResult.device.mac, device?.device?.mac) || StringUtils.isEquals(scanResult.sn, device?.sn)) {
                    isHas = true
                    break
                }

            }
            if (!isHas) {
                if (device != null) {
                    //只取绑定了机器人，加入列表中
                    for (robot in mBindedRobots) {
                        if (StringUtils.isEquals(robot.robotUserId, device.sn)) {

                            LogUtils.d("has binded device = " + device.sn)
                            mState = ScanState.SCANNING_WITH_ROBOT
                            refreshSearchUI()
                            robotScanResultList.add(device)
                            mMiniDeviceAdapter.onNotifyDataSetChanged(robotScanResultList)
                        } else {
                            continue
                        }
                    }

                }
            }

        }

        override fun onStop() {
            Log.i(TAG, "search stop")
            mState = ScanState.SCANINIG_STOPSCAN
            mPbmoredevice.visibility = View.GONE
            refreshSearchUI()

        }

    }

    private fun initView() {
        mSearchWithRobotRl = findViewById(R.id.search_withrobot_rl)
        mSearchingRobotLl = findViewById(R.id.searching_ll)
        mSearchTimeOutRl = findViewById(R.id.search_norobot_rl)
        mResultLv = findViewById(R.id.result_lv)
        mResultLv.onItemClickListener = this
        mPbmoredevice = findViewById(R.id.pb_moredevice)
        tvCannotFind = findViewById(R.id.cannot_find_hint)
        tvReSearch = findViewById(R.id.tv_retry_search)
        tvReSearch.setOnClickListener(clickListener)
        mBackIv = findViewById(R.id.iv_close)
        mBackIv.setOnClickListener(clickListener)


    }


    override fun onItemClick(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
        if (mIsConnecting || FastClickUtil.isFastClick()) {
            return
        }
        MiniApi.get().stopScanRobot()
        if (mConnectedBleDevice != null) {

            if (StringUtils.isEquals(mConnectedBleDevice?.sn, robotScanResultList[position].sn)) {//如果点击的是自己，则是断开

                showDisconnectRobotHint()
            } else {
                //点击其他，则是切换

                mConnectingBleDevice = robotScanResultList[position]
                showSwitchRobotHint()


            }

        } else {
            mConnectingBleDevice = robotScanResultList[position]
            startConnect(mConnectingBleDevice)
        }


    }

    private fun startConnect(bleDevice: UbtBleDevice?) {


        if (isRobotOnline(bleDevice?.sn)) {
            mState = ScanState.CONNECTING
            refreshSearchUI()
            connectMini(bleDevice)
        } else {
            showRobotOffline()
        }

    }

    private fun showRobotOffline() {
        var dialog = MaterialDialog(this)
        dialog.setTitle(R.string.coding_robot_offline_title)
                .setMessage(R.string.coding_robot_offline_tips)
                .setNegativeButton(R.string.codemao_bluetooth_cancel) {
                    dialog.dismiss()
                }
                .setPositiveButton(R.string.switch_wifi) {
                    MiniApi.get().disconnectRobot()
                    PageRouter.toChooseWifiActivity(this@SearchBleForCodingActivity)
                    finish()
                    dialog.dismiss()
                }.show()

    }

    private fun isRobotOnline(robotSn: String?): Boolean {

        var isRobotOnline = true
        try {
            isRobotOnline = MyRobotsLive.getInstance().getRobotById(robotSn).isOnline
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isRobotOnline
    }

    /**
     * 连接机器人蓝牙
     *
     * @param connectDevice 连接设备
     */
    private fun connectMini(connectDevice: UbtBleDevice?) {
        mMiniDeviceAdapter.setConnecting(connectDevice?.sn)
        Log.d(TAG, "mConnectingSN==" + connectDevice?.sn)
        mIsConnecting = true
        if (connectDevice != null) {
            mPbmoredevice.visibility = View.GONE

            MiniApi.get().connectRobot(connectDevice, connectingResultCallback)

//            CodingOpusRepository.switchCodingModel(connectDevice!!.sn, true, object : ICodingOpusDataSource.ControlOpusCallback {
//                override fun onSuccess() {
//                    CodingAssignBleHelper.getInstance().startScanAssignRobot(BusinessConstants.SCAN_TYPE_CODEMAO, connectDevice)
//                    CodingAssignBleHelper.getInstance().setBluetoothScanListener {
//                        when {
//                            CodingAssignBleHelper.getInstance().assignRobot != null -> {
//                                MiniApi.get().connectRobot(CodingAssignBleHelper.getInstance().assignRobot, connectingResultCallback)
//                            }
//                            else -> {
//                                LogUtils.w("can not find assignRobot connect fail robot = " + connectDevice.sn)
//                                mMiniDeviceAdapter.setConnecting("")
//                                mIsConnecting = false
//                                showBleConnectFail()
//                            }
//                        }
//                    }
//                }
//
//                override fun onFail(e: ThrowableWrapper) {
//                    mMiniDeviceAdapter.setConnecting("")
//                    mIsConnecting = false
//                    showSkillStartFailDialog(e.message!!)
//
//                }
//
//            })


        }
    }

    private fun startSkill() {
        MiniApi.get().handshake(object : IConnectionControl.IControlCallback {
            override fun onSuccess() {
                connectedSuccess()
            }

            override fun onFail(code: Int) {
                LogUtils.w("switchCodingModel fail code = $code")
                mMiniDeviceAdapter.setConnecting("")
                mIsConnecting = false
                showSkillStartFailDialog(CodeMaoErrorCode.getErrorMsg(code))
            }

        })
    }

    private fun connectedSuccess() {

        mMiniDeviceAdapter.setConnectedSN(mConnectedBleDevice?.sn)
        var bundle = Bundle()
        bundle.putInt(PageRouter.INTENT_DATA_ENTER_TYPE, BusinessConstants.CODING_ENTER_TYPE_CONNECTED)
        bundle.putParcelable(PageRouter.INTENT_DATA_CONNECTED_DEIVCE, mConnectedBleDevice)
        PageRouter.toCodingActivity(this@SearchBleForCodingActivity, bundle)
        MyRobotsLive.getInstance().setSelectToRobotId(mConnectedBleDevice?.sn)
        HandlerUtils.runUITask({ finish() }, 2000)
    }


    fun showSkillStartFailDialog(msg: String) {

        var dialog = MaterialDialog(this)
        dialog.setMessage(msg).setPositiveButton(R.string.codemao_bluetooth_sure) { dialog.dismiss() }.show()
    }

    var connectingResultCallback = object : IRobotBleControl.ConnectingResultCallback {


        override fun onConnected(ubtBleDevice: UbtBleDevice) {
            LogUtils.d("onConnected")
            mIsConnecting = true //不允许再点击
            mConnectedBleDevice = ubtBleDevice
            startSkill()
        }

        override fun onConnectFail() {
            LogUtils.d("onConnectFail")
            mConnectingBleDevice = null
            mIsConnecting = false
            mMiniDeviceAdapter.setConnecting("")
            refreshSearchUI()
            showBleConnectFail()

        }

        override fun onDisconnected() {
            LogUtils.d("onDisconnected")

            if (isRobotConnected(mConnectedBleDevice)) {
                when (mHandleType) {
                    HANDLE_TYPE_DISCONNECT_DEVICE -> {
                        showRobotDisconnectSuccess()
                    }
                    HANDLE_TYPE_SWITCH_DEVICE -> {//断开成功后再开始连接
                        startConnect(mConnectingBleDevice)
                    }
                    else -> {
                        showBluetoothDisconnetedWarning()
                    }
                }
            }


            mIsConnecting = false

            mMiniDeviceAdapter.setConnectedSN("")
            mConnectedBleDevice = null


        }


    }

    private fun isRobotConnected(bleDevice: UbtBleDevice?): Boolean {

        if (bleDevice == null) {
            return false
        }
        if (isRobotOnline(bleDevice.sn) && MiniApi.get().isRobotConnected) {
            return true
        }
        return false


    }

    fun showBluetoothDisconnetedWarning() {
        var dialog = MaterialDialog(this)

        dialog.setMessage(R.string.codemao_bluetooth_disconnect).setPositiveButton(R.string.codemao_bluetooth_sure) { dialog.dismiss() }.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onPause() {
        super.onPause()
        LogUtils.d("onPause")
        MiniApi.get().stopScanRobot()
        MiniApi.get().unRegisterRobotBleStateListener(connectingResultCallback)
    }

    override fun onDestroy() {
        if (!MiniApi.get().isRobotConnected) {//正在连接的过程中，退出页面，取消此次连接
            MiniApi.get().disconnectRobot()
        }
        super.onDestroy()
    }

    companion object {
        private val TAG = SearchBleForCodingActivity::class.java.simpleName
    }

    private fun showBleConnectFail() {
        var dialog = MaterialDialog(this)
        dialog.setMessage(R.string.coding_connect_fail).setPositiveButton(R.string.codemao_bluetooth_sure) { dialog.dismiss() }.show()
    }

    private fun showDisconnectRobotHint() {
        var dialog = MaterialDialog(this)
        dialog.setTitle(R.string.codemao_make_sure_disconnect_title)
                .setMessage(R.string.codemao_make_sure_disconnect_msg)
                .setNegativeButton(R.string.codemao_bluetooth_cancel) {
                    dialog.dismiss()
                }
                .setPositiveButton(R.string.codemao_make_sure_disconnect_btn1) {
                    mHandleType = HANDLE_TYPE_DISCONNECT_DEVICE
                    MiniApi.get().disconnectRobot()
                    dialog.dismiss()
                }.show()
    }

    private fun showSwitchRobotHint() {
        var dialog = MaterialDialog(this)
        dialog.setTitle(R.string.coding_switch_robot_title)
                .setMessage(R.string.coding_switch_robot_msg)
                .setNegativeButton(R.string.codemao_bluetooth_cancel) {
                    dialog.dismiss()
                    mConnectingBleDevice = null
                }
                .setPositiveButton(R.string.codemao_bluetooth_switch) {
                    mHandleType = HANDLE_TYPE_SWITCH_DEVICE
                    mMiniDeviceAdapter.setConnectedSN("")
                    MiniApi.get().disconnectRobot()
                    dialog.dismiss()
                }.show()
    }

    private fun showRobotDisconnectSuccess() {
        var dialog = MaterialDialog(this)
        dialog.setMessage(R.string.coding_disconnect_success).setPositiveButton(R.string.codemao_bluetooth_sure) {
            var bundle = Bundle()
            bundle.putInt(PageRouter.INTENT_DATA_ENTER_TYPE, BusinessConstants.CODING_ENTER_TYPE_DISCONNECT)
            PageRouter.toCodingActivity(this@SearchBleForCodingActivity, bundle)
            dialog.dismiss()
            finish()
        }.show()
    }
}
