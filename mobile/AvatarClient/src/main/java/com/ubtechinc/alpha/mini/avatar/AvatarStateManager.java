package com.ubtechinc.alpha.mini.avatar;

import static com.ubtechinc.alpha.mini.avatar.AvatarStateManager.State.CONNECTING;

/**
 * @作者：liudongyang
 * @日期: 18/5/23 17:37
 * @描述:
 */

public class AvatarStateManager {

    public enum State{
        NORMAL, NETWORK_SUCK, NETWORK_ERROR, CONNECTING, CONNECTFAILED, START_FAILED
    }

    private State mState = CONNECTING;

    private IAvatarStateListener listener;

    public interface IAvatarStateListener{
        void currentState(State mState);
    }

    public void setStateListener(IAvatarStateListener listener){
        this.listener = listener;
        if (listener != null){
            listener.currentState(mState);
        }
    }

    public void setState(State state){
        mState = state;
        if (listener != null){
            listener.currentState(mState);
        }
    }

    public State getState() {
        return mState;
    }
}
