package com.ubtechinc.alpha.mini.avatar;

import android.support.annotation.MainThread;

import com.ubtechinc.alpha.mini.avatar.robot.IAvatarRobot;
import com.ubtechinc.alpha.mini.avatar.server.IAvatarServer;

/**
 * Created by ubt on 2017/9/4.
 */

public class AvatarInject {

    private static AvatarInject inject;

    private IAvatarRobot avatarRobot;

    private IAvatarServer avatarServer;

    private AndroidContextHelper contextHelper;

    @MainThread
    public static AvatarInject getInstance() {
        if (inject == null) {
            inject = new AvatarInject();
        }
        return inject;
    }

    private AvatarInject() {

    }

    public void inject(IAvatarRobot avatarRobot, IAvatarServer avatarServer, AndroidContextHelper contextHelper) {
        this.avatarRobot = avatarRobot;
        this.avatarServer = avatarServer;
        this.contextHelper = contextHelper;
    }

    public IAvatarRobot getAvatarRobot() {
        if (avatarRobot == null) {
            throw new RuntimeException("IAvatarRobot is null , " +
                    "please call AvatarInject.inject to inject the require object");
        }
        return avatarRobot;
    }

    public IAvatarServer getAvatarServer() {
        if (avatarServer == null) {
            throw new RuntimeException("IAvatarServer is null , " +
                    "please call AvatarInject.inject to inject the require object");
        }
        return avatarServer;
    }

    public AndroidContextHelper getContextHelper() {
        if (contextHelper == null) {
            throw new RuntimeException("AndroidContextHelper is null , " +
                    "please call AvatarInject.inject to inject the require object");
        }
        return contextHelper;
    }

    public static void onDestroy(){
        if(inject != null){
            inject = null;
        }
    }
}
