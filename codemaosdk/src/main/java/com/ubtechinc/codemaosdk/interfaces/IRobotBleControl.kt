package com.ubtechinc.codemaosdk.interfaces


import android.bluetooth.BluetoothGatt

import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.ubtechinc.codemaosdk.bean.UbtBleDevice

/**
 * Created by tanghongyu on 2018/3/10.
 */

interface IRobotBleControl {

    interface ConnectingResultCallback {
        fun onConnectFail()
        fun onConnected(ubtBleDevice: UbtBleDevice)
        fun onDisconnected()
    }

    interface ScanResultCallback {

        fun onScanStarted(boolean: Boolean)
        fun onScanning(device: UbtBleDevice?)
        fun onStop()
    }

    interface ScanAndConnectCallback {
        fun onScanStarted(success: Boolean)

        fun onScanFinished(scanResult: BleDevice)

        fun onScanning(bleDevice: BleDevice)

        fun onStartConnect()

        fun onConnectFail(exception: BleException)

        fun onConnectSuccess(bleDevice: BleDevice, gatt: BluetoothGatt, status: Int)

        fun onDisConnected(isActiveDisConnected: Boolean, device: BleDevice, gatt: BluetoothGatt, status: Int)
    }

    interface ControlCallback {

        fun onSuccess()
        fun onError(code: Int)

    }
}
