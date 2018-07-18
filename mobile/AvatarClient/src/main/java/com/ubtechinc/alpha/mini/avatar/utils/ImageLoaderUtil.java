package com.ubtechinc.alpha.mini.avatar.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.ubtechinc.alpha.mini.avatar.R;

/**
 * Created by liuliaopu on 2017/5/9.
 * 基于Glide的图片加载器类，替换原来的ImageLoader(以前的会导致OOM，且性能不行)
 */
public class ImageLoaderUtil {
    /**
     * 加载并显示图片
     *
     * @param context   建议不要用全局context, 因为glide会根据Context对应的生命周期来回收图片
     * @param imageView 显示图片控件
     * @param imageUrl  图片url
     */
    public static void displayImage(Context context, final ImageView imageView, final String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)) {
            setDefaultImage(imageView);
        } else {
            Glide.with(context)
                    .load(imageUrl)
                    .asBitmap()
                    .into(imageView);
        }

    }

    /**
     * <<<<<<< HEAD
     * 加载本地图片
     * =======
     * 加载并显示图片
     * >>>>>>> feature/dongyang_im
     *
     * @param context   建议不要用全局context, 因为glide会根据Context对应的生命周期来回收图片
     * @param imageView 显示图片控件
     * @param imageUrl  图片url
     */

    public static void displayImage(Context context, final ImageView imageView, final int imageUrl) {

        Glide.with(context)
                .load(imageUrl)
                .asBitmap()
                .into(imageView);
    }

    public static void displayImage(Context context, final ImageView imageView, final String imageUrl, int placeId) {
        if (TextUtils.isEmpty(imageUrl)) {
            setDefaultImage(imageView);
        } else {
            Glide.with(context)
                    .load(imageUrl)
                    .asBitmap()
                    .placeholder(placeId)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }

    }

    public static void display(Context context, final ImageView imageView, final String imageUrl, int placeId) {
        Glide.with(context)
                .load(imageUrl)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_notloaded)
                .into(imageView);
    }

    public static void display(Context context, final ImageView imageView, final Integer imageId, int placeId) {
        Glide.with(context)
                .load(imageId)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_notloaded)
                .into(imageView);
    }

    /**
     * 获取位图
     *
     * @param context  建议不要用全局context, 因为glide会根据Context对应的生命周期来回收图片
     * @param imageUrl 图片url
     * @return 返回位图
     */
    public static Bitmap getAsBitmap(Context context, final String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)) {
            return null;
        }

        Bitmap bitmap = null;
        try {
            bitmap = Glide.with(context)
                    .load(imageUrl)
                    .asBitmap()
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * 加载gif
     *
     * @param context
     * @param imageView
     * @param id
     * @param placeId
     */
    public static void displayGif(Context context, final ImageView imageView, final int id, int placeId, boolean isRecyle) {
        if (isRecyle) {
            Glide.with(context).load(id).diskCacheStrategy(DiskCacheStrategy.ALL).into(new GlideDrawableImageViewTarget(imageView, 100000));
        } else {
            Glide.with(context).load(id).diskCacheStrategy(DiskCacheStrategy.ALL).into(new GlideDrawableImageViewTarget(imageView, 1));
        }
    }


    /**
     * 设置缺省图片
     */
    public static void setDefaultImage(ImageView imageView) {
        imageView.setImageResource(R.drawable.no_photo);
    }

    public static void setWhiteBgImage(ImageView imageView){
        imageView.setImageResource(R.drawable.placeholder_img);
    }
}
