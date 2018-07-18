package com.ubtechinc.alpha.mini.im;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.ubtech.utilcode.utils.notification.NotificationCenter;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.event.RobotImResponseEvent;
import com.ubtechinc.nets.phonerobotcommunite.ITimeoutEngine;

import java.util.HashMap;
import java.util.Map;

/**
 * Im 超时实现类
 * Created by ubt on 2017/12/2.
 */

public class ImTimeoutEngine extends ITimeoutEngine {

    private static ImTimeoutEngine instance;

    public static final long TIME_OUT = 15000;

    private HandlerThread timerThread;
    private Handler timerHandler;
    private OnlineConfirmHelper onlineConfirmHelper;

    private Map<Integer,String> request2RobotIdMap = new HashMap<>();

    public static ImTimeoutEngine get() {
        if (instance == null) {
            synchronized (ImTimeoutEngine.class) {
                if (instance == null) {
                    instance = new ImTimeoutEngine();
                }
            }
        }
        return instance;
    }

    private ImTimeoutEngine() {
        timerThread = new HandlerThread("im_timeout_thread");
        timerThread.start();
        timerHandler = new Handler(timerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Long requestId = Long.valueOf(msg.what);
                int cmId = msg.arg1;
                String peer = (String) msg.obj;
                onTimeout(requestId);
                if(cmId != IMCmdId.IM_CONFIRM_ONLINE_REQUEST){
                    if(onlineConfirmHelper == null){
                        onlineConfirmHelper = new OnlineConfirmHelper();
                    }
                    onlineConfirmHelper.testOnline(peer);
                }
            }
        };
    }

    @Override
    public void onSetTimeout(String peer,int cmId, Long requestId) {
        Message msg = Message.obtain();
        msg.what = requestId.intValue();
        msg.arg1 = cmId;
        msg.obj = peer;
        request2RobotIdMap.put(msg.what,peer);
        timerHandler.sendMessageDelayed(msg, TIME_OUT);
    }

    @Override
    public void onCancelTimeout(Long requestId) {
        int what = requestId.intValue();
        if (timerHandler.hasMessages(what)) {
            timerHandler.removeMessages(what);
            if(onlineConfirmHelper!=null){
                onlineConfirmHelper.stop();
            }
            String robotId = request2RobotIdMap.get(what);
            if (robotId != null) {
                NotificationCenter.defaultCenter().publish(new RobotImResponseEvent(robotId));
            }
        }
        if(request2RobotIdMap.containsKey(what)){
            request2RobotIdMap.remove(what);
        }

    }

    @Override
    public void release() {
        super.release();
        if (timerThread != null) {
            timerThread.quit();
        }
        request2RobotIdMap.clear();
        instance = null;
    }


}
