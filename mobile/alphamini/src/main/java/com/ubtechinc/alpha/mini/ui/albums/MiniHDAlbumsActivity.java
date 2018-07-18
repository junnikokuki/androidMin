package com.ubtechinc.alpha.mini.ui.albums;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;
import com.ubtech.utilcode.utils.FileUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.databinding.AlbumsActivityHdBinding;
import com.ubtechinc.alpha.mini.entity.AlbumItem;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.ui.utils.Utils;
import com.ubtechinc.alpha.mini.utils.Constants;
import com.ubtechinc.alpha.mini.utils.Tools;
import com.ubtechinc.alpha.mini.viewmodel.AlbumsViewModel;
import com.ubtechinc.alpha.mini.widget.ActionSheetDialog;
import com.ubtechinc.alpha.mini.widget.DetailImageView;
import com.ubtechinc.alpha.mini.widget.LoadingDialog;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.alpha.mini.widget.ShareView;
import com.ubtechinc.alpha.mini.widget.ZoomImageView.OnSingleTapConfirmed;
import com.yanzhenjie.permission.AndPermission;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.ubtechinc.alpha.mini.ui.PageRouter.INTENT_KEY_CURRENT_ALBUM;
import static com.ubtechinc.alpha.mini.ui.PageRouter.INTENT_KEY_ROBOT_ALBUMS;

/**
 * @作者：liudongyang
 * @日期: 18/3/19 11:02
 * @描述: 详情页面
 */

public class MiniHDAlbumsActivity extends BaseToolbarActivity{

    private List<AlbumItem> mAllItems = new ArrayList<>();

    private AlbumsActivityHdBinding binding;

    private AlbumsViewModel mAlbumsViewModel;

    private AlbumsPagerAdapter mPagerAdapter;

    private ShareView mShareView;

