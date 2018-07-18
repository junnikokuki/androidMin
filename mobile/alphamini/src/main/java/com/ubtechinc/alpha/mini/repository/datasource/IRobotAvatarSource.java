package com.ubtechinc.alpha.mini.repository.datasource;

import com.ubtechinc.nets.http.ThrowableWrapper;


public interface IRobotAvatarSource {

    interface StartAvatarCallback {

        void onSuccess(String channelId, String channelKey);

        void onFail(ThrowableWrapper e);
    }

}
