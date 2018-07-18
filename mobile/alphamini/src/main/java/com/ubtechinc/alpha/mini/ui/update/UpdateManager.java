package com.ubtechinc.alpha.mini.ui.update;

/**
 * @作者：liudongyang
 * @日期: 18/6/27 14:44
 * @描述:
 */
public class UpdateManager {

    private static UpdateManager manager;


    private UpdateManager() {

    }

    public static UpdateManager getManager() {
        if (manager == null){
            synchronized (UpdateManager.class){
                if (manager == null){
                    manager = new UpdateManager();
                }
            }
        }
        return manager;
    }
}
