package com.ubtect.xingepushlibrary.request;

import android.content.Context;
import android.util.Log;

import com.tencent.android.tpush.XGPushConfig;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtect.xingepushlibrary.AppInfo;
import com.ubtect.xingepushlibrary.net.UnBindTokenModule;

import java.util.HashMap;
import java.util.Map;

/**
 * @Date: 2017/11/17.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class UnbindTokenRequest {
    private String TAG = getClass().getSimpleName();
    private Context context;

    public UnbindTokenRequest(Context context){
        this.context = context;
    }

    public void unbindToken(AppInfo appInfo, String userId, String serverToken, final UnbindTokenRequest.IUnbindTokenCallback callBack){
        String deviceToken = XGPushConfig.getToken(context);
        UnBindTokenModule.Request request = new UnBindTokenModule().new Request();
        request.setAppId(Integer.valueOf(appInfo.getAppId()));
        request.setCreateTime(appInfo.getCreateTime());
        request.setToken(deviceToken);
        request.setUserId(Integer.valueOf(userId));
        Map<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("authorization", serverToken);
        HttpProxy.get().doPost(request, hashMap, new ResponseListener<UnBindTokenModule.Response>() {

            @Override
            public void onError(ThrowableWrapper e) {
                Log.e(TAG, "onError");
                callBack.onError();
            }

            @Override
            public void onSuccess(UnBindTokenModule.Response response) {
                Log.i(TAG, "onSuccess: ");
                if (response.getUnbindSuccess()){
                    callBack.onSuccess();
                }else{
                    callBack.onError();
                }
            }
        });
    }

    public interface IUnbindTokenCallback{
        void onSuccess();

        void onError();
    }

}
