package com.ubtechinc.alpha.mini.repository;

import com.ubtechinc.alpha.mini.net.FeedbackModule;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;

/**
 * Created by hongjie.xiang on 2017/12/7.
 */

public class CommitFeedbackRepository {

    public void commitFeedback(String appVersion,String feedbackInfo,String userInfo, int uploadLog, String equipmentId, final CommitFeedBackCallback callback){
        FeedbackModule.Request request=new FeedbackModule().new Request();
        request.setFeedbackInfo(feedbackInfo);
        request.setFeedbackUser(userInfo);
        request.setAppVersion(appVersion);
        request.setEquipmentId(equipmentId);
        if (uploadLog == 1) {
            request.setUploadLog(uploadLog);
        }
        HttpProxy.get().doPost(request, new ResponseListener<FeedbackModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                callback.onError(e);
            }

            @Override
            public void onSuccess(FeedbackModule.Response response) {
                callback.onSuccess(response);
            }
        });

    }

    public interface CommitFeedBackCallback{
        public void onError(ThrowableWrapper e);

        public void onSuccess(FeedbackModule.Response response);
    }
}
