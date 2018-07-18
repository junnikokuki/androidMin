package com.ubtechinc.alpha.mini.repository;

import com.ubtech.utilcode.utils.MD5Utils;
import com.ubtech.utilcode.utils.PhoneUtils;
import com.ubtechinc.alpha.mini.net.UpdateInfoModule;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @作者：liudongyang
 * @日期: 18/6/26 14:21
 * @描述:
 */
public class UpdateRepository {

    private String TAG = getClass().getSimpleName();

    private static final String APPID        = "100020031";

    private static final String TESTAPPKEY   = "e5b43be966834ec78265a4f0db40f4d1";

    private static final String FORMERAPPKEY = "7e3a58a5559c4d2eb86b3683b9f911da";

    private static final String MODULE_NAME  = "AndroidApp";

    private static final String PRODUCT_NAME = "AlphaMini";

    private static final String FILENAME     = "mini.apk";

    private static final String SIGN_SPARATOR = " ";

    public void checkVersionInfo(String versionInfo, final IVersionInfoCallback callback){
        final UpdateInfoModule.Request request = new UpdateInfoModule.Request();
        request.setProductName(PRODUCT_NAME);
        request.setModuleNames(MODULE_NAME);
        request.setVersionNames(versionInfo);
        Map<String, String> headers = new HashMap<>();
        long nowTime = (System.currentTimeMillis() / 1000);
        headers.put("X-UBT-Sign", MD5Utils.MD5Encode(nowTime + FORMERAPPKEY) + SIGN_SPARATOR + nowTime);
        headers.put("X-UBT-AppId",    APPID);
        headers.put("X-UBT-DeviceId", PhoneUtils.getIMEI());
        HttpProxy.get().doGet(request, headers, new ResponseListener<List<UpdateInfoModule.AppUpdateModule>>() {
            @Override
            public void onError(ThrowableWrapper e) {
                callback.onError(e.getErrorCode());
            }

            @Override
            public void onSuccess(List<UpdateInfoModule.AppUpdateModule> modules) {
                if (modules.size() > 0){
                    callback.onSuccess(modules.get(0));
                }


            }
        });
    }


    public interface IVersionInfoCallback{

        public void onSuccess(UpdateInfoModule.AppUpdateModule module);

        public void onError(int errorCode);

    }




}
