package com.ubtechinc.alpha.mini.widget.viewpage.page;

/**
 * Created by junsheng.chen on 2018/7/12.
 */
public interface UploadProgressListener {

    void onUploadStart();

    void onUploadProgress();

    void onUploadSuccess();

    void onUploadFail();
}
