package com.ubtechinc.alpha.mini.repository.datasource.im;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ubtechinc.alpha.mini.entity.AlbumItem;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.im.PullImEngine;
import com.ubtechinc.alpha.mini.im.PushImEngine;
import com.ubtechinc.alpha.mini.repository.datasource.IAlbumsDataSource;
import com.ubtechinc.alpha.mini.repository.datasource.IAlbumsDataSource.IGetAlbumsItemCallback;
import com.ubtechinc.alpha.mini.ui.albums.CacheHelper;
import com.ubtrobot.common.config.SyncLocal;
import com.ubtrobot.lib.sync.engine.QueryPacket;
import com.ubtrobot.lib.sync.engine.ResultPacket;
import com.ubtrobot.lib.sync.engine.im.callback.PullIMCallback;
import com.ubtrobot.lib.sync.engine.im.callback.PushIMCallback;
import com.ubtrobot.param.sync.Consts;
import com.ubtrobot.param.sync.Files;
import com.ubtrobot.param.sync.Params;

import java.util.ArrayList;
import java.util.List;

import static com.ubtechinc.alpha.mini.ui.albums.AlbumsAdapter.ITEM_IMAGE_TYPE;

/**
 * @作者：liudongyang
 * @日期: 18/3/22 14:35
 * @描述: 删除远程图片的IM操作
 */

public class IMAlbumsDataSource {

    private String TAG = getClass().getSimpleName();
    private PullImEngine mPullImEngine;


    private void setEnginCallback(final IGetAlbumsItemCallback callback) {
        mPullImEngine.setCallback(new PullIMCallback() {
            @Override
            public void onSuccess(long l, @NonNull QueryPacket<Params.PullRequest> pullRequestQueryPacket, @NonNull ResultPacket<Params.PullResponse> responseResultPacket) {
                String userId = MyRobotsLive.getInstance().getRobotUserId();

                List<Files.MetaData> fileInfo = responseResultPacket.response.getDataList();

                if (fileInfo != null && fileInfo.size() > 0) {
                    List<AlbumItem> items = new ArrayList<>();

                    for (Files.MetaData data : fileInfo) {
                        AlbumItem item = new AlbumItem();
                        item.setId(data.getName() + userId);
                        item.setImageOriginUrl(CacheHelper.getInstance().getItemOriginPath(data.getName()));
                        item.setImageHDUrl(CacheHelper.getInstance().getItemHDPath(data.getName()));
                        item.setImageThumbnailUrl(SyncLocal.getFilePath(data.getThumbnail()));
                        item.setImageId(data.getName());
                        item.setCreateTime(data.getCreateTime());
                        item.setImageUploadTime(item.getDate());
                        item.setRobotId(userId);
                        item.setPath(data.getPath());
                        item.setSize(data.getSize());
                        item.setItemType(ITEM_IMAGE_TYPE);
                        Log.i("TAG", "onSuccess: "    + data.getName());
                        items.add(item);
                    }
                    callback.onSuccess(items, responseResultPacket);
                    return;
                }

                callback.onSuccess(new ArrayList<AlbumItem>(), responseResultPacket);
            }

            @Override
            public void onFail(long l, @NonNull QueryPacket<Params.PullRequest> pullRequestQueryPacket, int i, String s) {
                callback.onError(i, s);
            }
        });
    }

    public void syncAlbum(IGetAlbumsItemCallback callback) {
        if (mPullImEngine == null) {
            mPullImEngine = new PullImEngine();
        }
        setEnginCallback(callback);
        mPullImEngine.setPageSize(32);
        mPullImEngine.start();
    }


    public void loadMoreAlbums(IGetAlbumsItemCallback callback, String context, boolean mHasNext) {
        if (mPullImEngine == null) {
            Log.e(TAG, "loadMoreAlbums: mPullImEngine == null");
            return;
        }
        setEnginCallback(callback);
        mPullImEngine.mContext = context;
        mPullImEngine.mHasNext = mHasNext;
        mPullImEngine.next();
    }

    public void deleteOneAlbum(AlbumItem item, final IAlbumsDataSource.IDelteAlbumItemCallback callback){
        List<AlbumItem> items = new ArrayList<>();
        items.add(item);
        deleteAlbumListInRobot(items, callback);
    }


    public void deleteAlbumListInRobot(List<AlbumItem> deleteList, final IAlbumsDataSource.IDelteAlbumItemCallback callback) {
        PushImEngine mPushImEngine = new PushImEngine();

        List<Files.ModifyData> modifyModels = new ArrayList<>();
        for (int i = 0; i < deleteList.size(); i++) {
            Files.ModifyData.Builder builder = Files.ModifyData.newBuilder();
            builder.setAction(Consts.FILE_ACTION_DELETE);
            builder.setPath(deleteList.get(i).getPath());
            builder.setUpdateTime(System.currentTimeMillis());
            modifyModels.add(builder.build());
        }

        mPushImEngine.modifyModels = modifyModels;

        mPushImEngine.setCallback(new PushIMCallback() {
            @Override
            public void onSuccess(long l, @NonNull QueryPacket<Params.PushRequest> pushRequestQueryPacket, @NonNull ResultPacket<Params.PushResponse> pushResponseResultPacket) {
                if (callback != null){
                    callback.onSuccess();
                }
            }

            @Override
            public void onFail(long l, @NonNull QueryPacket<Params.PushRequest> pushRequestQueryPacket, int i, String s) {
            }
        });

        mPushImEngine.start();
    }
}
