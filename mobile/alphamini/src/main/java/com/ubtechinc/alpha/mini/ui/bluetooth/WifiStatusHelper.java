package com.ubtechinc.alpha.mini.ui.bluetooth;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

/**
 * @desc : wifi状态辅助类
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2018/5/3
 */

public class WifiStatusHelper {

    private static final String TAG = "WifiStatusHelper";
    public static final String CAPABILITIES_WEP = "WEP";
    public static final String CAPABILITIES_WPA2 = "WPA2";
    public static final String CAPABILITIES_WPA = "WPA";
    public static final String CAPABILITIES_N = "N";

    public static String getSecure(String purpose, Context context) {
        Log.d(TAG, " getSecure -- purpose : " + purpose);
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> scanResults = wifiManager.getScanResults();
        for(ScanResult scanResult : scanResults) {
            if(scanResult.SSID.equals(purpose)) {
                String strSecure = scanResult.capabilities;
                if (strSecure.contains("WEP")) {
                    strSecure = "WEP";
                } else if (strSecure.contains("WPA2")) {
                    strSecure = "WPA2";
                } else if (strSecure.contains("WPA")) {
                    strSecure = "WPA";
                } else {
                    // 表示其他
                    strSecure = "N";
                }
                Log.d(TAG, " getSecure -- purpose : " + purpose + " strSecure : " + strSecure);
                return strSecure;
            }
        }
        return null;
    }

}

