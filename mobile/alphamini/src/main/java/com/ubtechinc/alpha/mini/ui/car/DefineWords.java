package com.ubtechinc.alpha.mini.ui.car;

/**
 * @作者：liudongyang
 * @日期: 18/7/11 10:26
 * @描述:
 */
public class DefineWords {
    private String mContent;

    private boolean mIsRunning;

    private int mPlayTime;


    public int getPlayTime() {
        return mPlayTime;
    }

    public void setPlayTime(int playTime) {
        this.mPlayTime = playTime;
    }

    public DefineWords() {
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    public void setRunning(boolean mIsRunning) {
        this.mIsRunning = mIsRunning;
    }
}
