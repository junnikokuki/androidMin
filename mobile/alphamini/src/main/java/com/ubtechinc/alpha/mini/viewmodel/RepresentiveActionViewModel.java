package com.ubtechinc.alpha.mini.viewmodel;

import android.util.Log;

import com.ubtechinc.alpha.mini.entity.RepresentActionLive;
import com.ubtechinc.alpha.mini.repository.RepresentiveActionRepository;
import com.ubtechinc.alpha.mini.net.ActionRepresentiveQueryModule;
import com.ubtechinc.alpha.mini.net.ActionRepresentiveRecomandListModule;
import com.ubtechinc.alpha.mini.net.ActionRepresentiveUpdataModule;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.ThrowableWrapper;

/**
 * @Date: 2017/12/6.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 机器人档案，代表动作
 */

public class RepresentiveActionViewModel {
    private String TAG = getClass().getSimpleName();

    private RepresentiveActionRepository representiveActionRepository;
    private RepresentActionLive representActionLive;

    public RepresentiveActionViewModel(){
        representiveActionRepository = new RepresentiveActionRepository();
        representActionLive = new RepresentActionLive();
    }

    public RepresentActionLive quertyRobotRepresentiveAction(String robotUserId){
        representiveActionRepository.queryRobotRepresentiveAction(robotUserId, new ResponseListener<ActionRepresentiveQueryModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                e.printStackTrace();
            }

            @Override
            public void onSuccess(ActionRepresentiveQueryModule.Response response) {
                if (response.isSuccess()){
                    String name = response.getData().getResult().getDescription();
                    representActionLive.setRepresentiveAction(name);
                }else{
                    Log.e(TAG, "onSuccess: "  + response.getMsg());
                }
            }
        });
        return representActionLive;
    }


    public RepresentActionLive queryRepresentiveActions(){
        representiveActionRepository.queryRepresentiveActions(new ResponseListener<ActionRepresentiveRecomandListModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                e.printStackTrace();
            }

            @Override
            public void onSuccess(ActionRepresentiveRecomandListModule.Response response) {
                if (response.isSuccess()){
                    representActionLive.setRepresentListAction(response.getData().getResult());
                }else{
                    Log.e(TAG, "onSuccess: "  + response.getMsg());
                }
            }
        });
        return representActionLive;
    }

    public RepresentActionLive updataRobotAction(String robotUserId, int actionId, final IOnActionChangeListener listener){
        representiveActionRepository.updataRobotRepresentiveActions(robotUserId, actionId, new ResponseListener<ActionRepresentiveUpdataModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                e.printStackTrace();
            }

            @Override
            public void onSuccess(ActionRepresentiveUpdataModule.Response response) {
                if (response.isSuccess()){
                    listener.onActionUpdata();
                }
            }
        });
        return representActionLive;
    }

    public interface IOnActionChangeListener{
        void onActionUpdata();
    }

}
