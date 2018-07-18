package com.ubtechinc.alpha.mini.viewmodel;

import android.text.TextUtils;
import android.util.Log;

import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.net.GetMobileInfoModule;
import com.ubtechinc.alpha.mini.net.GetVetificationModule;
import com.ubtechinc.alpha.mini.net.MobileNumberBindModule;
import com.ubtechinc.alpha.mini.repository.BindMobileRepository;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.utils.JsonUtil;

import java.util.Map;

/**
 * @作者：liudongyang
 * @日期: 18/4/16 10:33
 * @描述: 手机号码业务逻辑
 */

public class MobileViewModel {

    private BindMobileRepository repository;


    public MobileViewModel() {
        repository = new BindMobileRepository();
    }


    public LiveResult vertification(String mobileNumer) {
        final LiveResult liveResult = new LiveResult();
        repository.getVerificationCode(mobileNumer, new ResponseListener<GetVetificationModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                Log.e("0416", "onError e :" + e);
                String errorBody = e.getErrorBody();
                if (!TextUtils.isEmpty(errorBody)){
                    Map<String, Object> map = JsonUtil.getMap(errorBody);
                    if (map.get("message") instanceof String){
                        String msg = (String) map.get("message");
                        liveResult.fail(msg);
                    }else{
                        liveResult.fail("获取验证码失败");
                    }
                }else{
                    liveResult.fail("获取验证码失败");
                }

            }

            @Override
            public void onSuccess(GetVetificationModule.Response response) {
                liveResult.success(response);
            }
        });
        return liveResult;
    }


    public LiveResult bindMobile(String mobileNumber,String vertificationCode) {
        final LiveResult liveResult = new LiveResult();
        repository.accountBind(mobileNumber, vertificationCode, new ResponseListener<MobileNumberBindModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                Log.e("0416", "onError e :" + e);
                String errorBody = e.getErrorBody();
                if (!TextUtils.isEmpty(errorBody)){
                    Map<String, Object> map = JsonUtil.getMap(errorBody);
                    if (map.get("message") instanceof String){
                        String msg = (String) map.get("message");
                        liveResult.fail(msg);
                    }else{
                        liveResult.fail("验证码错误,请重试");
                    }
                }else{
                    liveResult.fail("验证码错误,请重试");
                }
            }

            @Override
            public void onSuccess(MobileNumberBindModule.Response response) {
                liveResult.success(response);
            }
        });
        return liveResult;
    }


    public LiveResult getBindMobileInfo(){
        final LiveResult liveResult = new LiveResult();
        repository.getBindMobileInfo(new ResponseListener<GetMobileInfoModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                liveResult.fail("获取失败");
            }

            @Override
            public void onSuccess(GetMobileInfoModule.Response response) {
                String phone = response.getExpandPhone();
                liveResult.success(phone);
            }
        });
        return liveResult;
    }


}
