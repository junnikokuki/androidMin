package com.ubtechinc.alpha.mini.download;

import android.util.Log;

import com.ubtechinc.alpha.AlphaMessageOuterClass;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.im.business.ReceiveMessageBussinesss;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;
import com.ubtrobot.common.utils.XLog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ubt on 2018/4/2.
 */

public class PicImDownloader implements PicDownloader {

    private Map<String, List<WeakReference<PicDownloadManager.PicDownloadCallback>>> downloadCallbacks = new HashMap<>();
    private Map<String, List<PicDownloadManager.PicDownloadCallback>> downloadOriginCallbacks = new HashMap<>();
    private Map<String, List<WeakReference<PicDownloadManager.PicDownloadCallback>>> downloadHDPicCallbacks = new HashMap<>();

    private ReceiveMessageBussinesss.DownloadListener downloadListener = new ReceiveMessageBussinesss.DownloadListener() {
        @Override
        public void onSuccess(String fileName) {
            List<WeakReference<PicDownloadManager.PicDownloadCallback>> listeners = downloadCallbacks.get(fileName);
            if (listeners != null) {
                for (WeakReference<PicDownloadManager.PicDownloadCallback> picDownloadCallbackWeakReference : listeners) {
                    PicDownloadManager.PicDownloadCallback callback = picDownloadCallbackWeakReference.get();
                    if (callback != null) {
                        callback.onSuccess(fileName);
                    }
                }
                downloadCallbacks.remove(fileName);
            }
        }

        @Override
        public void onError(String fileName, int i) {
            List<WeakReference<PicDownloadManager.PicDownloadCallback>> listeners = downloadCallbacks.get(fileName);
            if (listeners != null) {
                for (WeakReference<PicDownloadManager.PicDownloadCallback> picDownloadCallbackWeakReference : listeners) {
                    PicDownloadManager.PicDownloadCallback callback = picDownloadCallbackWeakReference.get();
                    if (callback != null) {
                        callback.onFail(fileName,i);
                    }
                }
                downloadCallbacks.remove(fileName);
            }
        }
    };


    private ReceiveMessageBussinesss.DownloadOriginListener downloadOriginListener = new ReceiveMessageBussinesss.DownloadOriginListener() {

        @Override
        public void onProgress(String fileName, int progress) {
            Log.i("IM通道", "onProgress: fileName " + fileName + " progress : " + progress);
            List<PicDownloadManager.PicDownloadCallback> callbacks = downloadOriginCallbacks.get(fileName);
            if (callbacks != null){
                for (PicDownloadManager.PicDownloadCallback picDownloadCallbackWeakReference : downloadOriginCallbacks.get(fileName)) {
                    PicDownloadManager.PicDownloadCallback callback = picDownloadCallbackWeakReference;
                    if (callback != null) {
                        callback.onProgress(fileName, progress);
                    }
                }
            }
        }

        @Override
        public void onORiginSuccess(String fileName, String url) {
            Log.i("IM通道", "onORiginSuccess: fileName " + fileName );
            List<PicDownloadManager.PicDownloadCallback> callbacks = downloadOriginCallbacks.get(fileName);
            if (callbacks != null){
                for (PicDownloadManager.PicDownloadCallback picDownloadCallbackWeakReference : downloadOriginCallbacks.get(fileName)) {
                    PicDownloadManager.PicDownloadCallback callback = picDownloadCallbackWeakReference;
                    if (callback != null) {
                        callback.onSuccess(fileName);
                    }
                    downloadOriginCallbacks.remove(fileName);
                }
            }
        }

        @Override
        public void onORiginError(String fileName, Throwable e) {
            Log.e("IM通道", "onORiginError:  filename" + fileName);
            Log.e("IM通道", "Throwable : " + e);
            List<PicDownloadManager.PicDownloadCallback> listeners = downloadOriginCallbacks.get(fileName);
            if (listeners != null) {
                for (PicDownloadManager.PicDownloadCallback picDownloadCallbackWeakReference : listeners) {
                    PicDownloadManager.PicDownloadCallback callback = picDownloadCallbackWeakReference;
                    if (callback != null) {
                        callback.onFail(fileName, 0);
                    }
                }
                downloadOriginCallbacks.remove(fileName);
            }
        }

    };

