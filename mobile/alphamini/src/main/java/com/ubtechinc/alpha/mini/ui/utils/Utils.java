package com.ubtechinc.alpha.mini.ui.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.ubtech.utilcode.utils.constant.MemoryConstant;
import com.ubtechinc.alpha.mini.ui.bluetooth.bean.UbtScanResult;
import com.ubtechinc.bluetooth.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author：wululin
 * @date：2017/10/20 15:06
 * @modifier：ubt
 * @modify_date：2017/10/20 15:06
 * [A brief description]
 * version
 */

public class Utils {
    private static long lastClickTime = 0;
    private static long DIFF = 1000;
    private static int lastButtonId = -1;
    private static final String TAG = "Utils";
    public static String pactkCommandToRobot(int command){
        JSONObject sendJsonObj = new JSONObject();
        try {
            sendJsonObj.put(Constants.DATA_COMMAND, command);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sendJsonObj.toString();
    }
    public static int getScreenHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    public static boolean isEmpty(CharSequence input) {
        if (input == null || "".equals(input) || "null".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }


    /**
     * 组装向机器人发送clinetId的指令
     * @param clientId
     * @return
     */
    public static String pactkClientIdCommandToRobot(String clientId){
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.DATA_COMMAND,Constants.CLIENT_ID_TRANS);
            jsonObject.put(Constants.CLIENTID,clientId);
            return jsonObject.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }



    /**
     * 校验wifi信息是否合法
     */
    public static boolean comfirmWifiInfoValidate(String ssidName,String password,String cap) {
        if (TextUtils.isEmpty(cap)){
            Logger.d(TAG, "ssid : " + ssidName + "'s capName is null!!!!!!!") ;
            return false;
        }
        int Type;
        if (cap.contains("WEP")) {
            Type = 2;
        } else if (cap.contains("WPA2")) {
            Type = 4;
        } else if (cap.contains("WPA")) {
            Type = 3;
        } else {
            Type = 1; //加密类型无需密码
        }
        if (Type != 1){
            return !(password.length() < 8);
        }else{
            return !(password.length() != 0);
        }
    }

    public static void sortList(List<UbtScanResult> mList){
        Collections.sort(mList, new Comparator<UbtScanResult>() {
            @Override
            public int compare(UbtScanResult scanResult, UbtScanResult t1) {
                return new Integer(t1.getL()).compareTo(new Integer(scanResult.getL()));
            }
        });
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.isAvailable()) {
            return true;
        }
        return false;
    }

    public static boolean isWifiAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    //判断wifi是否有密码
    public static boolean isWifiHasPwd(String capabilities){
            return (!capabilities.contains("WPA")
            && !capabilities.contains("wpa")
            && !capabilities.contains("WEP")
            && !capabilities.contains("wep")
            && !capabilities.contains("EAP"));
    }

    public static  String getWifiCap(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String ssid = info.getSSID();
        List<ScanResult> list = wifiManager.getScanResults();
        String capabilities = "WPA/WPA2";
        for (ScanResult scResult : list) {

            if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(ssid)) {
                capabilities = scResult.capabilities;
                Log.i("river","capabilities=" + capabilities);
            }
        }
        return capabilities;
    }

    public static String getSSID(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String ssid = info.getSSID();
        return ssid;
    }

    /**
     * 判断两次点击的间隔，如果小于1000，则认为是多次无效点击
     *
     * @return
     */
    public static boolean isFastDoubleClick() {
        return isFastDoubleClick(-1, DIFF);
    }

    /**
     * 判断两次点击的间隔，如果小于1000，则认为是多次无效点击
     *
     * @return
     */
    public static boolean isFastDoubleClick(int buttonId) {
        return isFastDoubleClick(buttonId, DIFF);
    }

    /**
     * 判断两次点击的间隔，如果小于diff，则认为是多次无效点击
     *
     * @param diff
     * @return
     */
    public static boolean isFastDoubleClick(int buttonId, long diff) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (lastButtonId == buttonId && lastClickTime > 0 && timeD < diff) {
            Log.v("isFastDoubleClick", "短时间内按钮多次触发");
            return true;
        }
        lastClickTime = time;
        lastButtonId = buttonId;
        return false;
    }



    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    /**
     * 跳转到权限设置界面
     */
    public static  void getAppDetailSettingIntent(Context context){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(Build.VERSION.SDK_INT >= 9){
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if(Build.VERSION.SDK_INT <= 8){
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(intent);
    }

    public static void saveImageToAblum(Context context, File file) {



         /*将数据插入多媒体数据库中，相册会自动刷新界面显示新增的相册*/
        ContentValues localContentValues = new ContentValues();

        localContentValues.put("_data", file.toString());

        localContentValues.put("mime_type", "image/jpeg");

        ContentResolver localContentResolver = context.getContentResolver();

        Uri localUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        localContentResolver.insert(localUri, localContentValues);
    }


    public static String byteNumerToMemerySize(double size){
        if (size < 0) {
            return "shouldn't be less than zero!";
        } else if (size < MemoryConstant.KB) {
            return String.format("%.0fB",  size);
        } else if (size < MemoryConstant.MB) {
            return String.format("%.0fKB", size / MemoryConstant.KB);
        } else if (size < MemoryConstant.GB) {
            return String.format("%.2fMB", size / MemoryConstant.MB);
        } else {
            return String.format("%.2fGB", size / MemoryConstant.GB);
        }
    }



}
