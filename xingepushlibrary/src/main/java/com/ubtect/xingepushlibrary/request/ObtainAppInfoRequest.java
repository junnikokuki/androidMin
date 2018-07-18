package com.ubtect.xingepushlibrary.request;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.ubtech.utilcode.utils.SPUtils;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtect.xingepushlibrary.UbtXinGeConstant;
import com.ubtect.xingepushlibrary.AppInfo;
import com.ubtect.xingepushlibrary.net.GetPushAppInfoModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Date: 2017/11/1.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class ObtainAppInfoRequest {

    private SPUtils spUtils;

    private String TAG = getClass().getSimpleName();
    private Context context;

    public ObtainAppInfoRequest(Context context){
        this.context = context;
        spUtils = new SPUtils(UbtXinGeConstant.APPNAME);
    }

    public void obtainAppInfoFromServer(String token, final ObtainAppInfoCallBack callBack){
        final GetPushAppInfoModule.Request request = new GetPushAppInfoModule().new Request();
        request.setAppName(UbtXinGeConstant.APPNAME);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("authorization", token);
        HttpProxy.get().doGet(request, hashMap, new ResponseListener<List<AppInfo>>() {
            @Override
            public void onError(ThrowableWrapper e) {
                Log.i(TAG, "onError: get Appinfo failed!");
                callBack.onError();
            }

            @Override
            public void onSuccess(List<AppInfo> appInfos) {
                Log.i(TAG, "onSuccess: get Appinfo success");
                if (appInfos != null && appInfos.size() > 0){
                    for (AppInfo info:appInfos) {
                        if (info.getDevice().equalsIgnoreCase("a")) {
                            callBack.onSuccess(info);
                            return;
                        }
                    }
                    Log.i(TAG, "didn't get Android AppInfo");
                    callBack.onError();
                }else{
                    callBack.onError();
                }
            }
        });
    }

    public interface ObtainAppInfoCallBack{
        void onSuccess(AppInfo appInfo);

        void onError();
    }

}
