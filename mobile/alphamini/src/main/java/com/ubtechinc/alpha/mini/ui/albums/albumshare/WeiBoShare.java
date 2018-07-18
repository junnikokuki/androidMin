package com.ubtechinc.alpha.mini.ui.albums.albumshare;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.ubtechinc.alpha.mini.entity.AlbumItem;
import com.ubtechinc.alpha.mini.ui.albums.CacheHelper;
import com.ubtechinc.alpha.mini.utils.Constants;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @作者：liudongyang
 * @日期: 18/3/29 19:45
 * @描述: 微博分享
 */

public class WeiBoShare extends BaseAlbumsShare {

    public WeiBoShare(Context ctx, AlbumItem item) {
        super(ctx, item);
    }

    @Override
    public void share() {
        createWaterMaskBitmap(mAlbumItem.getImageHDUrl());
        IWeiboShareAPI mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(mCtx, Constants.APP_KEY);
        mWeiboShareAPI.registerApp();   // 将应用注册到微博客户端

        WeiboMessage message = new WeiboMessage();
        ImageObject imageObject = new ImageObject();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(CacheHelper.getWaterMarkPath(mAlbumItem.getImageId()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(fis);
        Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, 960, 720, true);
        imageObject.setImageObject(bitmap1);
        message.mediaObject = imageObject;
        bitmap.recycle();
        bitmap1.recycle();
        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.message = message;
        mWeiboShareAPI.sendRequest(request);
    }

    @Override
    public String getShareTitle() {
        return "微博";
    }


}
