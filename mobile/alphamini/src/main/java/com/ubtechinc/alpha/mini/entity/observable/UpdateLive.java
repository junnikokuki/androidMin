package com.ubtechinc.alpha.mini.entity.observable;

/**
 * @作者：liudongyang
 * @日期: 18/6/27 15:08
 * @描述:
 */
public class UpdateLive extends LiveResult<UpdateLive>{

    public enum UpdateState{
        FORCE_UPDATE, SIMPLE_UPDATE, UNKOWN, UPDATED
    }


    private UpdateState state = UpdateState.UNKOWN;

    private UpdateLive() {

    }

    private static class UpdateLiveHolder {
        public static UpdateLive instance = new UpdateLive();
    }

    public static UpdateLive getInstance() {
        return UpdateLiveHolder.instance;
    }

    public void setState(UpdateState state){
        this.state = state;
        setValue(this);
    }


    public UpdateState getUpdateState() {
        return state;
    }
}
