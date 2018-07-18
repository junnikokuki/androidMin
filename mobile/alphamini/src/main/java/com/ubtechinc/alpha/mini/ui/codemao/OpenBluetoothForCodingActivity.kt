package com.ubtechinc.alpha.mini.ui.codemao

import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ubtech.utilcode.utils.LogUtils
import com.ubtechinc.alpha.mini.R
import com.ubtechinc.alpha.mini.common.AnalysisClickListener
import com.ubtechinc.alpha.mini.common.BleNetworkBaseActivity
import com.ubtechinc.alpha.mini.constants.BusinessConstants
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive
import com.ubtechinc.alpha.mini.ui.PageRouter
import com.ubtechinc.alpha.mini.ui.bluetooth.PermissionLocationRequest
import com.ubtechinc.alpha.mini.ui.bluetooth.bean.OpenBluetoothEvent
import com.ubtechinc.alpha.mini.ui.utils.Utils
import com.ubtechinc.bluetooth.UbtBluetoothDevice
import com.ubtechinc.bluetooth.UbtBluetoothManager
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission

class OpenBluetoothForCodingActivity : BleNetworkBaseActivity() {
    override fun onReconnectDevice(device: UbtBluetoothDevice?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_bluetooth_for_coding)
        initView()
        initListener()
    }

    override fun setStatuesBar() {
    }
    private fun initListener() {
        findViewById<ImageView>(R.id.iv_close).setOnClickListener(mOnClickListener)
    }

    private fun initView() {
        findViewById<TextView>(R.id.open_bluetooth).setOnClickListener(mOnClickListener)
    }

    private var mLocationRequest: PermissionLocationRequest? = null
    internal var mOnClickListener: AnalysisClickListener = object : AnalysisClickListener() {
        override fun onClick(view: View, reported: Boolean) {
            when (view.id) {
                R.id.iv_close -> finish()
                R.id.open_bluetooth -> {
                    if (AndPermission.hasPermission(this@OpenBluetoothForCodingActivity, *Permission.LOCATION)) run {
                        LogUtils.i("has Permission.LOCATION ")
                        UbtBluetoothManager.getInstance().openBluetooth(this@OpenBluetoothForCodingActivity)
                    }
                    else {
                        if (null == mLocationRequest) {
                            mLocationRequest = PermissionLocationRequest(this@OpenBluetoothForCodingActivity)
                        }
                        LogUtils.w("request  PermissionLocation")
                        mLocationRequest!!.request(object : PermissionLocationRequest.PermissionLocationCallback {
                            override fun onSuccessful() {
                                Log.d(TAG, "开启定位权限成功")
                                UbtBluetoothManager.getInstance().openBluetooth(this@OpenBluetoothForCodingActivity)
                            }

                            override fun onFailure() {
                                Log.d(TAG, "开启定位权限失败")
                                Utils.getAppDetailSettingIntent(this@OpenBluetoothForCodingActivity)
                            }

                            override fun onRationSetting() {
                                Log.d(TAG, "用户勾选过不再提醒提示")
                                Utils.getAppDetailSettingIntent(this@OpenBluetoothForCodingActivity)
                            }
                        })
                    }

                }


            }
        }
    }
    private var mIsJumpBanding: Boolean = false
    override fun onMessageEvent(event: OpenBluetoothEvent) {
        val blueState = event.mState
        when (blueState) {
            BluetoothAdapter.STATE_TURNING_ON -> {
                if (AndPermission.hasPermission(this@OpenBluetoothForCodingActivity, *Permission.LOCATION)) {

                    if (MyRobotsLive.getInstance().robotCount == 0) {

                        PageRouter.toRemindBindRobotActivity(this)

                    } else {
                        Log.d(TAG, " STATE_TURNING_ON 1")
                        showLoadingDialog()
                        CodingBleHelper.getInstance().startScan(BusinessConstants.SCAN_TYPE_MINI, MyRobotsLive.getInstance().robots.data)
                        CodingBleHelper.getInstance().setBluetoothScanListener {
                                dismissDialog()
                                jumpToConnect()
                        }


                    }

                }


            }
            BluetoothAdapter.STATE_TURNING_OFF -> {

            }
        }

    }

    private fun jumpToConnect() {

        when {
            CodingBleHelper.getInstance().purposeDevice != null -> {//搜索到一个
                var bundle = Bundle()
                bundle.putParcelable(PageRouter.INTENT_DATA_PURPOSE_DEVICE, CodingBleHelper.getInstance().purposeDevice)
                bundle.putInt(PageRouter.INTENT_DATA_ENTER_TYPE, BusinessConstants.CODING_ENTER_TYPE_CONNECT_PURPOSE_DEVICE)
                PageRouter.toCodingActivity(this, bundle)
                finish()
            }
            CodingBleHelper.getInstance().hasDevices() ->  {
                var bundle = Bundle()
                bundle.putBoolean(PageRouter.INTENT_KEY_HASDEVICES, true)
                bundle.putInt(PageRouter.INTENT_DATA_ENTER_TYPE, BusinessConstants.SEARCH_ENTER_TYPE_SEARCH_DEVICE)
                PageRouter.toSearchBleActivity(this, bundle)
            }//多个机器人
            else ->  {
                var bundle = Bundle()
                bundle.putBoolean(PageRouter.INTENT_KEY_HASDEVICES, false)
                bundle.putInt(PageRouter.INTENT_DATA_ENTER_TYPE, BusinessConstants.SEARCH_ENTER_TYPE_SEARCH_DEVICE)
                PageRouter.toSearchBleActivity(this, bundle)
            }
        }
        finish()
    }
}
