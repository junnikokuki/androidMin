package com.ubtechinc.alpha.mini.push;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.ubtechinc.alpha.mini.entity.Message;
import com.ubtechinc.alpha.mini.utils.PushMessageUtils;
import com.ubtechinc.nets.utils.JsonUtil;
import com.ubtect.xingepushlibrary.IReciverXinGeMsgCallBack;
import com.ubtect.xingepushlibrary.UbtXinGeManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2017/11/3.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class AlphaMiniPushManager implements IReciverXinGeMsgCallBack{

    private UbtXinGeManager mPushManager;

    private AlphaMiniPushConfig mConfig;

    private List<IMiniMsgCallback> messageListeners = new ArrayList<IMiniMsgCallback>();

    private AlphaMiniPushManager(){
        mPushManager = UbtXinGeManager.getInstance();
    }


    private static class AlphaMiniPushManagerHolder{
        public static AlphaMiniPushManager instance = new AlphaMiniPushManager();
    }

    public static AlphaMiniPushManager getInstance(){
        return AlphaMiniPushManagerHolder.instance;
    }

    public void initPush(Context context){
        mPushManager.init(context);
    }

    public void setPushConfig(AlphaMiniPushConfig config){
        this.mConfig = config;
    }

    public void registerPush(){
        if (mPushManager.hasAppinfoCache()){
            mPushManager.fetchAppInfoFromLocal(mConfig.getUserId(), mConfig.getToken());
        }else{
            mPushManager.fetchAppInfoFromServer(mConfig.getUserId(), mConfig.getToken());
        }
        mPushManager.registerMessageListener(this);
    }

    public void unRegisterPush(){
        mPushManager.unbindToken(mConfig.getUserId(), mConfig.getToken());
        mPushManager.unRegisterMessageListener();
    }

    public void addMessageListener(IMiniMsgCallback callBack){
        synchronized (messageListeners){
            messageListeners.add(callBack);
        }
    }

    public void removeMessageListener(IMiniMsgCallback callBack){
        synchronized (messageListeners){
            messageListeners.remove(callBack);
        }
    }

    @Override
    public void onMessageRevier(String message) {
        Message msg = null;
        String commandId = null;//2003---2009
        try {
            JSONObject object = new JSONObject(message);
            String bodyData = object.getString("bodyData");
            msg = PushMessageUtils.parserMsgBody(bodyData);
            String header = object.getString("header");
            commandId = PushMessageUtils.parserMsgHeader(header);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (msg == null || commandId == null){
            Logger.e("推送消息格式错误!");
            return;
        }

        for (IMiniMsgCallback callback: messageListeners) {
            callback.onReceiverMsg(commandId, msg);
        }
    }



}
