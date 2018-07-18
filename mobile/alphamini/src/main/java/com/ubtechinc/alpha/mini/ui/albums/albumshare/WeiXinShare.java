package com.ubtechinc.alpha.mini.ui.albums.albumshare;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.entity.AlbumItem;
import com.ubtechinc.alpha.mini.ui.albums.CacheHelper;
import com.ubtechinc.alpha.mini.utils.Constants;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @作者：liudongyang
 * @日期: 18/3/29 18:51
 * @描述: 微信分享
 */

public class WeiXinShare extends BaseAlbumsShare {

    private int mFlag;


    public WeiXinShare(Context ctx, AlbumItem currentItem, int flag) {
        super(ctx, currentItem);
        this.mFlag = flag;
    }

    @Override
    public void share() {
        createWaterMaskBitmap(mAlbumItem.getImageHDUrl());
        IWXAPI wxApi = WXAPIFactory.createWXAPI(mCtx, com.ubtechinc.alpha.mini.constants.Constants.WX_APP_ID);
        WXImageObject wxImageObject = new WXImageObject();
        wxImageObject.setImagePath(CacheHelper.getWaterMarkPath(mAlbumItem.getImageId()));
        wxApi.registerApp(com.ubtechinc.alpha.mini.constants.Constants.WX_APP_ID);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = wxImageObject;
        msg.title = mCtx.getResources().getString(R.string.share_title);
        msg.description = mCtx.getResources().getString(R.string.share_photo);

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(mAlbumItem.getImageThumbnailUrl());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap bitmap = BitmapFactory.decodeStream(fis);
        if (bitmap != null) {
            msg.setThumbImage(bitmap);
            bitmap.recycle();
        }

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = mFlag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        wxApi.sendReq(req);
    }

    @Override
    public String getShareTitle() {
        return mFlag == 0 ? "微信" : "朋友圈";
    }


}
