package com.ubtechinc.alpha.mini.ui.albums;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.ubtechinc.alpha.mini.entity.AlbumItem;
import com.ubtechinc.alpha.mini.viewmodel.AlbumsViewModel;
import com.ubtechinc.alpha.mini.widget.DetailImageView;
import com.ubtechinc.alpha.mini.widget.ZoomImageView.OnSingleTapConfirmed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @作者：liudongyang
 * @日期: 18/3/28 14:14
 * @描述: 相册列表的图片
 */

public class AlbumsPagerAdapter extends PagerAdapter{

    private List<AlbumItem> mItems = new ArrayList<>();

    private Context mCtx;

    private OnSingleTapConfirmed mListener;

    private AlbumsViewModel viewModel;

    private DetailImageView.DownloadHDImgCallBack mCallback;

    public AlbumsPagerAdapter(Context ctx, AlbumsViewModel viewModel) {
        this.mCtx = ctx;
        this.viewModel = viewModel;
    }

    public void setAlbumsList(List<AlbumItem> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void setLoadImgCallBack(DetailImageView.DownloadHDImgCallBack callback){
        this.mCallback = callback;
    }


    public void setSingleTagListener(OnSingleTapConfirmed listener){
        this.mListener = listener;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    private boolean isNetworkOk;
    private boolean isRobotConnect;
    public void setContentNetworkState(boolean isNetwork, boolean isRobotConnect){
        this.isNetworkOk = isNetwork;
        this.isRobotConnect = isRobotConnect;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        DetailImageView detailView = new DetailImageView(mCtx, viewModel);
        detailView.setLoadImgCallBack(mCallback,position);
        detailView.displayImageView(mItems.get(position));
        detailView.setId(position);
        detailView.initOriginButtonEnable(isNetworkOk, isRobotConnect);
        container.addView(detailView);
        return detailView;
    }


    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        DetailImageView mImageView = (DetailImageView) object;//最后一个View被删除了,怎么显示？
        if (mImageView != null) {
            mImageView.setSingleTapListener(mListener);
        }
        super.setPrimaryItem(container, position, object);
    }



}
