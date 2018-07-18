package com.ubtechinc.alpha.mini.ui.albums.albumshare;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.entity.AlbumItem;
import com.ubtechinc.alpha.mini.ui.albums.CacheHelper;
import com.ubtechinc.alpha.mini.utils.Constants;

/**
 * @作者：liudongyang
 * @日期: 18/3/29 19:44
 * @描述: qq分享
 */

public class QQShare extends BaseAlbumsShare {

    public QQShare(Context ctx, AlbumItem item) {
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
        Tencent tencent = Tencent.createInstance(com.ubtechinc.alpha.mini.constants.Constants.QQ_OPEN_ID, mCtx);
        final Bundle params = new Bundle();
        createWaterMaskBitmap(mAlbumItem.getImageHDUrl());
        params.putInt(com.tencent.connect.share.QQShare.SHARE_TO_QQ_KEY_TYPE, com.tencent.connect.share.QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_TITLE, mCtx.getResources().getString(R.string.share_title));
        params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_SUMMARY, mCtx.getResources().getString(R.string.share_summary));
        String localPath = CacheHelper.getWaterMarkPath(mAlbumItem.getImageId());
        params.putString(com.tencent.connect.share.QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, localPath);
        tencent.shareToQQ((Activity) mCtx, params, qqIUiListener);

    }

    @Override
    public String getShareTitle() {
        return "QQ";
    }
}
