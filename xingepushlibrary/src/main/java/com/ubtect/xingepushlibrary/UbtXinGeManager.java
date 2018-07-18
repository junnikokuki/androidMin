package com.ubtect.xingepushlibrary;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.ubtech.utilcode.utils.AppUtils;
import com.ubtech.utilcode.utils.SPUtils;
import com.ubtect.xingepushlibrary.request.ObtainAppInfoRequest;
import com.ubtect.xingepushlibrary.request.BindTokenRequest;
import com.ubtect.xingepushlibrary.request.UnbindTokenRequest;


/**
 * @Date: 2017/10/31.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class UbtXinGeManager {
    private String TAG = getClass().getSimpleName();

    public static int retryCount = 1;
    private Context context;
    private SPUtils mSPutils;
    private ObtainAppInfoRequest obtainAppInfoRequest;
    private BindTokenRequest mBindTokenRequest;
    private UnbindTokenRequest mUnbindTokenRequest;
    private IReciverXinGeMsgCallBack callback;
    private AppInfo mAppInfo;


    private static class UbtXinGeManagerHolder {
        public static UbtXinGeManager instance = new UbtXinGeManager();
    }

    public static UbtXinGeManager getInstance(){
        return UbtXinGeManagerHolder.instance;
    }

    private UbtXinGeManager(){

    }

    public void init(Context context){
        this.context = context;
        obtainAppInfoRequest = new ObtainAppInfoRequest(context);
        mBindTokenRequest = new BindTokenRequest(context);
        mUnbindTokenRequest = new UnbindTokenRequest(context);
        mSPutils = new SPUtils(UbtXinGeConstant.APPNAME);
    }

    public void registerMessageListener(IReciverXinGeMsgCallBack callback){
        this.callback = callback;
    }

    public void unRegisterMessageListener(){
        callback = null;
    }

    void onReceverMessage(String message){
        if (callback != null)
            callback.onMessageRevier(message);
    }

    public boolean hasAppinfoCache(){
        return mSPutils.getBoolean(UbtXinGeConstant.CaeheAppInfo);
    }


    public void fetchAppInfoFromServer(final String usrId, final String token){
        obtainAppInfoRequest.obtainAppInfoFromServer(token, new ObtainAppInfoRequest.ObtainAppInfoCallBack() {
            @Override
            public void onSuccess(AppInfo appInfo) {
                Log.i(TAG, "fetch AppInfo : " + appInfo);
                mAppInfo = appInfo;
                mSPutils.putObject(mAppInfo);
                mSPutils.put(UbtXinGeConstant.CaeheAppInfo, true);
                registerXinGe(usrId, token);
            }

            @Override
            public void onError() {

            }
        });
    }

    public void fetchAppInfoFromLocal(final String userId, final String token){
        mAppInfo = (AppInfo) mSPutils.getObject(AppInfo.class);
        if (mAppInfo == null || TextUtils.isEmpty(mAppInfo.getAccessId()) || TextUtils.isEmpty(mAppInfo.getAccessKey())){
            Log.i(TAG, "AppInfo : " + mAppInfo);
            if (retryCount > 0){
                retryCount --;//防止后台返回AppInfo中的字段为空
                fetchAppInfoFromServer(userId, token);
            }
            return;
        }else{
            registerXinGe(userId, token);
        }
    }


    private void registerXinGe(final String userId , final String token){
        XGPushConfig.enableDebug(context, false);
        XGPushConfig.setAccessKey(context, mAppInfo.getAccessKey());
        XGPushConfig.setAccessId(context, Long.valueOf(mAppInfo.getAccessId()));
        XGPushManager.registerPush(context,new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int i) {
                Log.i(TAG,"注册成功，设备token为：" + data + "device Token :" + XGPushConfig.getToken(context) );
                bindToken(userId, token);
            }

            @Override
            public void onFail(Object o, int errCode, String msg) {
                Log.d(TAG, "注册失败，错误码：" + errCode + ",错误信息：" + msg);
            }
        });
    }


    public void bindToken(String userId, String token){
        mBindTokenRequest.maySynchronizeTokenWithServer(mAppInfo, userId, token, new BindTokenRequest.IBindTokenCallback() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "上报token成功");
            }

            @Override
            public void onError() {
                Log.d(TAG, "上报token失败");
            }
        });
    }

    public void unbindToken(String userId, String token){
        if(mAppInfo == null){
            Log.i(TAG, "Appinfo未获取到，则不需要解除绑定");
            return;
        }
        mUnbindTokenRequest.unbindToken(mAppInfo, userId, token, new UnbindTokenRequest.IUnbindTokenCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "解除token绑定");
            }

            @Override
            public void onError() {
                Log.d(TAG, "解除token绑定失败");
            }
        });
    }






}
