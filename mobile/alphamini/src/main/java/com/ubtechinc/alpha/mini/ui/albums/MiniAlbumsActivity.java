package com.ubtechinc.alpha.mini.ui.albums;

import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.footer.LoadingView;
import com.ubtech.utilcode.utils.FileUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.databinding.ActivityMiniAlbumsBinding;
import com.ubtechinc.alpha.mini.download.PicDownloadManager;
import com.ubtechinc.alpha.mini.entity.AlbumItem;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.ui.albums.AlbumsAdapter.IPhotoCheckedChangeListener;
import com.ubtechinc.alpha.mini.ui.utils.Utils;
import com.ubtechinc.alpha.mini.viewmodel.AlbumsViewModel;
import com.ubtechinc.alpha.mini.widget.ActionSheetDialog;
import com.ubtechinc.alpha.mini.widget.LoadingDialog;
import com.ubtechinc.alpha.mini.widget.PopView;
import com.ubtechinc.alpha.mini.widget.PopView.IAnmamtionListener;
import com.ubtechinc.alpha.mini.widget.RefreshView;
import com.ubtrobot.lib.sync.SyncGlobal;
import com.ubtrobot.lib.sync.utils.CommHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * @作者：liudongyang
 * @日期: 18/3/19 10:29
 * @描述: 相册列表UI（包括无数据页面）
 */

public class MiniAlbumsActivity extends BaseToolbarActivity implements IAnmamtionListener{

    private String TAG = getClass().getSimpleName();

    private static final int ALBUM_SIMPLE_MODE = 2000;

    private static final int ALBUM_EDIT_MODE   = 2001;

    private static final String defaultUrl = "/ubtdownload";//用于同步服务的默认缓存路径

    private ActivityMiniAlbumsBinding binding;

    private AlbumsViewModel mAlbumsViewModel;

    private AlbumsAdapter mAlbumsAdapter;

    private @AlbumEditMode int mCurrentMode = ALBUM_SIMPLE_MODE;

    @IntDef({ALBUM_EDIT_MODE, ALBUM_SIMPLE_MODE})
    @Target({PARAMETER, FIELD, LOCAL_VARIABLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AlbumEditMode {}


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_mini_albums);
        binding.setResponse(new EventReponse());
        binding.setNoPhoto(false);
        mAlbumsViewModel = new AlbumsViewModel();
        SyncGlobal.init(this.getApplicationContext(), CacheHelper.getRootPath(), CommHelper.getIM());//当前这个dir没有作用,并没有用到, 第三个参数是必须的吗？
        PicDownloadManager.get();
        initView();
        loadAllAlbumsData();
        EventBus.getDefault().register(this);
    }


