package com.ubtechinc.alpha.mini.avatar.server;

import com.ubtechinc.alpha.mini.avatar.entity.AvatarResponse;

/**
 * Created by ubt on 2017/8/29.
 */

public interface IAvatarServer {

    public void getAllActions(int type,AvatarServerCallback callback);

    public void getFavActions(int type,AvatarServerCallback callback);

    public void updateFavActions(int type,String actionIds,AvatarServerCallback callback);

    public interface AvatarServerCallback{

        public void onSuccess(AvatarResponse response);

        public void onError(AvatarResponse response);

    }
}
