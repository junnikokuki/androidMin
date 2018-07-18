package com.ubtechinc.alpha.mini.download;

import android.util.Log;

import com.ubtechinc.alpha.mini.entity.AlbumItem;

import java.util.List;

import static com.ubtechinc.alpha.mini.download.PicDownloader.ERROR_UNREACHABLE;

/**
 * Created by ubt on 2018/4/2.
 */

public class PicDownloadManager {

    public PicDownloader imDownloader;

    public PicDownloader socketDownloader;

    private static volatile PicDownloadManager instance;

    public static PicDownloadManager get() {
        if (instance == null) {
            synchronized (PicDownloadManager.class) {
                if (instance == null) {
                    instance = new PicDownloadManager();
                }
            }
        }
        return instance;
    }

    private PicDownloadManager() {
        imDownloader = new PicImDownloader();
        socketDownloader = new PicSocketDownloader();
    }

    public void downloadPic(String robotUserId, AlbumItem albumItem, final PicDownloadCallback callback) {
        doDownloadPic(robotUserId, albumItem, 1, callback);
    }

    public void downloadOriginPic(String robotUserId, AlbumItem albumItem, PicDownloadCallback callback) {
        doDownloadPic(robotUserId, albumItem, 0, callback);
    }

    public void downloadHDPic(String robotUserId, AlbumItem albumItem, PicDownloadCallback callback) {
        doDownloadPic(robotUserId, albumItem, 2, callback);
    }

    public void downloadPic(String robotUserId, List<AlbumItem> albumItems, final PicDownloadCallback callback) {
        for (AlbumItem albumItem : albumItems) {
            doDownloadPic(robotUserId, albumItem, 1, callback);
        }
    }

    public void downloadOriginPic(String robotUserId,  List<AlbumItem> albumItems, PicDownloadCallback callback) {
        for (AlbumItem albumItem : albumItems) {
            doDownloadPic(robotUserId, albumItem, 0, callback);
        }
    }

    public void downloadHDPic(String robotUserId,  List<AlbumItem> albumItems, PicDownloadCallback callback) {
        for (AlbumItem albumItem : albumItems) {
            doDownloadPic(robotUserId, albumItem, 2, callback);
        }
    }

    private void doDownloadPic(String robotUserId, AlbumItem albumItem, int level, final PicDownloadCallback callback) {
        final DownloadRequest request = new DownloadRequest(albumItem, level, robotUserId);
        socketDownloader.download(request, new PicDownloadCallback() {
            @Override
            public void onSuccess(String fileName) {
                if (callback != null) {
                    callback.onSuccess(fileName);
                }
            }

            @Override
            public void onStart(String fileName) {

            }

            @Override
            public void onProgress(String fileName, int progress) {
                if (callback != null){
                    callback.onProgress(fileName, progress);
                }
            }

            @Override
            public void onFail(String fileName,int code) {
                if (ERROR_UNREACHABLE == code) {
                    imDownloader.download(request, callback);
                } else {
                    if (callback != null) {
                        callback.onFail(fileName,code);
                    }
                }
            }
        });
    }

    public interface PicDownloadCallback {

        public void onStart(String fileName);

        public void onProgress(String fileName,int progress);

        public void onFail(String fileName,int code);

        public void onSuccess(String fileName);

    }

    public void release() {
        if (imDownloader != null) {
            imDownloader.release();
        }
        if (socketDownloader != null) {
            socketDownloader.release();
        }
        instance = null;
    }
}
