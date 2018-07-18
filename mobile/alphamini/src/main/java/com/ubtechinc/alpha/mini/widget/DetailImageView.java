package com.ubtechinc.alpha.mini.widget;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ubtech.utilcode.utils.FileUtils;
import com.ubtech.utilcode.utils.NetworkUtils;
import com.ubtech.utilcode.utils.SizeUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.entity.AlbumItem;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.entity.observable.PicDownloadResult;
import com.ubtechinc.alpha.mini.utils.NetUtil;
import com.ubtechinc.alpha.mini.viewmodel.AlbumsViewModel;

import java.io.File;


public class DetailImageView extends RelativeLayout{

    private static final int ORIGIN = 10000;
    private static final int HD = 10001;

    private Context         mContext;
    private Button          mOriginBtn;
    private TextView        mTvRetry;
    private ProgressBar     mPbBar;
    private ZoomImageView   mPhotoView;
    private AlbumItem       mItem;
    private AlbumsViewModel mViewModel;
    private DownloadHDImgCallBack mCallBack;
    private boolean isOriginImage;

    private int width;
    private int height;
    private int position = -1;



    public DetailImageView(Context context, AlbumsViewModel mViewModel) {
        super(context);
        this.mContext = context;
        this.mViewModel = mViewModel;
        initIsViewPage();
    }


