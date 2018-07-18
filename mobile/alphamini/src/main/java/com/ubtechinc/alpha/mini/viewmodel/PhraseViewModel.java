package com.ubtechinc.alpha.mini.viewmodel;

import android.util.Log;

import com.ubtechinc.alpha.mini.entity.observable.RobotPhraseLive;
import com.ubtechinc.alpha.mini.repository.PhraseRepository;
import com.ubtechinc.alpha.mini.net.PetphraseQueryModule;
import com.ubtechinc.alpha.mini.net.PetphraseRecomandListModule;
import com.ubtechinc.alpha.mini.net.PetphraseUpdataModule;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.ThrowableWrapper;

/**
 * @Date: 2017/12/6.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 口头禅网络部分业务逻辑
 */

public class PhraseViewModel{

    public  String TAG = getClass().getSimpleName();
    private PhraseRepository mPhraseRepository;
    private RobotPhraseLive robotPhraseLive;

    public PhraseViewModel() {
        mPhraseRepository = new PhraseRepository();
        robotPhraseLive = new RobotPhraseLive();
    }

    public RobotPhraseLive queryRecomandPhraseLists(){
        mPhraseRepository.fetchRecomandPhraseList(new ResponseListener<PetphraseRecomandListModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {

            }

            @Override
            public void onSuccess(PetphraseRecomandListModule.Response response) {
                if (response.isSuccess()){
                    robotPhraseLive.setRecomandPhraseList(response.getData().getResult());
                }
            }
        });
        return robotPhraseLive;
    }

    public RobotPhraseLive queryUserPhrase(String useId){
        mPhraseRepository.queryUserPhrase(useId, new ResponseListener<PetphraseQueryModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {

            }

            @Override
            public void onSuccess(PetphraseQueryModule.Response response) {
                if (response.isSuccess()){
                    String content = response.getData().getResult().getContent();
                    Log.d(TAG, "onSuccess---> content :" + content);
                    robotPhraseLive.setUserPhrase(content);
                }
            }
        });
        return robotPhraseLive;
    }

    public RobotPhraseLive updataRobotPhrase(String userId, String updataPhrase, final IOnChangePhraseListener listener){
        mPhraseRepository.updataUserPhrase(userId, updataPhrase, new ResponseListener<PetphraseUpdataModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {

            }

            @Override
            public void onSuccess(PetphraseUpdataModule.Response response) {
                if (response.isSuccess()){
                    listener.onPhraseChange();
                }
            }
        });
        return robotPhraseLive;
    }


    public interface IOnChangePhraseListener{
        void onPhraseChange();
    }
}