    private ReceiveMessageBussinesss.DownloadHDPicListener downloadHDPicListener = new ReceiveMessageBussinesss.DownloadHDPicListener() {

        @Override
        public void onHDSuccess(String fileName) {
            List<WeakReference<PicDownloadManager.PicDownloadCallback>> listeners = downloadHDPicCallbacks.get(fileName);
            if (listeners != null) {
                for (WeakReference<PicDownloadManager.PicDownloadCallback> picDownloadCallbackWeakReference : listeners) {
                    PicDownloadManager.PicDownloadCallback callback = picDownloadCallbackWeakReference.get();
                    if (callback != null) {
                        callback.onSuccess(fileName);
                    }
                }
                downloadHDPicCallbacks.remove(fileName);
            }
        }

        @Override
        public void onHDError(String fileName, int i) {
            List<WeakReference<PicDownloadManager.PicDownloadCallback>> listeners = downloadHDPicCallbacks.get(fileName);
            if (listeners != null) {
                for (WeakReference<PicDownloadManager.PicDownloadCallback> picDownloadCallbackWeakReference : listeners) {
                    PicDownloadManager.PicDownloadCallback callback = picDownloadCallbackWeakReference.get();
                    if (callback != null) {
                        callback.onFail(fileName,i);
                    }
                }
                downloadHDPicCallbacks.remove(fileName);
            }
        }
    };

    public PicImDownloader(){
        init();
    }

    private void init() {
        ReceiveMessageBussinesss.getInstance().setDownloadListener(downloadListener);
        ReceiveMessageBussinesss.getInstance().setDownloadOriginListener(downloadOriginListener);
        ReceiveMessageBussinesss.getInstance().setDownloadHDPicListener(downloadHDPicListener);
    }

    public void release() {
        ReceiveMessageBussinesss.getInstance().removeDownloadListener(downloadListener);
        ReceiveMessageBussinesss.getInstance().removeDownloadOriginListener(downloadOriginListener);
        ReceiveMessageBussinesss.getInstance().removeDownloadHDPicListener(downloadHDPicListener);
        downloadCallbacks.clear();
        downloadHDPicCallbacks.clear();
        downloadOriginCallbacks.clear();
    }

    @Override
    public boolean download(final DownloadRequest downloadRequest, final PicDownloadManager.PicDownloadCallback callback) {
        addCallback(downloadRequest, callback);
        Phone2RobotMsgMgr.getInstance().sendData(IMCmdId.IM_GET_MOBILE_ALBUM_DOWNLOAD_REQUEST,
                IMCmdId.IM_VERSION, downloadRequest.getImRequest(),
                downloadRequest.getRobotUserId(), new ICallback<AlphaMessageOuterClass.AlphaMessage>() {
                    @Override
                    public void onSuccess(AlphaMessageOuterClass.AlphaMessage data) {
                        XLog.d("xxxxxxx", "download success");
                    }

                    @Override
                    public void onError(ThrowableWrapper e) {
                        XLog.d("xxxxxxx", "download onError" + e.getMessage());
                        if (callback == null){
                            callback.onFail(downloadRequest.getName(), e.getErrorCode());
                        }
                    }
                });
        return false;
    }

    private void addCallback(DownloadRequest downloadRequest, PicDownloadManager.PicDownloadCallback callback) {
        switch (downloadRequest.getLevel()) {
            case 0:
                if (callback != null) {
                    List<PicDownloadManager.PicDownloadCallback> listeners = downloadOriginCallbacks.get(downloadRequest.getName());
                    if(listeners == null){
                        listeners = new ArrayList<>();
                        downloadOriginCallbacks.put(downloadRequest.getName(),listeners);
                    }
                    listeners.add(callback);
                }
                break;
            case 1:
                if (callback != null) {
                    List<WeakReference<PicDownloadManager.PicDownloadCallback>> listeners = downloadCallbacks.get(downloadRequest.getName());
                    if(listeners == null){
                        listeners = new ArrayList<>();
                        downloadCallbacks.put(downloadRequest.getName(),listeners);
                    }
                    listeners.add(new WeakReference<PicDownloadManager.PicDownloadCallback>(callback));
                }
                break;
            case 2:
                if (callback != null) {
                    List<WeakReference<PicDownloadManager.PicDownloadCallback>> listeners = downloadHDPicCallbacks.get(downloadRequest.getName());
                    if(listeners == null){
                        listeners = new ArrayList<>();
                        downloadHDPicCallbacks.put(downloadRequest.getName(),listeners);
                    }
                    listeners.add(new WeakReference<PicDownloadManager.PicDownloadCallback>(callback));
                }
                break;
            default:
                break;
        }
    }
}
