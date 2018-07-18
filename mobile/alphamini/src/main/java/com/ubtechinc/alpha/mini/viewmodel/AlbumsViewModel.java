package com.ubtechinc.alpha.mini.viewmodel;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.ubtechinc.alpha.mini.download.PicDownloadManager;
import com.ubtechinc.alpha.mini.download.SimpleDownloadCallback;
import com.ubtechinc.alpha.mini.entity.AlbumItem;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.entity.observable.PicDownloadResult;
import com.ubtechinc.alpha.mini.entity.observable.PicDownloadResult.PICSTATE;
import com.ubtechinc.alpha.mini.repository.AlbumsRepository;
import com.ubtechinc.alpha.mini.repository.datasource.IAlbumsDataSource;
import com.ubtechinc.alpha.mini.repository.datasource.IAlbumsDataSource.IDelteAlbumItemCallback;
import com.ubtechinc.alpha.mini.repository.datasource.IAlbumsDataSource.IGetAlbumsItemCallback;
import com.ubtechinc.alpha.mini.repository.datasource.IAlbumsDataSource.IGetAllAlbumsFromDBCallback;
import com.ubtechinc.alpha.mini.utils.Constants;
import com.ubtrobot.lib.sync.engine.ResultPacket;
import com.ubtrobot.param.sync.Params;

import java.io.File;
import java.util.List;

/**
 * @作者：liudongyang
 * @日期: 18/3/19 14:12
 * @描述: 相册的业务逻辑
 */

public class AlbumsViewModel {

    private AlbumsRepository mAlbumsRepository;

    private Handler mHandler;

    private static final int DOWNLOAD_HDIMG_TIMEOUT_EVENT = 10000;
    private static final int DOWNLOAD_ORIMG_TIMEOUT_EVENT = 10001;
    private static final int DOWNLOAD_ORIMG_NEED_PROGRESS_TIMEOUT_EVENT = 10002;

    public  static final int TIMEOUT_FAIL = 100;


    /**用于load下一页数据**/
    private boolean mHasNextPage;
    private String  mContext;

