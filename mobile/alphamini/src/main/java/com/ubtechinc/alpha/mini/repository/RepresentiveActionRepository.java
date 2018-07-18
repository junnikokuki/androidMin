package com.ubtechinc.alpha.mini.repository;

import com.ubtechinc.alpha.mini.net.ActionRepresentiveQueryModule;
import com.ubtechinc.alpha.mini.net.ActionRepresentiveRecomandListModule;
import com.ubtechinc.alpha.mini.net.ActionRepresentiveUpdataModule;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;

/**
 * @Date: 2017/12/6.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class RepresentiveActionRepository {

    public void queryRobotRepresentiveAction(String robotUserId, final ResponseListener<ActionRepresentiveQueryModule.Response> listener){
        ActionRepresentiveQueryModule.Request request = new ActionRepresentiveQueryModule().new Request();
        request.setRobotUserId(robotUserId);
        HttpProxy.get().doGet(request, new ResponseListener<ActionRepresentiveQueryModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                listener.onError(e);
            }

            @Override
            public void onSuccess(ActionRepresentiveQueryModule.Response response) {
                listener.onSuccess(response);
            }
        });
    }

    public void queryRepresentiveActions(final ResponseListener<ActionRepresentiveRecomandListModule.Response> listener){
        ActionRepresentiveRecomandListModule.Request request = new ActionRepresentiveRecomandListModule().new Request();
        HttpProxy.get().doGet(request, new ResponseListener<ActionRepresentiveRecomandListModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                listener.onError(e);
            }

            @Override
            public void onSuccess(ActionRepresentiveRecomandListModule.Response response) {
                listener.onSuccess(response);
            }
        });
    }

    public void updataRobotRepresentiveActions(String robotUserId, int actionId, final ResponseListener<ActionRepresentiveUpdataModule.Response> listener){
        ActionRepresentiveUpdataModule.Request request = new ActionRepresentiveUpdataModule().new Request();
        request.setRobotUserId(robotUserId);
        request.setAction(actionId);
        HttpProxy.get().doPost(request, new ResponseListener<ActionRepresentiveUpdataModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                listener.onError(e);
            }

            @Override
            public void onSuccess(ActionRepresentiveUpdataModule.Response response) {
                listener.onSuccess(response);
            }
        });
    }

}
