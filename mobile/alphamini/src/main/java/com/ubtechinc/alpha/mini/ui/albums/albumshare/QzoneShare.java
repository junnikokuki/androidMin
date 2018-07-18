package com.ubtechinc.alpha.mini.ui.albums.albumshare;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.ubtechinc.alpha.mini.entity.AlbumItem;
import com.ubtechinc.alpha.mini.ui.albums.CacheHelper;
import com.ubtechinc.alpha.mini.utils.Constants;

/**
 * @作者：liudongyang
 * @日期: 18/3/29 19:45
 * @描述: qq空间
 */

public class QzoneShare extends BaseAlbumsShare {

    public QzoneShare(Context ctx, AlbumItem item) {
        super(ctx, item);
    }

    private IUiListener qqIUiListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            Log.d("1103", " onComplete o : " + o);
        }

        @Override
        public void onError(UiError uiError) {
            Log.d("1103", " onError uiError : " + uiError.errorMessage + uiError.toString());
        }

        @Override
        public void onCancel() {
            Log.d("1103", " onCancel o  ");
        }
    };

    @Override
    public void share() {
        createWaterMaskBitmap(mAlbumItem.getImageHDUrl());
        final Bundle params = new Bundle();

        params.putInt(com.tencent.connect.share.QQShare.SHARE_TO_QQ_KEY_TYPE, com.tencent.connect.share.QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, CacheHelper.getWaterMarkPath(mAlbumItem.getImageId()));
        params.putInt(com.tencent.connect.share.QQShare.SHARE_TO_QQ_EXT_INT, com.tencent.connect.share.QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        params.putInt(com.tencent.connect.share.QzoneShare.SHARE_TO_QZONE_KEY_TYPE, com.tencent.connect.share.QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE);//类型

        new Thread(new Runnable() {

            @Override
            public void run() {
                Tencent mTencent = Tencent.createInstance(com.ubtechinc.alpha.mini.constants.Constants.QQ_OPEN_ID, mCtx);
                mTencent.shareToQQ((Activity) mCtx, params, qqIUiListener);
            }
        }).start();
    }

    @Override
    public String getShareTitle() {
        return "QQ空间";
    }
}