    private void initView() {
        initToolbar(binding.titlebar, getString(R.string.albums), View.VISIBLE, true);
        initConnectFailedLayout(R.string.notifaction_phone, R.string.notifactionText);
        actionBtn.setImageResource(R.drawable.icon_top_edit);

        RefreshView headerView = new RefreshView(this);
        headerView.setTextColor(getResources().getColor(R.color.alexa_text_color_black));
        headerView.setRefreshingStr(getString(R.string.album_pull_hint));
        binding.refreshLayout.setHeaderView(headerView);
        LoadingView loadingView = new LoadingView(this);
        binding.refreshLayout.setBottomView(loadingView);
        binding.refreshLayout.setAutoLoadMore(true);
        binding.refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                syncAlbum();
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                loadMore();
            }
        });

        mAlbumsAdapter = new AlbumsAdapter(this, new IPhotoCheckedChangeListener() {

            @Override
            public void onCheckedPhotoSizeChange(int checkCount) {
                if (mCurrentMode == ALBUM_SIMPLE_MODE) {
                    return;
                }
                if (checkCount == 0) {
                    setTitle(getString(R.string.albums_edit_title));
                } else {
                    setTitle(getString(R.string.albums_edit_title_choosed, checkCount));
                }
                checkBottomClickable(checkCount);
            }

            @Override
            public void onSyncStateOfSelectAll(boolean isSelectAll) {
                syncSelectState(isSelectAll);
            }

            @Override
            public void onAlbumsTotalnumChange(int totalAlbumsCount) {
                if (totalAlbumsCount == 0) {
                    binding.rlNoDataLay.setVisibility(View.VISIBLE);
                } else {
                    binding.rlNoDataLay.setVisibility(View.GONE);
                }
                checkAlbumImgEditable(totalAlbumsCount);
            }

        });

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 4);

        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {//设置其列数
            @Override
            public int getSpanSize(int position) {
                List<Integer> timeIndexs = mAlbumsAdapter.getTimeIndex();
                for (Integer integer : timeIndexs) {
                    if (position == integer) {
                        return 4;//当前position所占列数
                    }
                }
                return 1;//当前position所占列数
            }
        });

        binding.rvAlbums.setLayoutManager(mLayoutManager);
        binding.rvAlbums.setAdapter(mAlbumsAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AlbumsDataChangeEvent event) {
        binding.refreshLayout.startRefresh();
    }

    private void loadAllAlbumsData() {
        mAlbumsViewModel.loadAllAlbumsData().observe(this, new Observer<LiveResult>() {
            @Override
            public void onChanged(@Nullable LiveResult liveResult) {
                switch (liveResult.getState()) {
                    case SUCCESS:
                        List<AlbumItem> lists = (List<AlbumItem>) liveResult.getData();
                        Log.i(TAG, "read from db lists : " + lists);
                        liveResult.removeObserver(this);
                        List<AlbumItem> showAlbums = pickUpNoCacheImg(lists);
                        checkAlbumImgEditable(showAlbums.size());
                        checkDataContent();
                        mAlbumsAdapter.pullRefreshPhotos(showAlbums);
                        if(getRobotConnect() && getNetworkConnectState()){
                            binding.refreshLayout.startRefresh();
                        }
                        break;
                    case FAIL:
                        liveResult.removeObserver(this);
                        break;
                    case LOADING:
                        break;
                }
            }

            /**
             * 删除数据库存在的Item，但是没有缓存的图片,防止空图片加载
             * @param dbAlbums
             * @return
             */
            private List<AlbumItem> pickUpNoCacheImg(List<AlbumItem> dbAlbums){
                if (dbAlbums == null || dbAlbums.size() == 0){
                    return new ArrayList<>();
                }
                List<AlbumItem> localItems = new ArrayList<>();
                for (AlbumItem item: dbAlbums){
                    if (FileUtils.isFileExists(item.getImageThumbnailUrl())){
                        localItems.add(item);
                    }
                }
                return localItems;
            }
        });
    }

    private void syncAlbum() {
        mAlbumsViewModel.syncAlbum().observe(this, new Observer<LiveResult>() {
            @Override
            public void onChanged(@Nullable LiveResult liveResult) {
                switch (liveResult.getState()) {
                    case SUCCESS:
                        liveResult.removeObserver(this);
                        binding.refreshLayout.finishRefreshing();
                        handleSyncAlbumsSuccessEvent(liveResult);
                        break;
                    case FAIL:
                        toastError("网络不给力，清稍后再试");
                        liveResult.removeObserver(this);
                        binding.refreshLayout.finishRefreshing();
                        checkDataContent(true);
                        break;
                    case LOADING:
                        break;
                }
            }

            private void handleSyncAlbumsSuccessEvent(@Nullable LiveResult liveResult) {
                List<AlbumItem> syncItems = (List<AlbumItem>) liveResult.getData();
                checkAlbumImgEditable(syncItems != null ? syncItems.size() : 0);
                checkImgHasLocalCache(syncItems);
                checkIfShowNoDataPage(syncItems);
                int refreshCount = mAlbumsAdapter.pullRefreshPhotos(syncItems);
                popToastHint(syncItems, refreshCount);
            }

            private void checkIfShowNoDataPage(List<AlbumItem> syncItems) {
                if (syncItems == null || syncItems.size() == 0){
                    binding.rlNoDataLay.setVisibility(View.VISIBLE);
                }else {
                    binding.rlNoDataLay.setVisibility(View.GONE);
                }
            }

            private void popToastHint(List<AlbumItem> syncItems, int refreshCount) {
                if (syncItems == null || syncItems.size() == 0){
                    Log.i(TAG, "robot has no albums!!!!" );
                    return;
                }

                if (refreshCount == 0){
                    toastSuccess(getString(R.string.album_no_photo_hint_msg));
                }else{
                    toastSuccess(getString(R.string.album_update, refreshCount));
                }
            }
        });
    }


    private void loadMore() {
        mAlbumsViewModel.loadMoreAlbums().observe(this, new Observer<LiveResult>() {
            @Override
            public void onChanged(@Nullable LiveResult liveResult) {
                List<AlbumItem> moreItems = (List<AlbumItem>) liveResult.getData();
                switch (liveResult.getState()) {
                    case SUCCESS:
                        liveResult.removeObserver(this);
                        checkImgHasLocalCache(moreItems);
                        binding.refreshLayout.finishLoadmore();
                        int count = mAlbumsAdapter.addPartPhotos(moreItems);
                        toastSuccess(getString(R.string.album_update, count));
                        break;
                    case FAIL:
                        liveResult.removeObserver(this);
                        binding.refreshLayout.finishLoadmore();
                        break;
                    case LOADING:
                        break;
                }
            }
        });
    }

    private void checkImgHasLocalCache(List<AlbumItem> refreshItems) {
        if (refreshItems == null || refreshItems.size() == 0){
            return;
        }
        List<AlbumItem> noCacheImageLists = new ArrayList<>();

        for (AlbumItem item: refreshItems){
            String path = item.getImageThumbnailUrl();
            boolean isExists = FileUtils.isFileExists(path);
            if (!isExists){
                noCacheImageLists.add(item);
            }
        }

        if (noCacheImageLists.size() > 0){
            downloadPic(noCacheImageLists, false);
        }
    }


    @Override
    protected void onAction(View view) {
        if (mCurrentMode == ALBUM_SIMPLE_MODE){
            enterInEditMode();
        }else{
            exitEditMode();
        }
    }

    private void exitEditMode() {
        mCurrentMode = ALBUM_SIMPLE_MODE;

        //底部bottomEditView消失
        if (mBottomEditView != null){
            mBottomEditView.dismiss();
        }

        //toolbar变化
        setTitle(getString(R.string.albums));

        backBtn.setVisibility(View.VISIBLE);
        actionBtn.setImageResource(R.drawable.icon_top_edit);

        //通知adapter退出编辑状态
        mAlbumsAdapter.setEditingState(false);

        //退出编辑状态时候，全选状态重置为false
        syncSelectState(false);

        binding.refreshLayout.setEnableRefresh(true);
    }

    private PopView mBottomEditView;
    private void enterInEditMode() {
        mCurrentMode = ALBUM_EDIT_MODE;

        //底部bottomEditView显示
        if (mBottomEditView == null){
            mBottomEditView = new BottomEditView(binding.rlBottomEdit);
            mBottomEditView.addAnimationListener(this);
        }
        mBottomEditView.show();

        //toolbar变化
        setTitle(getString(R.string.albums_edit_title));
        backBtn.setVisibility(View.GONE);
        actionBtn.setImageResource(R.drawable.ic_btn_back);

        //通知adapter进入编辑状态
        mAlbumsAdapter.setEditingState(true);

        binding.refreshLayout.setEnableRefresh(false);
    }


    private void deleteAlbums(final List<AlbumItem> albumItems) {
        LoadingDialog.getInstance(this).show();
        mAlbumsViewModel.deleteCheckedRobotAlbums(albumItems).observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                LiveResult liveResult = (LiveResult) o;
                switch (liveResult.getState()) {
                    case SUCCESS:
                        liveResult.removeObserver(this);
                        LoadingDialog.getInstance(MiniAlbumsActivity.this).dismiss();
                        mAlbumsAdapter.deleteCheckedPhotos();
                        exitEditMode();
                        break;
                    case FAIL:
                        liveResult.removeObserver(this);
                        ToastUtils.showShortToast(R.string.delete_failed);
                        LoadingDialog.getInstance(MiniAlbumsActivity.this).dismiss();
                        exitEditMode();
                        toastError(getString(R.string.delete_failed));
                        break;
                    case LOADING:
                        break;
                }
            }
        });
    }


    private void downloadPic(final List<AlbumItem> albumItems, final boolean needSaveToAlbums) {
        mAlbumsViewModel.downloadPic(albumItems).observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                LiveResult result = (LiveResult) o;
                String fileName = (String) result.getData();
                switch (result.getState()){
                    case SUCCESS:
                        mAlbumsAdapter.notifyItemDownloadFinish(fileName);
                        if (needSaveToAlbums) {
                            finishDownloadTask(CacheHelper.getThumbPath(fileName));
                        }
                        break;
                    case FAIL:
                        mAlbumsAdapter.notifyItemDownloadFinish(fileName);
                        downloadTaskFailed();
                        break;
                    case LOADING:
                        break;
                }
            }
        });
    }


    private void downloadOriginPic(final List<AlbumItem> albumItems){
        mAlbumsViewModel.downloadOriginPic(albumItems).observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                LiveResult result = (LiveResult) o;
                String fileName = (String) ((LiveResult) o).getData();
                switch (result.getState()){
                    case SUCCESS:
                        Log.i(TAG, "success download origin : " + fileName);
                        mAlbumsAdapter.notifyItemDownloadFinish(fileName);
                        finishDownloadTask(CacheHelper.getInstance().getItemOriginPath(fileName));
                        break;
                    case FAIL:
                        mAlbumsAdapter.notifyItemDownloadFinish(fileName);
                        downloadTaskFailed();
                        break;
                    case LOADING:
                        break;

                }
            }
        });
    }

    private void finishDownloadTask(String path) {
        if (mUtils == null) {
            mUtils = new DownloadSaveUtil(binding, this);
        }
        mUtils.saveImageToAlbums(path);
    }

    private void downloadTaskFailed(){
        if (mUtils == null) {
            mUtils = new DownloadSaveUtil(binding, this);
        }
        mUtils.finishTaskAndRefreshTopLayout();
    }

    private DownloadSaveUtil mUtils;
    private void downloadAndSaveToAbums(final List<AlbumItem> albumItems, boolean isOrigin) {
        mAlbumsAdapter.notifyUIChangeWhenDownloading();
        exitEditMode();

        if (isOrigin) {
            downloadOriginPic(albumItems);
        } else {
            downloadPic(albumItems, true);
        }

        if (mUtils == null) {
            mUtils = new DownloadSaveUtil(binding, this);
        }
        mUtils.setUpToplayout(albumItems.size());//创建头部
    }


    @Override
    public void animationStart() {
        enableActionBtn(false);
    }

    @Override
    public void animationEnd() {
        if (getRobotConnect() && getNetworkConnectState()) {//防止未链接的时候
            checkAlbumImgEditable(mTotalAlbumsCount);
        }
    }

    @Override
    protected void setConnectFailedLayout(boolean robotConnectState, boolean networkConnectState) {
        checkAlbumImgEditable(mTotalAlbumsCount);
        checkBottomClickable(mCheckPhotoSize);
        checkDataContent();
    }

    private void checkDataContent(boolean isNetworkSuck){
        if (mTotalAlbumsCount != 0){//有数据的情况
            binding.tvMobileWithoutNetwork.setVisibility(View.GONE);
            binding.tvMoibleNetworkSuck.setVisibility(View.GONE);
            binding.tvOffline.setVisibility(View.GONE);
            binding.refreshLayout.setVisibility(View.VISIBLE);
            super.setConnectFailedLayout(isRobotConnectState(), isNetworkConnectState());
            return;
        }

        if (binding.rlNoDataLay.getVisibility() == View.VISIBLE){
            super.setConnectFailedLayout(isRobotConnectState(), isNetworkConnectState());
            return;
        }

        if (!isNetworkConnectState()){
            binding.tvMobileWithoutNetwork.setVisibility(View.VISIBLE);
            binding.tvOffline.setVisibility(View.GONE);
            binding.tvMoibleNetworkSuck.setVisibility(View.GONE);
            binding.refreshLayout.setVisibility(View.GONE);
            return;
        }

        if (!isRobotConnectState()){
            binding.tvOffline.setVisibility(View.VISIBLE);
            binding.tvMobileWithoutNetwork.setVisibility(View.GONE);
            binding.tvMoibleNetworkSuck.setVisibility(View.GONE);
            binding.refreshLayout.setVisibility(View.GONE);
            return;
        }

        if (isNetworkSuck){//网络不给力的情况
            binding.tvMoibleNetworkSuck.setVisibility(View.VISIBLE);
            binding.tvOffline.setVisibility(View.GONE);
            binding.tvMobileWithoutNetwork.setVisibility(View.GONE);
            binding.refreshLayout.setVisibility(View.GONE);
            return;
        }

        binding.tvMobileWithoutNetwork.setVisibility(View.GONE);
        binding.tvOffline.setVisibility(View.GONE);
        binding.tvMoibleNetworkSuck.setVisibility(View.GONE);
        binding.refreshLayout.setVisibility(View.VISIBLE);
        binding.refreshLayout.startRefresh();
    }

    private void checkDataContent() {
        checkDataContent(false);
    }


    //只有在进入加载本地数据或者拉取了网络部分数据时或在界面删除相片的情况才会刷新TotalAlbumsCount值
    private int mTotalAlbumsCount = 0;
    private void checkAlbumImgEditable(int totalCount){
        this.mTotalAlbumsCount = totalCount;
        if (!isNetworkConnectState() || !isRobotConnectState()){
            enableActionBtn(false);
            return;
        }

        if (mTotalAlbumsCount == 0){
            enableActionBtn(false);
        }else{
            enableActionBtn(true);
        }
    }


    private void enableActionBtn(boolean enable){
        if (enable) {
            actionBtn.setEnabled(true);
            actionBtn.setAlpha(1f);
        }else{
            actionBtn.setEnabled(false);
            actionBtn.setAlpha(0.3f);
        }
    }


    private int mCheckPhotoSize = 0;
    private void checkBottomClickable(int checkedNumber){
        if (mCurrentMode == ALBUM_SIMPLE_MODE){
            return;
        }
        this.mCheckPhotoSize = checkedNumber;
        if (!isNetworkConnectState() || !isRobotConnectState()){
            binding.ivDownload.setClickable(false);
            binding.ivDownload.setImageAlpha(32);
            binding.ivDelete.setClickable(false);
            binding.ivDelete.setImageAlpha(32);
            return;
        }
        if (mCheckPhotoSize > 0){
            binding.ivDownload.setClickable(true);
            binding.ivDownload.setImageAlpha(255);
            binding.ivDelete.setClickable(true);
            binding.ivDelete.setImageAlpha(255);
        }else{
            binding.ivDownload.setClickable(false);
            binding.ivDownload.setImageAlpha(32);
            binding.ivDelete.setClickable(false);
            binding.ivDelete.setImageAlpha(32);
        }
    }



    private boolean mIsSelectAll = false;
    private void syncSelectState(boolean currentSelectState){
        mIsSelectAll = currentSelectState;
        binding.setIsSelectedAll(currentSelectState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PicDownloadManager.get().release();
        EventBus.getDefault().unregister(this);
    }

    public class EventReponse{

        public void doSelectAllOrCancelSelectAll(View view){
            syncSelectState(!mIsSelectAll);
            mAlbumsAdapter.selectAllOrDeselectAll(mIsSelectAll);
        }

        public void downloadPhotos(View view){
            final List<AlbumItem> checkedItems = mAlbumsAdapter.cloneCheckedPhotos();

            double thumbSize  = 0;
            double originSize = 0;
            for (AlbumItem item : checkedItems){
                thumbSize  += item.computeThumbSize();
                originSize += item.computSize();
            }

            String messageOrigin   = MiniAlbumsActivity.this.getResources().getString(R.string.origin_size, Utils.byteNumerToMemerySize(originSize)) ;
            String messageThumb    = MiniAlbumsActivity.this.getResources().getString(R.string.thumb_size,  Utils.byteNumerToMemerySize(thumbSize));

            new ActionSheetDialog(MiniAlbumsActivity.this)
                    .builder()
                    .setCancelable(true)
                    .setCanceledOnTouchOutside(true)
                    .setTitle(MiniAlbumsActivity.this.getString(R.string.album_save_title, checkedItems.size()))
                    .addSheetItem(messageOrigin,
                            ActionSheetDialog.SheetItemColor.Blue,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    downloadAndSaveToAbums(checkedItems, true);
                                }
                            })
                    .addSheetItem(messageThumb,
                            ActionSheetDialog.SheetItemColor.Blue,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    downloadAndSaveToAbums(checkedItems, false);
                                }
                            }).show();
        }

        public void deletePhotos(View view){
            final List<AlbumItem> items = mAlbumsAdapter.cloneCheckedPhotos();
            new ActionSheetDialog(MiniAlbumsActivity.this)
                    .builder()
                    .setCancelable(true)
                    .setCanceledOnTouchOutside(true)
                    .setSpaceVisible(true)
                    .setTitle(getString(R.string.albums_delete_hint))
                    .addSheetItem(getString(R.string.albums_robot_delete_dialog_title, items.size()),
                            ActionSheetDialog.SheetItemColor.Red,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    deleteAlbums(items);
                                }
                            }).show();
        }

    }


    class BottomEditView extends PopView{

        public BottomEditView(View view) {
            super(view);
        }
        @Override
        protected void setViewContent() {

        }

    }

}
