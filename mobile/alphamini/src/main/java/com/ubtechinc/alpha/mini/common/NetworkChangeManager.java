package com.ubtechinc.alpha.mini.common;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;

import com.ubtechinc.alpha.mini.utils.Constants;

import java.util.ArrayList;

/**
 * Created by nixiaoyan on 2017/2/7.
 */

public class NetworkChangeManager {
    public static Application sApplication;
    private static final String TAG = "NetworkChangeManager";
    private static volatile NetworkChangeManager sInstance;

    private ArrayList<NetworkChangedListener> mListeners = new ArrayList<NetworkChangedListener>();

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//        //wifi开关监听
//        isOpenWifi(intent, mListeners);
            //是否连接wifi
            isConnectionWifi(intent, mListeners);
            //监听网络连接设置
            isConnection(intent, mListeners, context);
        }
    };

    private NetworkChangeManager(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        sApplication.registerReceiver(mReceiver, filter);
    }

    public static void install(Application application){
        if(sApplication != null) {
            throw new IllegalArgumentException("it has init");
        }
        sApplication = application;
    }

    public static NetworkChangeManager getInstance(){
        if(sApplication == null) {
            throw new IllegalArgumentException("not init");
        }
        if(sInstance == null) {
            synchronized (NetworkChangeManager.class){
                if(sInstance == null) {
                    sInstance = new NetworkChangeManager();
                }
            }
        }

        return sInstance;
    }
    /**
     * 监听wifi打开与关闭
     * （与连接与否无关）
     */
    private void isOpenWifi(Intent intent, ArrayList<NetworkChangedListener> netStateListeners)
    {
        // 这个监听wifi的打开与关闭，与wifi的连接无关
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (wifiState) {
                //Wifi关闭
                case WifiManager.WIFI_STATE_DISABLED:
                    Log.e(TAG, "wifiState:wifi关闭！" );
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    Log.e(TAG, "wifiState:wifi打开！" );
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 连接有用的wifi（有效无线路由）
     * WifiManager.WIFI_STATE_DISABLING与WIFI_STATE_DISABLED的时候，根本不会接到这个广播
     */
    private void isConnectionWifi(Intent intent,ArrayList<NetworkChangedListener> netStateListeners)
    {
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            Parcelable parcelableExtra = intent
                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != parcelableExtra) {
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                NetworkInfo.State state = networkInfo.getState();
                boolean isConnected = state == NetworkInfo.State.CONNECTED;
                //wifi连接
                if (isConnected) {
                    for(NetworkChangedListener networkChangedListener:netStateListeners)
                        networkChangedListener.onNetworkChanged(Constants.NetWork.IS_WIFI);
                    Log.e(TAG, "isConnected:isWifi:true");
                }
            }
        }
    }

    /**
     * 监听网络连接的设置，包括wifi和移动数据的打开和关闭。(推荐)
     * 这个广播的最大弊端是比上边两个广播的反应要慢，如果只是要监听wifi，我觉得还是用上边两个配合比较合适
     */
    private void isConnection(Intent intent, ArrayList<NetworkChangedListener> netStateListeners, Context context)
    {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
            if (activeNetwork != null) {
                // connected to the internet
                if (activeNetwork.isConnected()) {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        // connected to wifi
                        for(NetworkChangedListener networkChangedListener:netStateListeners)
                            networkChangedListener.onNetworkChanged(Constants.NetWork.IS_WIFI);
                        Log.e(TAG, "当前WiFi连接可用 ");
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        for(NetworkChangedListener networkChangedListener:netStateListeners)
                            networkChangedListener.onNetworkChanged(Constants.NetWork.IS_MOBILE);
                        Log.e(TAG, "当前移动网络连接可用 ");
                    }
                } else {
                    Log.e(TAG, "当前没有网络连接，请确保你已经 打开网络 ");
                    for(NetworkChangedListener networkChangedListener:netStateListeners)
                        networkChangedListener.onNetworkChanged(Constants.NetWork.NO_CONNECTION);
                }

            } else {   // not connected to the internet
                Log.e(TAG, "当前没有网络连接，请确保你已经打开网络 ");
                for(NetworkChangedListener networkChangedListener:netStateListeners)
                    networkChangedListener.onNetworkChanged(Constants.NetWork.NO_CONNECTION);
            }
        }
    }
    public void addListener(NetworkChangedListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(NetworkChangedListener listener) {
        mListeners.remove(listener);
    }

    public interface NetworkChangedListener{
        void onNetworkChanged(int type);
    }
}