    public DetailImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initIsViewPage();
    }


    public void setLoadImgCallBack(DownloadHDImgCallBack callBack, int position){
        this.mCallBack = callBack;
        this.position  = position;
    }


    private void initIsViewPage() {
        LayoutInflater.from(mContext).inflate(R.layout.ablum_pic_detail, this, true);
        mPhotoView = findViewById(R.id.detail_image);
        mOriginBtn = findViewById(R.id.btn_check_origin);
        mPbBar = findViewById(R.id.pb_loading);
        mTvRetry = findViewById(R.id.tv_retry);

        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        width =  outMetrics.widthPixels;
        height = outMetrics.heightPixels;
    }


    public void initOriginButtonEnable(boolean isNetworkState,boolean isRobotConnectState){
        setOriginBtnVisiable(isNetworkState, isRobotConnectState);
    }


    public void displayImageView(final AlbumItem item) {
        this.mItem = item;
        if (FileUtils.isFileExists(item.getOriginUrl())){
            isOriginImage = true;
            mOriginBtn.setVisibility(View.GONE);
            showImage(item, ORIGIN);
            return;
        }
        if (FileUtils.isFileExists(item.getImageHDUrl())){
            isOriginImage = false;
            mOriginBtn.setVisibility(View.VISIBLE);
            mOriginBtn.setText(getContext().getString(R.string.btn_get_origin, item.computeOriginSize()));
            setOriginBtnClickListener(item);
            showImage(item, HD);
            return;
        }
        createDownloadHDRequest(item);
    }


    private void setOriginBtnClickListener(final AlbumItem item) {
        mOriginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isConnected() && NetUtil.isWiFi(getContext())) {//有网，并且不是wifi
                    downloadOrigin();
                    return;
                }

                final MaterialDialog dialog = new MaterialDialog(getContext());
                dialog.setTitle(R.string.network_mobile)
                        .setMessage(R.string.network_mobile_message)
                        .setNegativeButton(R.string.simple_message_cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton(R.string.simple_message_comfire, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        downloadOrigin();
                    }
                }).show();
            }

            private void downloadOrigin() {
                mViewModel.downloadOriginPic(item).observe((LifecycleOwner) mContext, new Observer() {
                    @Override
                    public void onChanged(@Nullable Object o) {
                        PicDownloadResult liveResult = (PicDownloadResult) o;
                        switch (liveResult.getCurrenState()){
                            case PROGRESSING:
                                String progressing = getContext().getString(R.string.btn_get_origin_progress, ((PicDownloadResult) o).getProgress() + "");
                                mOriginBtn.setText(progressing);
                                break;
                            case FAIL:
                                String failString = getContext().getString(R.string.btn_get_origin, item.computeOriginSize());
                                mOriginBtn.setText(failString);
                                liveResult.removeObserver(this);
                                ToastUtils.showShortToast("下载原图失败,请重试");
                                break;
                            case SUCCESS:
                                mOriginBtn.setText(getContext().getString(R.string.albums_download_finish));
                                displayImageView(item);
                                liveResult.removeObserver(this);
                                break;
                        }
                    }
                });
            }
        });
    }




    public void setIsZoomMode(boolean isZoomMode){
        if (isZoomMode) {
            mOriginBtn.setVisibility(View.GONE);
            return;
        }
        if (isOriginImage) {
            mOriginBtn.setVisibility(View.GONE);
            return;
        }
        if(isDownloading){
            mOriginBtn.setVisibility(View.GONE);
            return;
        }
        if (!FileUtils.isFileExists(mItem.getImageHDUrl())){
            mOriginBtn.setVisibility(View.GONE);
            return;
        }
        mOriginBtn.setVisibility(View.VISIBLE);
    }


    boolean isDownloading;
    private void createDownloadHDRequest(final AlbumItem item) {
        isDownloading = true;
        mViewModel.downloadHDPic(item).observe((LifecycleOwner) mContext, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                LiveResult liveResult = (LiveResult) o;
                switch (liveResult.getState()){
                    case SUCCESS:
                        isDownloading = false;
                        liveResult.removeObserver(this);
                        mPbBar.setVisibility(View.GONE);
                        displayImageView(item);
                        break;
                    case FAIL:
                        if (liveResult.getErrorCode() == 100){
                            Log.d("0426", "onChanged:  fail  timeout" + item.getImageId());
                        }
                        isDownloading = false;
                        liveResult.removeObserver(this);
                        handleHDDownloadFailed();
                        break;
                    case LOADING:
                        mPbBar.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }


    private void handleHDDownloadFailed() {
        mItem.setIsLoadSucc(false);
        mCallBack.onErrorLoadImg(position);

        mPbBar.setVisibility(View.GONE);
        mOriginBtn.setVisibility(View.GONE);
        mTvRetry.setVisibility(View.VISIBLE);
        mPhotoView.setPicLoadResult(false);

        mTvRetry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvRetry.setVisibility(View.GONE);
                createDownloadHDRequest(mItem);
            }
        });

        String text = mContext.getString(R.string.album_show_failed);
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.open_ble)), 5, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvRetry.setText(style);
    }


    private void showImage(AlbumItem item, int type){
        String path;
        if (type == ORIGIN){
            path = item.getOriginUrl();
        }else{
            path = item.getImageHDUrl();
        }
        if (TextUtils.isEmpty(path)){
            Log.e("0402", "showImage: " + path);
            return;
        }
        mPhotoView.setPicLoadResult(true);
        item.setIsLoadSucc(true);
        mCallBack.onSuccLoadImg(position);

        DrawableRequestBuilder<String> thumbnailRequest = Glide.with(mContext).
                load(path).override(width,width*3/4).centerCrop();

        Glide.with(mContext).load(new File(path)).skipMemoryCache(false).override(width,width * 3/4)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE).listener(new RequestListener<File, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, File file, Target<GlideDrawable> target, boolean b) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable glideDrawable, File file, Target<GlideDrawable> target, boolean b, boolean b1) {
                return false;
            }
        }).thumbnail(thumbnailRequest).centerCrop().into(mPhotoView);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b); //顺序不可调换
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            if (childView.getId() == R.id.btn_check_origin) {
                int left = width / 2 - mOriginBtn.getMeasuredWidth() / 2;
                int top  = (width * 3) / 8 + height / 2 + SizeUtils.dp2px(40);
                childView.layout(left, top, left + mOriginBtn.getMeasuredWidth(), top + mOriginBtn.getMeasuredHeight());
            }
        }
    }


    public void setSingleTapListener(ZoomImageView.OnSingleTapConfirmed singleTapListener) {
        mPhotoView.setSingleTapListener(singleTapListener);
    }


    public void setOriginBtnVisiable(boolean isNetworkOk, boolean isConnectRobot) {
        if (!isNetworkOk || !isConnectRobot) {
            mOriginBtn.setEnabled(false);
        } else {
            mOriginBtn.setEnabled(true);
        }
    }


    public interface DownloadHDImgCallBack{
         void onSuccLoadImg(int position);

         void onErrorLoadImg(int position);
    }





}
