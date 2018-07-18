package com.ubtechinc.alpha.mini.ui.codemao

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ubtechinc.alpha.mini.R
import com.ubtechinc.alpha.mini.common.AnalysisClickListener
import com.ubtechinc.alpha.mini.common.BaseActivity
import com.ubtechinc.alpha.mini.ui.PageRouter
import com.ubtechinc.alpha.mini.ui.bluetooth.BleAutoConnectActivity
import com.ubtechinc.bluetooth.UbtBluetoothManager
import com.ubtechinc.bluetooth.utils.UbtBluetoothHelper
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission

/**
 * @author: tanghongyu
 * @date: 2018/5/31
 * @modifier:
 * @modify_date:
 * @description: 提醒绑定机器人
 */
class RemindBindRobotActivity : BaseActivity() {


    private var mIsJumpBanding: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remind_bind_robot)
        initView()
        initListener()
    }

    override fun setStatuesBar() {
    }

    private fun initListener() {
    }

    private fun initView() {
        findViewById<TextView>(R.id.tv_start_bind).setOnClickListener(mOnClickListener)
        findViewById<ImageView>(R.id.iv_close).setOnClickListener(mOnClickListener)

    }


    internal var mOnClickListener: AnalysisClickListener = object : AnalysisClickListener() {
        override fun onClick(view: View, reported: Boolean) {
            when (view.id) {
                R.id.iv_close -> finish()
                R.id.tv_start_bind -> {
                    UbtBluetoothManager.getInstance().isChangeWifi = false
                    UbtBluetoothManager.getInstance().isFromHome = false
                    UbtBluetoothManager.getInstance().isFromCodeMao = true
//                    StoreActivityNamesBeforeBinding.getInstance().addActivityName(this@RemindBindRobotActivity.javaClass.simpleName)
                    if ((UbtBluetoothManager.getInstance().isOpenBluetooth && AndPermission.hasPermission(this@RemindBindRobotActivity, *Permission.LOCATION))) {
                        if (UbtBluetoothHelper.getInstance().scanEnd()) {
                            jumpToBinding()
                        } else {
                            showLoadingDialog()
                            mIsJumpBanding = true
                        }
                    } else {
                        PageRouter.toOpenBluetooth(this@RemindBindRobotActivity)
                    }
                }


            }
        }
    }


    override fun onResume() {
        super.onResume()
        // 开始搜索机器人
        if (UbtBluetoothManager.getInstance().isOpenBluetooth && AndPermission.hasPermission(this, *Permission.LOCATION)) {
            UbtBluetoothHelper.getInstance().startScan("")
            UbtBluetoothHelper.getInstance().setBluetoothScanListener {
                if (mIsJumpBanding) {
                    dismissDialog()
                    jumpToBinding()
                }
            }


        }
    }


    override fun onPause() {
        super.onPause()
        UbtBluetoothHelper.getInstance().stopScan()
    }

    private fun jumpToBinding() {
        Log.d(BaseActivity.TAG, " jumpToBinding ")
        mIsJumpBanding = false
        when {
            UbtBluetoothHelper.getInstance().purposeDevice != null -> {


                val intent = Intent(this, BleAutoConnectActivity::class.java)

                intent.putExtra(CodingBleHelper.KEY_UBTBLUETOOTH, UbtBluetoothHelper.getInstance().purposeDevice)
                startActivity(intent)
            }
            UbtBluetoothHelper.getInstance().hasDevices() -> PageRouter.toBleAutoConnectActivity(this)
            else -> PageRouter.toOpenPowerTip(this)
        }
    }
}
