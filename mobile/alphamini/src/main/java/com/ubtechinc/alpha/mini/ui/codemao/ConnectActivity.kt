package com.ubtechinc.alpha.mini.ui.codemao

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*

import com.google.common.collect.ImmutableMap
import com.ubtech.utilcode.utils.LogUtils
import com.ubtech.utilcode.utils.SPUtils
import com.ubtech.utilcode.utils.notification.NotificationCenter
import com.ubtechinc.alpha.mini.R
import com.ubtechinc.alpha.mini.common.BaseActivity
import com.ubtechinc.alpha.mini.event.BleEvent
import com.ubtechinc.alpha.mini.ui.PageRouter
import com.ubtechinc.codemaosdk.MiniApi
import com.ubtechinc.codemaosdk.bean.UbtBleDevice
import com.ubtechinc.codemaosdk.interfaces.IRobotBleControl
import com.ubtechinc.nets.utils.JsonUtil

import java.util.ArrayList

class ConnectActivity : BaseActivity() {
    private val TAG = "MainActivity"
    private var mScanBleBtn: Button? = null
    private var mDevicesLv: ListView? = null
    private val mCodeUrl = "CodeUrl"
    private val mCourseUrl = "CourseUrl"
    /**
     * 扫描到的机器人列表
     */
    private val robotScanResultList = ArrayList<UbtBleDevice>()
    private var mDevicesAdapter: DevicesAdapter? = null
    private var connectSN: String? = null
    private lateinit var edtCodeUrl: EditText
    private lateinit var edtCourseUrl: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)
        LogUtils.d(JsonUtil.object2Json(ImmutableMap.of("isSuccess", true)))
        mScanBleBtn = findViewById(R.id.scan_ble_btn)
        mDevicesLv = findViewById(R.id.devices_lv)
        edtCodeUrl = findViewById(R.id.et_code_url)
        edtCourseUrl = findViewById(R.id.et_course_url)
        findViewById<View>(R.id.bt_disconnect).setOnClickListener { MiniApi.get().disconnectRobot() }
        findViewById<View>(R.id.coding_btn).setOnClickListener {
            var bundle = Bundle()

            var url = edtCodeUrl.editableText.toString()
            if (!TextUtils.isEmpty(url)) {
                SPUtils.get().put(mCodeUrl, url)
            }
            bundle.putString(PageRouter.INTENT_KEY_WEB_URL, url)
            PageRouter.toCodingActivity(this, bundle)

        }


        findViewById<View>(R.id.bt_course).setOnClickListener {

            var bundle = Bundle()
            bundle.putString(PageRouter.INTENT_KEY_PAGE_TYPE, PageRouter.INTENT_DATA_COURSE)
            var url = edtCourseUrl.editableText.toString()
            if (!TextUtils.isEmpty(url)) {
                SPUtils.get().put(mCourseUrl, url)
            }
            bundle.putString(PageRouter.INTENT_KEY_WEB_URL, url)
            PageRouter.toCodingActivity(this, bundle)
        }

        mDevicesAdapter = DevicesAdapter(this, robotScanResultList)
        mDevicesLv!!.adapter = mDevicesAdapter
        MiniApi.get().init(application)

        mScanBleBtn!!.setOnClickListener {
            robotScanResultList.clear()
            mDevicesAdapter!!.notifyDataSetChanged()
            MiniApi.get().startScanRobot(object : IRobotBleControl.ScanResultCallback {
                override fun onScanStarted(boolean: Boolean) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onScanning(device: UbtBleDevice?) {
                    Log.d(TAG, "搜索成功=========")
                    robotScanResultList.add(device!!)
                    mDevicesAdapter!!.onNotifyDataSetChanged(robotScanResultList)
                }

                override fun onStop() {
                    Log.d(TAG, "搜索=========")
                    Toast.makeText(applicationContext, "搜索超时", Toast.LENGTH_LONG).show()
                }
            })
        }

        findViewById<View>(R.id.stop_scan_btn).setOnClickListener { MiniApi.get().stopScanRobot() }
        mDevicesLv!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            showLoadingDialog()

            val connectDevice = robotScanResultList[position]
            connectSN = connectDevice.sn
            robotScanResultList.remove(connectDevice)
            robotScanResultList.add(0, connectDevice)
            mDevicesAdapter!!.setConnecting(connectSN)
            Log.d(TAG, "connectSN==" + connectSN!!)
            MiniApi.get().connectRobot(connectDevice, object : IRobotBleControl.ConnectingResultCallback {
                override fun onConnected(ubtBleDevice: UbtBleDevice) {
                    dismissDialog()


                    val intent = Intent(applicationContext, CodeMaoActivity::class.java)
                    var url = edtCodeUrl.editableText.toString()
                    if (!TextUtils.isEmpty(url)) {
                        SPUtils.get().put("cm_url", url)
                    }
                    intent.putExtra("url", url)
                    startActivity(intent)
                }

                override fun onConnectFail() {
                    dismissDialog()
                }


                override fun onDisconnected() {
                    dismissDialog()
                    Toast.makeText(applicationContext, "Robot Disconnect !!!!", Toast.LENGTH_LONG).show()
                    NotificationCenter.defaultCenter().publish(BleEvent(false))
                }

            })
        }
        var codeUrl = SPUtils.get().getString(mCodeUrl)
        var courseUrl = SPUtils.get().getString(mCourseUrl)
        if (!TextUtils.isEmpty(codeUrl)) {
            edtCodeUrl.setText(codeUrl)
        }
        if (!TextUtils.isEmpty(courseUrl)) {
            edtCourseUrl.setText(courseUrl)
        }
    }


}
