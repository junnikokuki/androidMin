package com.ubtechinc.alpha.mini.repository;

import com.ubtechinc.alpha.mini.net.PetphraseQueryModule;
import com.ubtechinc.alpha.mini.net.PetphraseRecomandListModule;
import com.ubtechinc.alpha.mini.net.PetphraseUpdataModule;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.HttpProxy;
import com.ubtechinc.nets.http.ThrowableWrapper;

/**
 * @Date: 2017/12/6.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :  口头禅相关接口(查询所有推荐口头禅，查询用户口头禅，更改用户口头禅)
 */

public class PhraseRepository {

    public void fetchRecomandPhraseList(final ResponseListener<PetphraseRecomandListModule.Response> listener){
        final PetphraseRecomandListModule.Request request = new PetphraseRecomandListModule().new Request();
        HttpProxy.get().doGet(request, new ResponseListener<PetphraseRecomandListModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                listener.onError(e);
            }

            @Override
            public void onSuccess(PetphraseRecomandListModule.Response response) {
                listener.onSuccess(response);
            }
        });
    }

    public void updataUserPhrase(String robotUserId, String phrase, final ResponseListener<PetphraseUpdataModule.Response> listener){
        PetphraseUpdataModule.Request request = new PetphraseUpdataModule().new Request();
        request.setPetPhrase(phrase);
        request.setRobotUserId(robotUserId);
        HttpProxy.get().doPost(request, new ResponseListener<PetphraseUpdataModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                listener.onError(e);
            }

            @Override
            public void onSuccess(PetphraseUpdataModule.Response response) {
                listener.onSuccess(response);
            }
        });
    }


    public void queryUserPhrase(String robotUserId, final ResponseListener<PetphraseQueryModule.Response> listener){
        final PetphraseQueryModule.Request request = new PetphraseQueryModule().new Request();
        request.setRobotUserId(robotUserId);
        HttpProxy.get().doGet(request, new ResponseListener<PetphraseQueryModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                listener.onError(e);
            }

            @Override
            public void onSuccess(PetphraseQueryModule.Response response) {
                listener.onSuccess(response);
            }
        });
    }


}
