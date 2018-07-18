package com.ubtechinc.alpha.mini.ui.car;

/**
 * @作者：liudongyang
 * @日期: 18/7/5 14:33
 * @描述:
 */
public class AuthWords {

    private String sayWords;

    private String content;

    private int avatar;

    private int mPlayTime = 5000;
    /*控制loading*/
    private boolean isRunning;

    public int getPlayTime() {
        return mPlayTime;
    }

    public void setPlayTime(int playTime) {
        this.mPlayTime = playTime;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public String getSayWords() {
        return sayWords;
    }

    public void setSayWords(String sayWords) {
        this.sayWords = sayWords;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "AuthWords{" +
                "sayWords='" + sayWords + '\'' +
                ", content='" + content + '\'' +
                ", avatar=" + avatar +
                '}';
    }
}
