package com.ubtechinc.alpha.mini.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.view.View;

import com.ubtech.utilcode.utils.FileUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.databinding.AlbumsActivityHdBinding;
import com.ubtechinc.alpha.mini.entity.AlbumItem;
import com.ubtechinc.alpha.mini.ui.albums.CacheHelper;
import com.ubtechinc.alpha.mini.ui.albums.albumshare.BaseAlbumsShare;
import com.ubtechinc.alpha.mini.ui.albums.albumshare.QQShare;
import com.ubtechinc.alpha.mini.ui.albums.albumshare.QzoneShare;
import com.ubtechinc.alpha.mini.ui.albums.albumshare.WeiBoShare;
import com.ubtechinc.alpha.mini.ui.albums.albumshare.WeiXinShare;
import com.ubtechinc.alpha.mini.utils.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by liudongyang on 2018/3/30.
 * 分享页面
 */

public class ShareView implements View.OnClickListener {

    private Context   mContext;

    private AlbumItem mCurItem;

    private AlbumsActivityHdBinding binding;

    private static final float IMAGE_SCALE_X = 0.425f;
    private static final float IMAGE_SCALE_Y = 0.07f;

    public ShareView(Context context, AlbumsActivityHdBinding binding) {
        this.binding = binding;
        this.mContext = context;
        initClickListener();
    }


    private void initClickListener() {
        binding.llWeixin.setOnClickListener(this);
        binding.llFriend.setOnClickListener(this);
        binding.llQq.setOnClickListener(this);
        binding.llQzone.setOnClickListener(this);
        binding.llSina.setOnClickListener(this);
    }

    public void setCurItem(AlbumItem mCurItem) {
        this.mCurItem = mCurItem;
    }


    @Override
    public void onClick(View v) {
        BaseAlbumsShare shareUtils = null;
        switch (v.getId()){
            case R.id.ll_weixin:
                shareUtils = new WeiXinShare(mContext, mCurItem , 0);
                break;
            case R.id.ll_friend:
                shareUtils = new WeiXinShare(mContext, mCurItem , 1);
                break;
            case R.id.ll_qq:
                shareUtils = new QQShare    (mContext, mCurItem);
                break;
            case R.id.ll_qzone:
                shareUtils = new QzoneShare (mContext, mCurItem);
                break;
            case R.id.ll_sina:
                shareUtils = new WeiBoShare (mContext, mCurItem);
                break;
        }
        if (shareUtils != null){
            shareUtils.showShareDialog();
        }
    }

    public void dismiss() {
        binding.setIsShareMode(false);
    }


    public void show() {
        binding.setIsShareMode(true);
        if (FileUtils.isFileExists(mCurItem.getImageHDUrl())) {
            binding.imgShare.setImageBitmap(createWaterMaskBitmap(mCurItem.getImageHDUrl()));
        }else if(FileUtils.isFileExists(mCurItem.getOriginUrl())){
            binding.imgShare.setImageBitmap(createOriginWaterMaskBitmap());
        }
    }

    private Bitmap createOriginWaterMaskBitmap(){
        if(FileUtils.isFileExists(CacheHelper.getWaterMarkPath(mCurItem.getImageId()) )){
            return getLocaBitmap(CacheHelper.getWaterMarkPath(mCurItem.getImageId()) );
        }
        Bitmap originBitmap = getLocaBitmap(mCurItem.getOriginUrl());
        Bitmap bitmap = ThumbnailUtils.extractThumbnail(originBitmap,960,720);
        return addWaterMask(bitmap);
    }

    // TODO: 18/3/31 暂时放在这里
    protected Bitmap createWaterMaskBitmap(String path) {
        if(FileUtils.isFileExists(CacheHelper.getWaterMarkPath(mCurItem.getImageId()) )){
            return getLocaBitmap(CacheHelper.getWaterMarkPath(mCurItem.getImageId()) );
        }
        Bitmap src = getLocaBitmap(path);
        return addWaterMask(src);
    }



    private Bitmap addWaterMask(Bitmap src){
        if (src == null) {
            return null;
        }
        Resources res    = mContext.getResources();
        int width  = src.getWidth ();
        int height = src.getHeight();

        //创建一个bitmap
        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        Log.i("0607", "src bitmap--- height : " + src.getHeight() + "wide :" + src.getWidth());

        //将该图片作为画布
        Canvas canvas = new Canvas(newBitmap);
        //在画布 0，0坐标上开始绘制原始图片
        canvas.drawBitmap(src, 0, 0, null);

        Bitmap watermark = BitmapFactory.decodeResource(res, R.drawable.img_photo_watermark);

        Matrix matrix = new Matrix();
        float scaleX = (src.getWidth() * IMAGE_SCALE_X) / watermark.getWidth();
        float scaleY = (src.getHeight() * IMAGE_SCALE_Y) / watermark.getHeight();
        matrix.setScale(scaleX, scaleY,0,0);
        matrix.postTranslate((1 - IMAGE_SCALE_X) * src.getWidth(), 0);

        Log.i("0607", "translationX:  " + (1 - IMAGE_SCALE_X) * src.getWidth());
        Log.i("0607", "addWaterMask:  scaleX : " + scaleX + " scaleY : " + scaleY );
        Log.i("0607", "Water wide " + watermark.getWidth() + " Water height :  " + watermark.getHeight());
        //在画布上绘制水印图片
        canvas.drawBitmap(watermark, matrix,null);

        // 保存
        canvas.save(Canvas.ALL_SAVE_FLAG);
        // 存储
        canvas.restore();
        String name = mCurItem.getImageId();
        saveBitmap(newBitmap, name);
        return newBitmap;
    }


    public Bitmap getLocaBitmap(String path) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(fis);
        return bitmap;
    }


    public void saveBitmap(Bitmap bm, String picName) {
        FileUtils.createOrExistsFile(CacheHelper.getWaterMarkPath(picName) );
        File f = new File(CacheHelper.getWaterMarkPath(), picName);
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

