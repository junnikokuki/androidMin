package com.ubtechinc.alpha.mini.avatar.rtc.ilive;

import android.util.Log;

import com.ubtechinc.alpha.mini.avatar.TimerManager;
import com.ubtechinc.alpha.mini.avatar.widget.ILiveSdkRootView;

import java.util.TimerTask;

public class ConnectionManager {

    public static final String TAG = "ConnectionManager";

    public static final int NORMAL = 0;
    public static final int INTERRUPTED = 1;
    public static final int LOST = 2;

    private int reconnectTimeout = 10000; //10秒
    private int interruptTimeout = 4000; //2秒

    private static ConnectionManager instance;

    private ConnectionListener connectionListener;

    private ILiveSdkRootView videoView;

    private TimerTask disConnectTask;
    private TimerTask interruptedTask;

    private int status = NORMAL;

    private int frameCount = 0;
    private int firstFrameCount = 0;

    private boolean checkFirstFrame;

    public static ConnectionManager getInstance() {
        if (instance == null) {
            synchronized (ConnectionManager.class) {
                if (instance == null) {
                    instance = new ConnectionManager();
                }
            }
        }
        return instance;
    }

    private ConnectionManager() {

    }

    public void start(ILiveSdkRootView videoView){
        status = NORMAL;
        checkFirstFrame = true;
        setVideoView(videoView);
    }

    public void setVideoView(ILiveSdkRootView videoView) {
        this.videoView = videoView;
        this.videoView.setOnFrameReceiveListener(new ILiveSdkRootView.OnFrameReceiveListener() {
            @Override
            public void onFrameReceive() {
                if (status == LOST) {
                    destroy();
                }
                if (status == INTERRUPTED) { //处于断网状态得到帧重连成功
                    frameCount++;
                    if (frameCount > 3) {
                        Log.d(TAG, "onReconnectSuccess");
                        status = NORMAL;
                        frameCount = 0;
                        stopDisConnectTask();
                        if (connectionListener != null) {
                            connectionListener.onReconnectSuccess();
                        }
                    }
                } else {
                    if(checkFirstFrame){
                        firstFrameCount++;
                        if(firstFrameCount >5){
                            checkFirstFrame = false;
                            firstFrameCount = 0;
                            if(connectionListener != null){
                                connectionListener.onFirstFrameReceive();
                            }
                        }
                    }
                    startInterruptedTask(); //收到启动断网检测定时器
                }
            }
        });
    }

    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    public interface ConnectionListener {

        public void onConnectionInterrupted();

        public void onConnectionLost();

        public void onReconnectSuccess();

        public void onFirstFrameReceive();
    }

    public void startDisconnectTask() {
        Log.d(TAG, "startDisconnectTask");
        stopDisConnectTask();
        disConnectTask = new TimerTask() {
            @Override
            public void run() {
                status = LOST;
                destroy();
                Log.d(TAG, "onConnectionLost");
                if (connectionListener != null) {
                    connectionListener.onConnectionLost();
                }
            }
        };
        TimerManager.getInstance().schedule(disConnectTask, reconnectTimeout);

    }

    public void startInterruptedTask() {
        stopInterruptedTask(); //先取消之前的定时器
//        Log.d(TAG, "startInterruptedTask");
        interruptedTask = new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "onConnectionInterrupted");
                if (connectionListener != null) {
                    connectionListener.onConnectionInterrupted();
                }
                status = INTERRUPTED;
                startDisconnectTask();
            }
        };
        TimerManager.getInstance().schedule(interruptedTask, interruptTimeout);
    }

    public void stopDisConnectTask() {
        if (disConnectTask != null) {
            disConnectTask.cancel();
        }
    }

    public void stopInterruptedTask() {
        if (interruptedTask != null) {
            interruptedTask.cancel();
        }
    }

    public void destroy() {
        if (this.videoView != null) {
            this.videoView.setOnFrameReceiveListener(null);
        }
        stopDisConnectTask();
        stopInterruptedTask();
        instance = null;
    }
}
