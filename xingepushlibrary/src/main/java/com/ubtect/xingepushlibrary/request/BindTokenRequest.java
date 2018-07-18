package com.ubtect.xingepushlibrary.request;

import android.content.Context;
import android.util.Log;

import com.tencent.android.tpush.XGPushConfig;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtect.xingepushlibrary.AppInfo;
import com.ubtect.xingepushlibrary.net.BindTokenModule;

import java.util.HashMap;
import java.util.Map;

/**
 * @Date: 2017/11/1.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class BindTokenRequest {
    private String TAG = getClass().getSimpleName();
    private Context context;

    public BindTokenRequest(Context context){
        this.context = context;
    }

    public void maySynchronizeTokenWithServer(AppInfo appInfo, String userId, String serverToken, final IBindTokenCallback callBack){
        String deviceToken = XGPushConfig.getToken(context);
        BindTokenModule.Request request = new BindTokenModule().new Request();
        request.setAppId(Integer.valueOf(appInfo.getAppId()));
        request.setCreateTime(appInfo.getCreateTime());
        request.setToken(deviceToken);
        request.setUserId(Integer.valueOf(userId));
        Map<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("authorization", serverToken);
        HttpProxy.get().doPost(request, hashMap, new ResponseListener<BindTokenModule.Response>() {

            @Override
            public void onError(ThrowableWrapper e) {
                Log.e(TAG, "onError");
                callBack.onError();
            }

            @Override
            public void onSuccess(BindTokenModule.Response response) {
                Log.i(TAG, "onSuccess: ");
                if (response.getBindSuccess()){
                    callBack.onSuccess();
                }else{
                    callBack.onError();
                }
            }

        });
    }

    public interface IBindTokenCallback{
        void onSuccess();

        void onError();
    }
}
