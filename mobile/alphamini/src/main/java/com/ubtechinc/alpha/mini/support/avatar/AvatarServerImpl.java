package com.ubtechinc.alpha.mini.support.avatar;

import com.ubtechinc.alpha.mini.avatar.db.AvatarActionHelper;
import com.ubtechinc.alpha.mini.avatar.entity.AvatarResponse;
import com.ubtechinc.alpha.mini.avatar.server.IAvatarServer;

/**
 * Created by ubt on 2017/8/30.
 */

public class AvatarServerImpl implements IAvatarServer {


    private static AvatarServerImpl instance;

    private AvatarServerImpl(){

    }

    public static AvatarServerImpl getInstance(){
        if(instance == null){
            synchronized (AvatarServerImpl.class){
                if(instance== null){
                    instance = new AvatarServerImpl();
                }
            }
        }
        return instance;
    }


    //TODO
    @Override
    public void getAllActions(int type, AvatarServerCallback callback) {
        AvatarResponse avatarResponse = new AvatarResponse();
        avatarResponse.setModels(AvatarActionHelper.getInstance().getAllAvatarActionModelList());
        avatarResponse.setStatus(true);
        callback.onSuccess(avatarResponse);
    }

    @Override
    public void getFavActions(int type, AvatarServerCallback callback) {
        AvatarResponse avatarResponse = new AvatarResponse();
        avatarResponse.setModels(AvatarActionHelper.getInstance().getAvatarActionModelList());
        avatarResponse.setStatus(true);
        callback.onSuccess(avatarResponse);
    }

    @Override
    public void updateFavActions(int type, String actionIds, AvatarServerCallback callback) {
        AvatarResponse avatarResponse = new AvatarResponse();
        avatarResponse.setModels(AvatarActionHelper.getInstance().getAvatarActionModelList());
        avatarResponse.setStatus(true);
        callback.onSuccess(avatarResponse);
    }

    public static void onDestroy() {
        if (instance != null) {
            instance = null;
        }
    }
}
