package com.ubtechinc.alpha.mini.repository;

import com.ubtechinc.alpha.mini.net.GetMobileInfoModule;
import com.ubtechinc.alpha.mini.net.GetVetificationModule;
import com.ubtechinc.alpha.mini.net.MobileNumberBindModule;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;

/**
 * @作者：liudongyang
 * @日期: 18/4/16 10:53
 * @描述: 手机的数据获取的网络逻辑部分
 */

public class BindMobileRepository {

    public static final int UBT_APP_ID = 100020011;//为了后台统计不同产品的注册数据，增加一个字段ubtAppId


    public void getVerificationCode(String moblieNumber, final ResponseListener<GetVetificationModule.Response> listener){
        GetVetificationModule.Request request = new GetVetificationModule.Request();
        StringBuilder builder = new StringBuilder();
        builder.append(86).append(moblieNumber);
        request.setAccount(builder.toString());
        request.setAccountType("0");
        request.setPurpose(4);
        request.setAppId(UBT_APP_ID);

        HttpProxy.get().doGet(request, new ResponseListener<GetVetificationModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                listener.onError(e);
            }

            @Override
            public void onSuccess(GetVetificationModule.Response response) {
                listener.onSuccess(response);
            }
        });
    }


    public void accountBind(String moblieNumber, String vertificationCode, final ResponseListener<MobileNumberBindModule.Response> listener){
        MobileNumberBindModule.Request request = new MobileNumberBindModule.Request();
        StringBuilder builder = new StringBuilder();
        builder.append(86).append(moblieNumber);
        request.setAccount(builder.toString());
        request.setAccountType(0);
        request.setAppId(UBT_APP_ID);
        request.setCaptcha(vertificationCode);


        HttpProxy.get().doPost(request, new ResponseListener<MobileNumberBindModule.Response>() {

            @Override
            public void onError(ThrowableWrapper e) {
                listener.onError(e);
            }

            @Override
            public void onSuccess(MobileNumberBindModule.Response response) {
                listener.onSuccess(response);
            }
        });
    }

    public void getBindMobileInfo(final ResponseListener<GetMobileInfoModule.Response> listener){
        GetMobileInfoModule.Request request = new GetMobileInfoModule.Request();
        request.setAppId(UBT_APP_ID);
        HttpProxy.get().doGet(request, new ResponseListener<GetMobileInfoModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                listener.onError(e);
            }

            @Override
            public void onSuccess(GetMobileInfoModule.Response response) {
                listener.onSuccess(response);
            }
        });

    }


}
