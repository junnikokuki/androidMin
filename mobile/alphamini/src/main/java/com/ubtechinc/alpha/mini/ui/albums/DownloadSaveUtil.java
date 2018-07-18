package com.ubtechinc.alpha.mini.ui.albums;

import android.content.Context;
import android.view.View;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.databinding.ActivityMiniAlbumsBinding;
import com.ubtechinc.alpha.mini.utils.Tools;
import com.ubtechinc.alpha.mini.widget.NotifactionView;

import java.io.File;

/**
 * @作者：liudongyang
 * @日期: 18/4/2 15:51
 * @描述: 刷新头部的
 */

public class DownloadSaveUtil {

    private ActivityMiniAlbumsBinding binding;

    private int mCurrent = 0;

    private int mTotalSize = 0;


    private Context mCtx;


    public DownloadSaveUtil(ActivityMiniAlbumsBinding binding, Context ctx) {
        this.binding = binding;
        this.mCtx = ctx;
    }



    public void setUpToplayout(int totalSize) {
        this.mTotalSize = totalSize;
        mCurrent = 0;

        binding.rlDownload.setVisibility(View.VISIBLE);
        String message = String.format(mCtx.getResources().getString(R.string.album_dowand_count), mCurrent, mTotalSize);
        binding.tvDownload.setText(message);
        binding.pbDowanload.setMax(totalSize);
        binding.pbDowanload.setProgress(mCurrent);

        //禁止下拉
        binding.refreshLayout.setEnableRefresh(false);
    }

    public void finishTaskAndRefreshTopLayout() {
        mCurrent++;
        if (mCurrent == mTotalSize){
            binding.rlDownload.setVisibility(View.GONE);
            //可以下拉
            binding.refreshLayout.setEnableRefresh(true);

            succe(mCtx.getString(R.string.album_save_success));
            return;
        }
        String message = String.format(mCtx.getResources().getString(R.string.album_dowand_count), mCurrent, mTotalSize);
        binding.tvDownload.setText(message);
        binding.pbDowanload.setMax(mTotalSize);
        binding.pbDowanload.setProgress(mCurrent);
    }


    public void saveImageToAlbums(String path){
        Tools.saveImageToAblum(mCtx, new File(path));
        finishTaskAndRefreshTopLayout();
    }


    private void succe(String msg) {
        NotifactionView notifactionView = new NotifactionView(mCtx);
        notifactionView.setNotifactionText(msg, R.drawable.ic_toast_succeed);
    }
}
