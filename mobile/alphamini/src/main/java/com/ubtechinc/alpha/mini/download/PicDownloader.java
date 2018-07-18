package com.ubtechinc.alpha.mini.download;

/**
 * Created by ubt on 2018/4/2.
 */

public interface PicDownloader {

    public static final int ERROR_UNREACHABLE = 2000;

    public boolean download(DownloadRequest downloadRequest, PicDownloadManager.PicDownloadCallback callback);

    public void release();

}
