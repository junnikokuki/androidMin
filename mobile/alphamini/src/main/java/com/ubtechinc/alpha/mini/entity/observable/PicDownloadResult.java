package com.ubtechinc.alpha.mini.entity.observable;

/**
 * @作者：liudongyang
 * @日期: 18/4/23 14:02
 * @描述: 图片下载
 */

public class PicDownloadResult extends LiveResult {


    public enum PICSTATE {
        PROGRESSING, SUCCESS, FAIL
    }

    private PICSTATE mState = PICSTATE.PROGRESSING;

    public PICSTATE getCurrenState(){
        return mState;
    }

    private int mErrorCode;

    private int mProgress;

    private String mMsg;

    private String mFileName;


    public String getMsg() {
        return mMsg;
    }

    public String getFileName() {
        return mFileName;
    }

    public int getProgress() {
        return mProgress;
    }


    public int getErrorCode() {
        return mErrorCode;
    }


    public void success(String fileName) {
        this.mState = PICSTATE.SUCCESS;
        this.mFileName = fileName;
        this.setValue(this);
    }

    public void progressing(int progress, String fileName) {
        this.mState = PICSTATE.PROGRESSING;
        this.mProgress = progress;
        this.mFileName = fileName;
        this.setValue(this);
    }

    public void fail(int code, String msg) {
        this.mState = PICSTATE.FAIL;
        this.mErrorCode = code;
        this.mMsg = msg;
        this.setValue(this);
    }


}
