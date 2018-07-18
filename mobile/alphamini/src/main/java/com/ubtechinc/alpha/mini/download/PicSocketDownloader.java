package com.ubtechinc.alpha.mini.download;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.ubtech.utilcode.utils.JsonUtils;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.ThreadPoolUtils;
import com.ubtech.utilcode.utils.thread.ThreadPool;
import com.ubtechinc.alpha.GetRobotIp;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.AlphaMiniApplication;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;
import com.ubtrobot.downloader.DownloadInfo;
import com.ubtrobot.downloader.IDownloadTask;
import com.ubtrobot.downloader.callback.UbtDownloadCallback;
import com.ubtrobot.lib.communation.mobile.connection.Connection;
import com.ubtrobot.lib.communation.mobile.connection.SocketConnection;
import com.ubtrobot.lib.sync.download.downloader.SocketDownloader;
import com.ubtrobot.lib.sync.download.manager.DownloadManager;
import com.ubtrobot.socketdownloader.SocketDownloadManager;

import java.lang.reflect.Field;
import java.net.InetAddress;

/**
 * Created by ubt on 2018/4/2.
 */

public class PicSocketDownloader implements PicDownloader {

    public static final String TAG = "PicSocketDownloader";

    private boolean reachable = false;

    private long reachableTime = 0;

    private SocketDownloader downloader;

    private String port = "6000";

    private String ip = "";

    private String robotSsid;

    public PicSocketDownloader() {
        init();
    }

    private void init() {
        getRobotIp();
    }

    private void initDownloader() {
        try {
            LogUtils.d(TAG,"initDownloader");
            Connection connection = new SocketConnection(AlphaMiniApplication.getInstance(), null);
            connection.setRobotSn(MyRobotsLive.getInstance().getRobotUserId());
            downloader = DownloadManager.getInstance().createSocket(connection);
            Field connectField = SocketDownloadManager.class.getDeclaredField("mConnection");
            connectField.setAccessible(true);
            connectField.set(SocketDownloadManager.getInstance(), connection);
            downloader.setServerAddress(ip + ":" + port);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG,"initDownloader error " + e.getMessage());
        }
    }

    private void getRobotIp() {
        String robotId = MyRobotsLive.getInstance().getRobotUserId();
        GetRobotIp.GetIpRequest request = GetRobotIp.GetIpRequest.newBuilder().setPath("").build();
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_GET_ROBOT_IP_REQUEST, IMCmdId.IM_VERSION, request, robotId, new ICallback<GetRobotIp.GetIpResponse>() {
            @Override
            public void onSuccess(GetRobotIp.GetIpResponse data) {
                ip = data.getIp();
                robotSsid = data.getWifissid();
                LogUtils.d(TAG, "getRobotIp success ip=%s, ssid= %s", ip, robotSsid);
                if (robotSsid.equals(getSsid())) {
                    checkRobotReachable(new ReachableCallback() {
                        @Override
                        public void onReachable(boolean reachable) {
                            if (reachable) {
                                reachableTime = System.currentTimeMillis();
                                initDownloader();
                            }
                            PicSocketDownloader.this.reachable = reachable;
                        }
                    });
                } else {
                    reachable = false;
                    LogUtils.D(TAG, "robotSsid no equal mobile Ssid");
                }

            }

            @Override
            public void onError(ThrowableWrapper e) {
                reachable = false;
            }
        });
    }

    private void checkRobotReachable(final ReachableCallback callback) {
        ThreadPool.getInstance().submit(new ThreadPool.Job<Object>() {
            @Override
            public Object run(ThreadPool.JobContext jc) {
                try {
                    InetAddress inetAddress = InetAddress.getByName(ip);
                    boolean reachable = inetAddress.isReachable(500);
                    if(callback != null){
                        callback.onReachable(reachable);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if(callback != null){
                        callback.onReachable(false);
                    }
                }
                return false;
            }
        });

    }

    private String getSsid() {
        WifiManager wifiManager = (WifiManager) AlphaMiniApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssId = null;
        if (wifiInfo != null) {
            ssId = wifiInfo.getSSID();
        }
        LogUtils.d(TAG, "getMobile ssid = %s", ssId);
        return ssId;
    }

    @Override
    public boolean download(final DownloadRequest downloadRequest, final PicDownloadManager.PicDownloadCallback callback) {
        final long now = System.currentTimeMillis();
        if (now - reachableTime > 30000) {
            checkRobotReachable(new ReachableCallback() {
                @Override
                public void onReachable(boolean reachable) {
                    PicSocketDownloader.this.reachable = reachable;
                    if(downloader == null && reachable){
                        initDownloader();
                    }
                    startDownload(downloadRequest,callback);
                }
            });
        }else{
            startDownload(downloadRequest,callback);
        }
        return reachable;
    }

    private void startDownload(final DownloadRequest downloadRequest, final PicDownloadManager.PicDownloadCallback callback){
        LogUtils.d(TAG, "download reachable = ", reachable);
        if (reachable) {
            final long now = System.currentTimeMillis();
            reachableTime = now;
            IDownloadTask task = downloader.createTask(downloadRequest.getSocketRequest());
            task.setCallback(new UbtDownloadCallback() {
                @Override
                public void pending(DownloadInfo downloadInfo, long l, long l1) {
                    Log.i(TAG, "pending: downloadInfo " + downloadInfo);
                }

                @Override
                public void progress(DownloadInfo downloadInfo, long l, long l1) {
                    Log.i(TAG, "progress currentLength : " + l + "totalLength: " + l1);
                    float percent = (float) ((double)l / (double)l1);
                    Log.i(TAG, "progress: percent " + percent);
                    if (callback != null) {
                        callback.onProgress(downloadRequest.getName(), (int) (percent * 100));
                    }
                    long now = System.currentTimeMillis();
                    reachableTime = now;
                }

                @Override
                public void completed(DownloadInfo downloadInfo) {
                    if (callback != null) {
                        callback.onSuccess(downloadRequest.getName());
                    }
                    long now = System.currentTimeMillis();
                    reachableTime = now;
                }

                @Override
                public void paused(DownloadInfo downloadInfo, long l, long l1) {
                    Log.i(TAG, "paused: downloadInfo " + downloadInfo);
                }

                @Override
                public void cancelled(DownloadInfo downloadInfo) {
                    Log.i(TAG, "cancelled: downloadInfo " + downloadInfo);
                }

                @Override
                public void error(DownloadInfo downloadInfo, Throwable throwable) {
                    if (callback != null) {
                        callback.onFail(downloadRequest.getName(),ERROR_UNREACHABLE);
                    }
                    reachable = false;
                    Log.i(TAG, "error: downloadInfo" + downloadInfo + " throwable : "+ throwable);
                }

                @Override
                public void warn(DownloadInfo downloadInfo, String s) {
                    Log.i(TAG, "error: warn" + downloadInfo + " String : "+  s);
                }
            });
            downloader.start(task);
        } else {
            if (callback != null) {
                callback.onFail(downloadRequest.getName(),ERROR_UNREACHABLE);
            }
        }
    }

    @Override
    public void release() {
        reachableTime = 0;
    }

    public interface ReachableCallback{
        public void onReachable(boolean reachable);
    }
}