    public AlbumsViewModel() {
        mAlbumsRepository = new AlbumsRepository();
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case DOWNLOAD_ORIMG_TIMEOUT_EVENT:
                    case DOWNLOAD_HDIMG_TIMEOUT_EVENT:
                        if (msg.obj instanceof LiveResult){
                            LiveResult liveResult = (LiveResult)msg.obj;
                            liveResult.fail(TIMEOUT_FAIL, "超时");
                        }
                        break;
                    case DOWNLOAD_ORIMG_NEED_PROGRESS_TIMEOUT_EVENT:
                        if (msg.obj instanceof PICSTATE){
                            PicDownloadResult result = (PicDownloadResult)msg.obj;
                            result.fail(TIMEOUT_FAIL, "超时");
                        }
                        break;
                }
            }
        };
    }


    public LiveResult<List<AlbumItem>> loadAllAlbumsData(){
        final LiveResult<List<AlbumItem>> result = new LiveResult<>();
        mAlbumsRepository.loadAlbumsDataFromDB(new IGetAllAlbumsFromDBCallback() {
            @Override
            public void onSuccess(List<AlbumItem> items) {
                result.success(items);
            }

            @Override
            public void onError(int code, String msg) {
                result.fail(code, msg);
            }
        });
        return result;
    }


    public LiveResult<List<AlbumItem>> syncAlbum(){
        final LiveResult<List<AlbumItem>> result = new LiveResult<>();

        mAlbumsRepository.syncAlbum(new IGetAlbumsItemCallback() {

            @Override
            public void onSuccess(List<AlbumItem> items, ResultPacket<Params.PullResponse> pullResponseResultPacket) {
                mAlbumsRepository.deleteRobotAlbumsCache();

                mHasNextPage = pullResponseResultPacket.response.getHasNext();
                mContext     = pullResponseResultPacket.response.getContext();

                mAlbumsRepository.saveSyncAlbums2DB(items);
                result.success(items);
            }

            @Override
            public void onError(int code, String msg) {
                result.fail(code, msg);
            }
        });

        Log.i("0301", "syncAlbum: ");
        return result;
    }


    public static final int NO_PHOTO = 10000;
    public LiveResult<List<AlbumItem>> loadMoreAlbums(){
        final LiveResult<List<AlbumItem>> result = new LiveResult<>();
        if (mHasNextPage){
            mAlbumsRepository.loadMoreAlbums(new IAlbumsDataSource.IGetAlbumsItemCallback() {
                @Override
                public void onSuccess(List<AlbumItem> items, ResultPacket<Params.PullResponse> pullResponseResultPacket) {
                    mHasNextPage = pullResponseResultPacket.response.getHasNext();
                    mContext     = pullResponseResultPacket.response.getContext();

                    result.success(items);
                }

                @Override
                public void onError(int code, String msg) {
                    result.fail(code, msg);
                }
            }, mContext, mHasNextPage);
        }else{
            result.fail(NO_PHOTO, "没有更多照片");
        }
        return result;
    }


    public LiveResult deleteCheckedRobotAlbums(final List<AlbumItem> items){
        final LiveResult result = new LiveResult();
        mAlbumsRepository.deleteAlbumsListInRobot(items, new IDelteAlbumItemCallback() {
            @Override
            public void onSuccess() {
                deleteAlbumListInDB(items);
                result.success(items);
            }

            @Override
            public void onError(int code, String msg) {
                result.fail(code, msg);
            }
        });
        return result;
    }


    private void deleteAlbumListInDB(final List<AlbumItem> items){
        mAlbumsRepository.deleteAlbumListInDB(items, new IDelteAlbumItemCallback() {
            @Override
            public void onSuccess() {
                for (AlbumItem item: items) {
                    deleteFile(item.getImageThumbnailUrl());
                    deleteFile(item.getImageHDUrl());
                    deleteFile(item.getOriginUrl());
                }
            }

            @Override
            public void onError(int code, String msg) {

            }
        });
    }
    private void deleteFile(String path){
        if (TextUtils.isEmpty(path)){
            return;
        }
        File file = new File(path);
        if (!file.exists()){
            return;
        }
        file.delete();
    }


    public LiveResult deleteOneAlbumInRobot(final AlbumItem item){
        final LiveResult result = new LiveResult();

        mAlbumsRepository.deleteOneAlbumInRobot(item, new IDelteAlbumItemCallback() {
            @Override
            public void onSuccess() {
                deleteOneAlbumInDB(item);
                result.success(item);
            }

            @Override
            public void onError(int code, String msg) {
                result.fail(code, msg);
            }
        });
        return result;
    }


    private void deleteOneAlbumInDB(final AlbumItem item){
        mAlbumsRepository.deleteOneAlbumInDB(item, new IDelteAlbumItemCallback() {
            @Override
            public void onSuccess() {
                deleteFile(item.getImageThumbnailUrl());
                deleteFile(item.getImageHDUrl());
                deleteFile(item.getOriginUrl());
            }

            @Override
            public void onError(int code, String msg) {

            }
        });
    }


    public LiveResult downloadPic(final List<AlbumItem> items) {
        final LiveResult liveResult = new LiveResult();
        String robotId = MyRobotsLive.getInstance().getRobotUserId();
        PicDownloadManager.get().downloadPic(robotId, items, new SimpleDownloadCallback(){

            @Override
            public void onSuccess(String fileName) {
                liveResult.success(fileName);
            }

            @Override
            public void onFail(String fileName,int code) {
                liveResult.fail(code,fileName);
            }

        });
        return liveResult;
    }


    public LiveResult downloadOriginPic(final List<AlbumItem> items){
        final LiveResult liveResult = new LiveResult();
        String robotId = MyRobotsLive.getInstance().getRobotUserId();
        generateTimeoutMessage(liveResult, DOWNLOAD_ORIMG_TIMEOUT_EVENT);
        PicDownloadManager.get().downloadOriginPic(robotId, items, new SimpleDownloadCallback(){
            @Override
            public void onSuccess(String fileName) {
                Log.i("fileName", "onSuccess: " + fileName);
                liveResult.success(fileName);
                mHandler.removeMessages(DOWNLOAD_ORIMG_TIMEOUT_EVENT);
            }

            @Override
            public void onFail(String fileName,int code) {
                liveResult.fail(code,fileName);
                mHandler.removeMessages(DOWNLOAD_ORIMG_TIMEOUT_EVENT);
            }
        });
        return liveResult;
    }


    public LiveResult downloadOriginPic(AlbumItem item){
        final PicDownloadResult liveResult = new PicDownloadResult();
        generateTimeoutMessage(liveResult, DOWNLOAD_ORIMG_NEED_PROGRESS_TIMEOUT_EVENT);
        String robotId = MyRobotsLive.getInstance().getRobotUserId();
        PicDownloadManager.get().downloadOriginPic(robotId, item, new PicDownloadManager.PicDownloadCallback(){
            @Override
            public void onStart(String fileName) {
                //暂时用不到
            }

            @Override
            public void onProgress(String fileName, int progress) {
                Log.i("0423", "onProgress: " + fileName);
                liveResult.progressing(progress, fileName);
            }

            @Override
            public void onSuccess(String fileName) {
                Log.i("0423", "origin image download onSuccess: " + fileName);
                mHandler.removeMessages(DOWNLOAD_ORIMG_NEED_PROGRESS_TIMEOUT_EVENT);
                liveResult.success(fileName);
            }


            @Override
            public void onFail(String fileName,int code) {
                Log.i("0423", "origin image download onFail: " + fileName);
                mHandler.removeMessages(DOWNLOAD_ORIMG_NEED_PROGRESS_TIMEOUT_EVENT);
                liveResult.fail(code,fileName);
            }
        });
        return liveResult;
    }


    public LiveResult downloadHDPic(AlbumItem item){
        final LiveResult liveResult = new LiveResult();
        generateTimeoutMessage(liveResult, DOWNLOAD_HDIMG_TIMEOUT_EVENT);
        String robotId  = MyRobotsLive.getInstance().getRobotUserId();
        PicDownloadManager.get().downloadHDPic(robotId, item, new SimpleDownloadCallback(){
            @Override
            public void onSuccess(String fileName) {
                Log.i("fileName", "hd image download onSuccess: " + fileName);
                mHandler.removeMessages(DOWNLOAD_HDIMG_TIMEOUT_EVENT);
                liveResult.success(fileName);
            }

            @Override
            public void onFail(String fileName,int code) {
                Log.e("fileName", "onFail: " + fileName + " code : "+ code );
                mHandler.removeMessages(DOWNLOAD_HDIMG_TIMEOUT_EVENT);
                liveResult.fail(code,fileName);
            }
        });
        return liveResult;
    }


    private void generateTimeoutMessage(LiveResult liveResult, int event) {
        Message msg = Message.obtain();
        msg.obj  = liveResult;
        msg.what = event;
        mHandler.sendMessageDelayed(msg, 15 * 1000);
    }



}
