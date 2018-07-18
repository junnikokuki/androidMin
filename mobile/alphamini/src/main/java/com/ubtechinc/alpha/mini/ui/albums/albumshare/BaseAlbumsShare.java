package com.ubtechinc.alpha.mini.ui.albums.albumshare;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;
import android.view.View;

import com.ubtech.utilcode.utils.FileUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.entity.AlbumItem;
import com.ubtechinc.alpha.mini.ui.albums.CacheHelper;
import com.ubtechinc.alpha.mini.utils.Constants;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @作者：liudongyang
 * @日期: 18/3/29 18:04
 * @描述: 相册分享工具
 */

public abstract class BaseAlbumsShare{


    private static final float IMAGE_SCALE_X = 0.425f;
    private static final float IMAGE_SCALE_Y = 0.07f;

    protected Context mCtx;

    protected AlbumItem mAlbumItem;

    public BaseAlbumsShare(Context ctx, AlbumItem item) {
        this.mCtx       = ctx;
        this.mAlbumItem = item;
    }


    public void showShareDialog(){
        final MaterialDialog settingDialog = new MaterialDialog(mCtx);
        String message = String.format(mCtx.getResources().getString(R.string.share_dialog), getShareTitle());
        settingDialog.setMessage(message)
                .setCanceledOnTouchOutside(true)
                .setPositiveButton(R.string.open, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        share();
                        settingDialog.dismiss();
                    }
                }).setNegativeButton(R.string.simple_message_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingDialog.dismiss();
            }
        }).show();

    }



    protected Bitmap createWaterMaskBitmap(String path) {
        Log.d("0403", " createWaterMaskBitmap -- path : " + path);
        if(FileUtils.isFileExists(CacheHelper.getWaterMarkPath(mAlbumItem.getImageId()))){
            return getLocaBitmap(path);
        }
        Bitmap src = getLocaBitmap(path);
        if (src == null) {
            return null;
        }
        Resources res    = mCtx.getResources();
        int width  = src.getWidth ();
        int height = src.getHeight();

        //创建一个bitmap
        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        //将该图片作为画布
        Canvas canvas = new Canvas(newBitmap);
        //在画布 0，0坐标上开始绘制原始图片
        canvas.drawBitmap(src, 0, 0, null);

        Bitmap watermark = BitmapFactory.decodeResource(res, R.drawable.img_photo_watermark);

        Matrix matrix = new Matrix();
        float scaleX = (src.getWidth() * IMAGE_SCALE_X)  / watermark.getWidth();
        float scaleY = (src.getHeight() * IMAGE_SCALE_Y) / watermark.getHeight();
        matrix.setScale(scaleX, scaleY,0,0);
        matrix.postTranslate((1 - IMAGE_SCALE_X) * src.getWidth(), 0);

        //在画布上绘制水印图片
        canvas.drawBitmap(watermark, matrix,null);

        // 保存
        canvas.save(Canvas.ALL_SAVE_FLAG);
        // 存储
        canvas.restore();
        String name = path.substring(path.lastIndexOf('/') + 1);
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
        FileUtils.createOrExistsFile(CacheHelper.getWaterMarkPath(picName));
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


    public abstract void share();

    public abstract String getShareTitle();
}