    private boolean isZoomMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.albums_activity_hd);
        binding.setIsZoomMode(isZoomMode);
        binding.setResponse(new EventReponse());
        binding.setIsShareMode(false);
        mAlbumsViewModel = new AlbumsViewModel();
        initItems();
        setClickListener();
        initView();
    }

    private void setClickListener() {//防止toolbar和bottomlayout点击穿透
        binding.rlDeleteLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.rlTopTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initView() {
        initToolbar(binding.rlTopTitle, "", View.GONE, false);
        initConnectFailedLayout(R.string.network_unavaliable_album, R.string.robot_offline);

        mShareView = new ShareView(this, binding);
        mShareView.setCurItem(mAllItems.get(mCurItemIndex));

        mPagerAdapter = new AlbumsPagerAdapter(this, mAlbumsViewModel);
        mPagerAdapter.setContentNetworkState(isNetworkConnectState(), isRobotConnectState());
        mPagerAdapter.setLoadImgCallBack(new DetailImageView.DownloadHDImgCallBack() {
            @Override
            public void onSuccLoadImg(int position) {
                if (position == mCurItemIndex) {
                    setBottomLayoutClickable();
                }
            }

            @Override
            public void onErrorLoadImg(int position) {
                if (position == mCurItemIndex) {
                    setBottomLayoutClickable();
                }
            }
        });
        mPagerAdapter.setSingleTagListener(new OnSingleTapConfirmed() {
            @Override
            public void onSingleTapConfirmed() {
                isZoomMode = !isZoomMode ;
                binding.setIsZoomMode(isZoomMode);
                DetailImageView imageView = binding.galley.findViewById(mCurItemIndex);
                imageView.setIsZoomMode(isZoomMode);
                if(isZoomMode){
                    binding.galley.setBackgroundColor(Color.BLACK);
                    ImmersionBar.with(MiniHDAlbumsActivity.this)
                            .statusBarColor(R.color.status_black)
                            .hideBar(BarHide.FLAG_SHOW_BAR)
                            .fitsSystemWindows(true)
                            .init();
                }else{
                    binding.galley.setBackgroundColor(Color.WHITE);
                    ImmersionBar.with(MiniHDAlbumsActivity.this)
                            .statusBarDarkFont(true)
                            .statusBarColor(android.R.color.white)
                            .hideBar(BarHide.FLAG_SHOW_BAR)
                            .fitsSystemWindows(true)
                            .init();
                }
            }

            @Override
            public void onDoubleTap() {

            }
        });

        binding.galley.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
        binding.galley.setOffscreenPageLimit(0);
        binding.galley.setAdapter(mPagerAdapter);
        binding.galley.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurItemIndex = position;
                AlbumItem currentItem = mAllItems.get(mCurItemIndex);
                mShareView.setCurItem(currentItem);
                setBottomLayoutClickable();

                DetailImageView imageView = binding.galley.findViewById(position);
                if (imageView != null){
                    imageView.setIsZoomMode(isZoomMode);
                    imageView.setOriginBtnVisiable(isNetworkConnectState(), isRobotConnectState());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mPagerAdapter.setAlbumsList(mAllItems);
        binding.galley.setCurrentItem(mCurItemIndex);
    }


    private int mCurItemIndex;
    private void initItems() {
        List<AlbumItem> deliveredItems = getIntent().getParcelableArrayListExtra(INTENT_KEY_ROBOT_ALBUMS);
        AlbumItem items = getIntent().getParcelableExtra(INTENT_KEY_CURRENT_ALBUM);

        for (int i = 0; i < deliveredItems.size(); i++) {
            AlbumItem item = deliveredItems.get(i);
            if (item.getItemType() == AlbumsAdapter.ITEM_TIME_TYPE) {
                continue;
            }
            mAllItems.add(item);
        }

        mCurItemIndex = indexFirstItem(items);
    }


    private int indexFirstItem(AlbumItem curItems) {
        for (int i = 0; i < mAllItems.size(); i++){
            if (mAllItems.get(i).equals(curItems)) {
                return i;
            }
        }
        return 0;
    }


    private void deleteCurrentItem(final AlbumItem item){
        LoadingDialog.getInstance(this).show();
        mAlbumsViewModel.deleteOneAlbumInRobot(item).observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                LiveResult liveResult = (LiveResult) o;
                switch (liveResult.getState()) {
                    case SUCCESS:
                        LoadingDialog.getInstance(MiniHDAlbumsActivity.this).dismiss();
                        liveResult.removeObserver(this);
                        mAllItems.remove(item);
                        //相片数据源发生了变化,需要通知到另外一端
                        EventBus.getDefault().post(new AlbumsDataChangeEvent());
                        if (mAllItems.size() == 0){
                            finish();
                        }
                        mPagerAdapter.setAlbumsList(mAllItems);
                        break;
                    case FAIL:
                        LoadingDialog.getInstance(MiniHDAlbumsActivity.this).dismiss();
                        liveResult.removeObserver(this);
                        toastError(getString(R.string.delete_failed));
                        break;
                    case LOADING:
                        break;
                }
            }
        });
    }

    // TODO: 18/4/19 网络恢复的时候加载图片的时候存在问题
    @Override
    protected void setConnectFailedLayout(boolean robotConnectState, boolean networkConnectState) {
        setBottomLayoutClickable();
        setOriginButtonClickable();
        setContentLayout();
    }

    private void setOriginButtonClickable() {
        if (binding.galley != null){
            DetailImageView imageView = binding.galley.findViewById(mCurItemIndex);
            if (imageView != null){
                imageView.setOriginBtnVisiable(isNetworkConnectState(), isRobotConnectState());
            }
        }
        if (mPagerAdapter != null){
            mPagerAdapter.setContentNetworkState(isNetworkConnectState(), isRobotConnectState());
        }
    }

    private void setContentLayout(){
        AlbumItem item = mAllItems.get(mCurItemIndex);
        if (FileUtils.isFileExists(item.getImageHDUrl()) || FileUtils.isFileExists(item.getOriginUrl())){
            binding.tvConnectError.setVisibility(View.GONE);
            binding.tvConnectSubError.setVisibility(View.GONE);
            binding.tvNetworkError.setVisibility(View.GONE);
            binding.galley.setVisibility(View.VISIBLE);
            super.setConnectFailedLayout(isRobotConnectState(), isNetworkConnectState());
            return;
        }

        if (!isNetworkConnectState()){
            binding.tvNetworkError.setVisibility(View.VISIBLE);
            binding.tvConnectError.setVisibility(View.GONE);
            binding.tvConnectSubError.setVisibility(View.GONE);
            binding.galley.setVisibility(View.GONE);
            return;
        }

        if (!isRobotConnectState()){
            binding.tvNetworkError.setVisibility(View.GONE);
            binding.tvConnectError.setVisibility(View.VISIBLE);
            binding.tvConnectSubError.setVisibility(View.VISIBLE);
            binding.galley.setVisibility(View.GONE);
            return;
        }

        binding.tvConnectError.setVisibility(View.GONE);
        binding.tvConnectSubError.setVisibility(View.GONE);
        binding.tvNetworkError.setVisibility(View.GONE);
        binding.galley.setVisibility(View.VISIBLE);
    }

    private void setBottomLayoutClickable() {
        if (!mAllItems.get(mCurItemIndex).isIsLoadSucc()){
            binding.ivDownload.setEnabled(false);
            binding.ivDownload.setAlpha(0.3f);
            binding.ivShare.setEnabled(false);
            binding.ivShare.setAlpha(0.3f);
            binding.ivDelete.setEnabled(false);
            binding.ivDelete.setAlpha(0.3f);
            return;
        }

        if (isNetworkConnectState()) {
            binding.ivShare.setEnabled(true);
            binding.ivShare.setAlpha(1f);
        } else {
            binding.ivShare.setEnabled(false);
            binding.ivShare.setAlpha(0.3f);
        }

        if (isNetworkConnectState() && isRobotConnectState()) {
            binding.ivDelete.setEnabled(true);
            binding.ivDelete.setAlpha(1f);
        } else {
            binding.ivDelete.setEnabled(false);
            binding.ivDelete.setAlpha(0.3f);
        }

        AlbumItem item = mAllItems.get(mCurItemIndex);
        if (FileUtils.isFileExists(item.getOriginUrl()) || FileUtils.isFileExists(item.getImageHDUrl())) {//原图或者连接上机器人
            binding.ivDownload.setEnabled(true);
            binding.ivDownload.setAlpha(1f);
        } else {
            binding.ivDownload.setEnabled(false);
            binding.ivDownload.setAlpha(0.3f);
        }
    }

    public class EventReponse {

        public void saveOrigin2SysAlbums(View view) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (AndPermission.hasPermission(MiniHDAlbumsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    saveToAlbums();
                } else {
                    requestPermission();
                }
            } else {
                saveToAlbums();
            }
        }

        private void saveToAlbums(){
            AlbumItem item = mAllItems.get(mCurItemIndex);
            if (FileUtils.isFileExists(item.getOriginUrl())){
                toastSuccess(getString(R.string.album_save_success));
                Tools.saveImageToAblum(MiniHDAlbumsActivity.this,new File(item.getOriginUrl()));
                return;
            }
            if (FileUtils.isFileExists(item.getImageHDUrl())){
                toastSuccess(getString(R.string.album_save_success));
                Tools.saveImageToAblum(MiniHDAlbumsActivity.this,new File(item.getImageHDUrl()));
                return;
            }
            toastError(getString(R.string.album_save_failed));
        }

        public void dismissShareView(View view){
            mShareView.dismiss();
        }

        public void shareImage(View view) {
            mShareView.show();
        }

        public void deleteImage(View view) {
            new ActionSheetDialog(MiniHDAlbumsActivity.this)
                    .builder()
                    .setCancelable(true)
                    .setCanceledOnTouchOutside(true)
                    .setSpaceVisible(true)
                    .setTitle(getString(R.string.albums_delete_hint))
                    .addSheetItem(getString(R.string.albums_delete),
                            ActionSheetDialog.SheetItemColor.Red,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    deleteCurrentItem(mAllItems.get(mCurItemIndex));
                                }
                            }).show();
        }

    }

    private CameraPermission permission;
    public void requestPermission() {
        if (permission == null) {
            permission = new CameraPermission(this);
        }
        permission.request(new CameraPermission.PermissionCamerasCallback() {
            @Override
            public void onSuccessful() {
                ToastUtils.showShortToast("获取权限成功");

            }

            @Override
            public void onFailure() {
                //空实现
                ToastUtils.showShortToast("获取权限失败");
                Utils.getAppDetailSettingIntent(MiniHDAlbumsActivity.this);
            }

            @Override
            public void onRationSetting() {
                popPermissionDialog();
            }


            public void popPermissionDialog(){
                final MaterialDialog dialog = new MaterialDialog(MiniHDAlbumsActivity.this);
                dialog.setTitle(R.string.albums_sdcard)
                        .setMessage(R.string.albums_save_msg)
                        .setNegativeButton(R.string.cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton(R.string.mobile_contacts_name_permission_position_button, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Utils.getAppDetailSettingIntent(MiniHDAlbumsActivity.this);
                    }
                }).show();
            }
        });
    }
}
