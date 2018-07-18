package com.clj.fastble.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;


/**
 * @author：wululin
 * @date：2017/10/19 16:29
 * @modifier：ubt
 * @modify_date：2017/10/19 16:29
 * [A brief description]
 * version
 */

public class BleUtil {
    private static final String TAG = BleUtil.class.getSimpleName();
    private static final String uuid_seed="minirobot";
    private static final int SEND_WIFI_LIST_SIZE = 5; //分批发送WiFi列表的个数
    public static String getUUID(){
        UUID uuid = UUID.nameUUIDFromBytes(uuid_seed.getBytes()); //07303e62-cb70-38d8-8f5c-40c062145442
        Log.d(TAG, "Robot uuid :" + uuid.toString());
        return uuid.toString();
    }

    public static boolean isHexWepKey(String wepKey) {
        final int len = wepKey.length();
        // WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }

        return isHex(wepKey);
    }

    public static boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f')) {
                return false;
            }
        }

        return true;
    }

    /**
     * 过滤ssid的wifi
     * @return
     */
    public static List<ScanResult> filterResultList(List<ScanResult> results){

        List<ScanResult> mWifiList = new ArrayList();
        for(ScanResult result : results){
            if (result.SSID == null || result.SSID.length() == 0 || result.capabilities.contains("[IBSS]")) {
                continue;
            }
            boolean found = false;
            for(ScanResult item:mWifiList){
                if(item.SSID.equals(result.SSID)&&item.capabilities.equals(result.capabilities)){
                    found = true;break;
                }
            }
            if(!found){
                mWifiList.add(result);
            }
        }

        Collections.sort(mWifiList, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult scanResult, ScanResult t1) {
                return new Integer(scanResult.level).compareTo(new Integer(t1.level));
            }
        });

        return mWifiList;
    }



    public static List<ScanResult> deleteSameSSID(List<ScanResult> list1,List<ScanResult> list2){
        List<ScanResult> scanResults = new ArrayList<>();
        for (ScanResult result: list1){
            String ssid1 = result.SSID;
            boolean isHas = false;
            for (ScanResult result1: list2){
                String ssid2 = result1.SSID;
                if(ssid1.equals(ssid2)){
                    isHas = true;
                }
            }
            if(!isHas){
                scanResults.add(result) ;
            }
        }
        return scanResults;
    }


    public static void openBle(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled()){
            bluetoothAdapter.enable();
        }
    }

    public static boolean isOpenBle(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.isEnabled();
    }

    public static void closeBle(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter.isEnabled()){
            bluetoothAdapter.disable();
        }
    }
}
