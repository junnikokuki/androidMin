//package com.ubtechinc.alpha.mini.ui;
//
//import android.arch.lifecycle.Observer;
//import android.content.Intent;
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Parcel;
//import android.support.annotation.NonNull;
//import android.support.annotation.RequiresApi;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.gyf.barlibrary.BarHide;
//import com.gyf.barlibrary.ImmersionBar;
//import com.sina.weibo.sdk.api.ImageObject;
//import com.sina.weibo.sdk.api.WeiboMessage;
//import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
//import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
//import com.sina.weibo.sdk.api.share.WeiboShareSDK;
//import com.tencent.connect.share.QQShare;
//import com.tencent.connect.share.QzoneShare;
//import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
//import com.tencent.mm.opensdk.modelmsg.WXImageObject;
//import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
//import com.tencent.mm.opensdk.openapi.IWXAPI;
//import com.tencent.mm.opensdk.openapi.WXAPIFactory;
//import com.tencent.tauth.Tencent;
//import com.ubtech.utilcode.utils.FileUtils;
//import com.ubtech.utilcode.utils.network.NetworkHelper;
//import com.ubtechinc.alpha.mini.R;
//import com.ubtechinc.alpha.mini.common.BaseActivity;
//import com.ubtechinc.alpha.mini.download.DownloadService;
//import com.ubtechinc.alpha.mini.download.ImRequest;
//import com.ubtechinc.alpha.mini.entity.RobotInfo;
//import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
//import com.ubtechinc.alpha.mini.im.PushImEngine;
//import com.ubtechinc.alpha.mini.utils.Constants;
//import com.ubtechinc.alpha.mini.utils.DownLoadStatus;
//import com.ubtechinc.alpha.mini.utils.PermissionStorageRequest;
//import com.ubtechinc.alpha.mini.utils.Tools;
//import com.ubtechinc.alpha.mini.viewmodel.ImageModel;
//import com.ubtechinc.alpha.mini.widget.DetailImageView;
//import com.ubtechinc.alpha.mini.widget.HackyViewPager;
//import com.ubtechinc.alpha.mini.widget.LoadDialog;
//import com.ubtechinc.alpha.mini.widget.MaterialDialog;
//import com.ubtechinc.alpha.mini.widget.NotifactionView;
//import com.ubtechinc.alpha.mini.widget.ShareView;
//import com.ubtechinc.alpha.mini.widget.ZoomImageView;
//import com.ubtrobot.common.utils.XLog;
//import com.ubtrobot.lib.sync.engine.QueryPacket;
//import com.ubtrobot.lib.sync.engine.ResultPacket;
//import com.ubtrobot.lib.sync.engine.socket.callback.PushSocketCallback;
//import com.ubtrobot.param.sync.Consts;
//import com.ubtrobot.param.sync.Files;
//import com.ubtrobot.param.sync.Params;
//import com.yanzhenjie.permission.AndPermission;
//import com.yanzhenjie.permission.Permission;
//import com.yanzhenjie.permission.SettingService;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//
//public class AblumDetailActivity extends BaseActivity implements View.OnClickListener,ZoomImageView.OnSingleTapConfirmed {
//    private static final int HANDLE_SAVE_FINISH = 0x0011;
//    private static final int HANDLE_SAVE_DISMISS = 0x0012;
//    private static final String TAG = AblumDetailActivity.class.getSimpleName();
//    private RelativeLayout editLay;
//    private ImageView btn_delete;
//    private ImageView btn_download;
//    private Map<String, DownLoadStatus> downLoadMap = new HashMap<String, DownLoadStatus>();
//    private List<ImageModel> picList = new ArrayList<ImageModel>();
//    private PagerAdapter adpeter;
//    private HackyViewPager galley;
//    private String imageName;
//    private int position;
//    private RelativeLayout mNetErrorView;
//    public static final String NETSTASTUS = "nestatus";
//    public ExecutorService mSingleExecutor = Executors.newSingleThreadExecutor();
//
//    private PermissionStorageRequest mPermissionStorageRequest;
//    private MaterialDialog materialDialog;
//    private int index = -1;
//    private NotifactionView notifactionView;
//    private ShareView mPop;
//    private ImageView share;
//    private IWeiboShareAPI mWeiboShareAPI;
//    private DetailImageView detailImageView ;
//
//    private int statusRobot ;
//    private RelativeLayout  topLine ;
//    private View line ;
//    private TextView title ;
//    private TextView btn_back;
//    private boolean isInSingleTap =false ;
//    private MaterialDialog deleteDialog;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.ablum_detail);
//        initView();
//    }
//
//    private void initView() {
////        mPop = new ShareView(this);
//        mNetErrorView = (RelativeLayout) findViewById(R.id.net_error_view);
//        imageName = getIntent().getStringExtra("imageName");
//        picList = (List<ImageModel>) getIntent().getSerializableExtra("picList");
//        position = getIntent().getIntExtra("pisition", -1);
//        editLay = (RelativeLayout) findViewById(R.id.btn_deleteLay);
//        title = (TextView) findViewById(R.id.authorize_title);
//        btn_back=findViewById(R.id.btn_back);
//        btn_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                doBack();
//            }
//        });
//        btn_delete = (ImageView) findViewById(R.id.btn_delete_icon);
//        btn_download = findViewById(R.id.btn_download_icon);
//        btn_delete.setOnClickListener(this);
//        btn_download.setOnClickListener(this);
//        topLine=findViewById(R.id.top_title);
//        View rightBtn =findViewById(R.id.btn_top_right);
//        if(rightBtn != null){
//            rightBtn.setVisibility(View.GONE);
//        }
//        line=findViewById(R.id.line);
//        int position = 0;
//        for (int i = 0; i < picList.size(); i++) {
//            DownLoadStatus ds = new DownLoadStatus();
//            ds.setDownloadProgress("0.0%");
//            ds.setDownloadStaStatus(0);
//            downLoadMap.put(picList.get(i).getImage_id(), ds);
//            if (picList.get(i).getImage_id().equals(imageName)) {
//                position = i;
//            }
//        }
//       // title.setText(position + 1 + "/" + picList.size());
//
//        share = findViewById(R.id.btn_share_icon);
//        share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                share();
//            }
//        });
//
//        galley = (HackyViewPager) findViewById(R.id.galley);
//        galley.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
//        galley.setOffscreenPageLimit(0);
//
//
//        adpeter = new PagerAdapter() {
//            @Override
//            public int getCount() {
//                return picList.size();
//            }
//
//            @Override
//            public boolean isViewFromObject(View view, Object object) {
//                return view == object;
//            }
//
//            @Override
//            public Object instantiateItem(ViewGroup container, int position) {
//                Log.d(TAG, "position = " + position);
////                DetailImageView view = new DetailImageView(AblumDetailActivity.this, true);
//                // ImageView photoView = (ImageView) view.getImageView();
//                // photoView.enable();
//                // photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
////                view.displayImageView(AblumDetailActivity.this, picList.get(position).getImage_original_url(), view.getImageView());
////                container.addView(view);
////                view.setTag(position);
//                return null;
//            }
//
//            @Override
//            public void destroyItem(ViewGroup container, int position, Object object) {
//                container.removeView((View) object);
//            }
//
//            @Override
//            public void notifyDataSetChanged() {
//                super.notifyDataSetChanged();
//            }
//
//            @Override
//            public int getItemPosition(Object object) {
//                return POSITION_NONE ;
//            }
//
//            @Override
//            public void setPrimaryItem(ViewGroup container, int position, Object object) {
//                detailImageView = (DetailImageView) object;
////                ((ZoomImageView)detailImageView.getImageView()).setSingleTapListener(AblumDetailActivity.this);
//                super.setPrimaryItem(container, position, object);
//            }
//
//        };
//
//        galley.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//              //  title.setText(position + 1 + "/" + picList.size());
//                if(statusRobot==-1){
//                    setUnEnableButton();
//                }else{
//                    setEnableButton();
//                }
//
//                downImage();
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//
//        galley.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//
//        galley.setAdapter(adpeter);
//        galley.setCurrentItem(position);
//        editLay.setVisibility(View.VISIBLE);
//        mPermissionStorageRequest = new PermissionStorageRequest(this);
////        ReceiveMessageBussinesss.getInstance().setDownloadOriginListener(this);
//        setRobotOnLineLister();
//        downImage();
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_delete_icon:
//                detete();
//                break;
//            case R.id.btn_download_icon:
//                //未下载完
//                showStoragePermisson();
//                break;
//            default:
//                break;
//        }
//    }
//
//    public void onPopDismiss(){
//        title.setText(getResources().getString(R.string.gallery));
//        if(detailImageView!=null){
////            detailImageView.getWaterMark().setVisibility(View.INVISIBLE);
////            detailImageView.downMove();
//            btn_back.setVisibility(View.VISIBLE);
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        if(mPop!=null){
//            mPop.dismiss();
//        }
//        super.onDestroy();
//    }
//
//
//    /**
//     * 下载前先判断权限
//     */
//    private void showStoragePermisson() {
//        if (AndPermission.hasPermission(this, Permission.STORAGE)) {
//            savePic();
//        } else {
//            if (null == mPermissionStorageRequest) {
//                mPermissionStorageRequest = new PermissionStorageRequest(this);
//            }
//            mPermissionStorageRequest.request(new PermissionStorageRequest.PermissionStorageCallback() {
//                @Override
//                public void onSuccessful() {
//                    savePic();
//                }
//
//                @Override
//                public void onFailure() {
////                    if (AndPermission.hasAlwaysDeniedPermission(AblumDetailActivity.this, Arrays.asList(Permission.STORAGE))) {
////                        showRationSettingDialog();
////                    }
//                }
//
//                @Override
//                public void onRationSetting() {
//                    showRationSettingDialog();
//                }
//            });
//        }
//    }
//
//    private void savePic() {
//        new Thread() {
//            @Override
//            public void run() {
//                String filename = String.valueOf(picList.get(galley.getCurrentItem()).getName());
//                File file = new File(Constants.PIC_DCIM_PATH + filename);
//                if (!file.exists()) {
//                    String newFileName = "origin" + System.currentTimeMillis() + ".jpg";
//                    Tools.copyFile(picList.get(galley.getCurrentItem()).getImage_original_url(), Constants.PIC_DCIM_PATH + newFileName);
//                    File newFile = new File(Constants.PIC_DCIM_PATH + newFileName);
//                    if (newFile.exists()) {
//                        Tools.saveImageToAblum(AblumDetailActivity.this, newFile);
//                    }
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            showDownLoadResult(true);
//
//                        }
//                    });
//                }
//
//            }
//        }.start();
//    }
//
//
//    private void showRationSettingDialog() {
//        final SettingService settingService = AndPermission.defineSettingDialog(AblumDetailActivity.this);
//        final MaterialDialog settingDialog = new MaterialDialog(AblumDetailActivity.this);
//        settingDialog.setTitle(R.string.permissions_cellphone_albums_title)
//                .setMessage(R.string.permissions_cellphone_albums_dialogue)
//                .setPositiveButton(R.string.common_button_settings, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        settingService.execute();
//                        settingDialog.dismiss();
//                    }
//                }).setNegativeButton(R.string.common_btn_not_allow, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                settingService.cancel();
//                settingDialog.dismiss();
//            }
//        }).show();
//    }
//
//
//    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
//    private void downImage() {
//        index = galley.getCurrentItem();
//        ImageModel imageModel = picList.get(galley.getCurrentItem());
//        Log.d(TAG, imageModel.getImage_original_url() + FileUtils.isFileExists(imageModel.getImage_original_url()));
//        ArrayList<ImRequest> requestArrayList = new ArrayList<>();
//        Parcel parcel = Parcel.obtain();
//        if (!FileUtils.isFileExists(imageModel.getImage_original_url())) {
//            setUnEnableButton();
//            XLog.d("xxxxx", "imageModel.getImage_original_url() is not exits");
//
//            ImRequest imRequest = (ImRequest) ImRequest.CREATOR.createFromParcel(parcel);
//            imRequest.setFileName(imageModel.getName());
////            imRequest.setPriority(ImRequest.HIGH_LEVEL);
//            imRequest.setLevel(0);
//            requestArrayList.add(imRequest);
//        } else {
//            if(statusRobot!=-1){
//                setEnableButton();
//            }
//        }
//
//        if (!requestArrayList.isEmpty()) {
//            XLog.d("xxxxx", "startService");
//            Intent intent = new Intent(this, DownloadService.class);
//            intent.putParcelableArrayListExtra(DownloadService.IM_POOL_PARAM_KEYWORD, requestArrayList);
//            startService(intent);
//        }
//
//    }
//
//    private void setEnableButton() {
//        btn_delete.setAlpha(1f);
//        btn_delete.setEnabled(true);
//        btn_delete.setClickable(true);
//        btn_download.setAlpha(1f);
//        btn_download.setEnabled(true);
//        btn_download.setClickable(true);
//
//        share.setAlpha(1f);
//        share.setEnabled(true);
//        share.setClickable(true);
//        XLog.d("xxxxx", "setEnableButton");
//
//
//    }
//
//    private void setUnEnableButton() {
//        btn_download.setAlpha(0.2f);
//        btn_download.setEnabled(false);
//        btn_download.setClickable(false);
//        btn_delete.setAlpha(0.2f);
//        btn_delete.setEnabled(false);
//        btn_delete.setClickable(false);
//
//        share.setAlpha(0.2f);
//        share.setEnabled(false);
//        share.setClickable(false);
//
//        XLog.d("xxxxx", "setUnEnableButton");
//
//
//
//    }
//
//    private void setRobotOnLineLister() {
//        MyRobotsLive.getInstance().getCurrentRobot().observe(AblumDetailActivity.this, new Observer<RobotInfo>() {
//            @Override
//            public void onChanged(RobotInfo robotInfo) {
//                Log.d(TAG, "robotInfo.getOnlineState()=" + robotInfo.getOnlineState());
//                if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {
//
//                }
//                if (robotInfo != null) {
//                    if (robotInfo.getOnlineState() != RobotInfo.ROBOT_STATE_ONLINE) {
//                        setUnEnableButton();
//                        statusRobot = -1 ;
//                    }else{
//                        statusRobot = 0 ;
//                        // setEnableButton();
//                    }
//                }
//            }
//
//        });
//    }
//
//
//    private void showDownLoadResult(boolean isSuccess) {
//        if (isSuccess) {
//            notifactionView = new NotifactionView(this);
//            notifactionView.setNotifactionText(getString(R.string.album_save_success), R.drawable.ic_toast_succeed);
//        } else {
//            notifactionView = new NotifactionView(this);
//            notifactionView.setNotifactionText(getString(R.string.album_save_failed), R.color.alexa_red_press, R.drawable.ic_toast_warn);
//        }
//    }
//
//    private void share() {
//        title.setText("分享");
//        btn_back.setVisibility(View.INVISIBLE);
//        if(detailImageView!=null){
////            detailImageView.getWaterMark().setVisibility(View.VISIBLE);
////            detailImageView.topMove();
//        }
////        mPop.setFocusable(false);
////        mPop.update();
////        mPop.showAtLocation(AblumDetailActivity.this.findViewById(R.id.galley), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
////        PopuUtils.fullScreenImmersive(mPop.getContentView());
////        mPop.setFocusable(true);
////        mPop.update();
//    }
//
//
//    private void detete() {
//        List<ImageModel> deleteList = new ArrayList<ImageModel>();
//        deleteList.add(new ImageModel());
//        showdeleteDialog(deleteList);
//    }
//
//    private void showdeleteDialog(List<ImageModel> list) {
//        if(deleteDialog == null){
//            deleteDialog = new MaterialDialog(this);
//            deleteDialog.setTitle(R.string.delete_pic)
//                    .setMessage(R.string.album_delete_phototips_title)
//                    .setPositiveButton(R.string.delete, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            deletePic();
//                            deleteDialog.dismiss();
//                        }
//                    })
//                    .setNegativeButton(R.string.cancel, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            deleteDialog.dismiss();
//                        }
//                    })
//                    .setPostiveTextColor(R.color.red_btn_normal);
//        }
//        deleteDialog.show();
//    }
//
//    private void deletePic() {
//        List<Files.ModifyData> modifyModels = new ArrayList<>();
//        if(picList.size()==0){
//            return;
//        }
//        ImageModel imageModel = picList.get(galley.getCurrentItem());
//
//        Files.ModifyData.Builder builder = Files.ModifyData.newBuilder();
//        builder.setAction(Consts.FILE_ACTION_DELETE);
//        builder.setPath(imageModel.getPath());
//        builder.setUpdateTime(System.currentTimeMillis());
//        modifyModels.add(builder.build());
//        PushImEngine pushEngine = new PushImEngine();
//        pushEngine.modifyModels = modifyModels;
//        pushEngine.start();
//        pushEngine.setCallback(new PushSocketCallback() {
//            @Override
//            public void onSuccess(long var1, @NonNull QueryPacket<Params.PushRequest> pushRequestQueryPacket, @NonNull ResultPacket<Params.PushResponse> pushResponseResultPacket) {
//                Log.d(TAG, "deletePic  onSuccess....");
//                if (notifactionView != null) {
//                    notifactionView.setNotifactionText(getString(R.string.delete_sucess), R.drawable.ic_toast_succeed);
//                }
//                FileUtils.deleteFile(picList.get(galley.getCurrentItem()).getImage_original_url());
//                FileUtils.deleteFile(picList.get(galley.getCurrentItem()).getThumbnail());
//                picList.remove(galley.getCurrentItem());
//                adpeter.notifyDataSetChanged();
//                if (picList.size() == 0) {
//                    doBack();
//                }
//            }
//
//            @Override
//            public void onFail(long var1, @NonNull QueryPacket<Params.PushRequest> pushRequestQueryPacket, int i, String s) {
//                if (notifactionView != null) {
//                    notifactionView.setNotifactionText(getString(R.string.delete_failed), R.color.alexa_red_press, R.drawable.ic_toast_warn);
//                }
//                Log.d(TAG, "deletePic  onFail...." + i + s);
//                LoadDialog.dismiss(AblumDetailActivity.this);
//
//            }
//
//        });
//
//
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//            if (keyCode == KeyEvent.KEYCODE_BACK) {
//                doBack();
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    private void doBack() {
//        Intent intent = new Intent();
//        intent.putExtra("picList", (Serializable) picList);
//        intent.putExtra("pisition", position);
//        intent.putExtra("targetPosition", galley.getCurrentItem());
//        setResult(Constants.REFRESH_PHOTO, intent);
//        this.finish();
//    }
//
////    @Override
//    public void onORiginSuccess(String fileName) {
//        if(picList.size()<=galley.getCurrentItem()){
//            return;
//        }
//        if(isDestroyed()){
//            return;
//        }
//        ImageModel imageModel = picList.get(galley.getCurrentItem());
//        if (fileName.equals(imageModel.getName())) {
//            XLog.d("xxxxx", "onORiginSuccess"+fileName);
//            if(detailImageView==null||this==null){
//                return ;
//            }
//            setEnableButton();
////            detailImageView.getProgressBar().setVisibility(View.GONE);
////            detailImageView.displayImageView(this, imageModel.getImage_original_url(), detailImageView.getImageView());
//            adpeter.notifyDataSetChanged();
//        }
//    }
//
////    @Override
//    public void onORiginError() {
//
//    }
//
//
//    private void shareToQQ(int flag) {
//        Tencent tencent = Tencent.createInstance(com.ubtechinc.alpha.mini.constants.Constants.QQ_OPEN_ID, AblumDetailActivity.this);
//        final Bundle params = new Bundle();
//        createWaterMaskBitmap(picList.get(galley.getCurrentItem()).getImage_original_url());
//        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,   QQShare.SHARE_TO_QQ_TYPE_IMAGE);
//        params.putString(QQShare.SHARE_TO_QQ_TITLE,   getResources().getString(R.string.share_title));
//        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, getResources().getString(R.string.share_summary));
//        String localPath = Constants.watermarkPath + picList.get(galley.getCurrentItem()).getName();
//        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, localPath);
//        if (flag == 0) {
////            tencent.shareToQQ(AblumDetailActivity.this, params, qqIUiListener);
//        } else {
//            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
//
////            tencent.shareToQzone(AblumDetailActivity.this, params, qqIUiListener);
//        }
//    }
//
//    private void shareToWechat(int flag) {
//        IWXAPI wxApi;
//        Log.d(TAG,"shareToWechat");
//        createWaterMaskBitmap(picList.get(galley.getCurrentItem()).getImage_original_url());
//        Log.d(TAG,"createWaterMaskBitmap");
//
//        wxApi = WXAPIFactory.createWXAPI(AblumDetailActivity.this, com.ubtechinc.alpha.mini.constants.Constants.WX_APP_ID);
//        WXImageObject wxImageObject = new WXImageObject();
//        wxImageObject.setImagePath(Constants.watermarkPath + picList.get(galley.getCurrentItem()).getName());
//        wxApi.registerApp(com.ubtechinc.alpha.mini.constants.Constants.WX_APP_ID);
//        WXMediaMessage msg = new WXMediaMessage();
//        msg.mediaObject = wxImageObject;
//        msg.title = getResources().getString(R.string.share_title);
//        msg.description = getResources().getString(R.string.share_photo);
//        FileInputStream fis = null;
//        try {
//            fis = new FileInputStream(picList.get(galley.getCurrentItem()).getThumbnail());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        Bitmap bitmap = BitmapFactory.decodeStream(fis);
//        if(bitmap!=null){
//            msg.setThumbImage(bitmap);
//            bitmap.recycle();
//        }
//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        req.transaction = String.valueOf(System.currentTimeMillis());
//        req.message = msg;
//        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
//        wxApi.sendReq(req);
//    }
//
//    private void shareToWeibo() {
//        createWaterMaskBitmap(picList.get(galley.getCurrentItem()).getImage_original_url());
//        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.APP_KEY);
//        mWeiboShareAPI.registerApp();   // 将应用注册到微博客户端
//
//        WeiboMessage message = new WeiboMessage();
//        ImageObject imageObject = new ImageObject();
//        FileInputStream fis = null;
//        try {
//            fis = new FileInputStream(Constants.watermarkPath + picList.get(galley.getCurrentItem()).getName());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        Bitmap bitmap = BitmapFactory.decodeStream(fis);
//        Bitmap bitmap1 =Bitmap.createScaledBitmap(bitmap,960,720,true);
//        imageObject.setImageObject(bitmap1);
//        message.mediaObject = imageObject;
//        bitmap.recycle();
//        bitmap1.recycle();
//        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
//        request.transaction = String.valueOf(System.currentTimeMillis());
//        request.message = message;
//        mWeiboShareAPI.sendRequest(request);
//
//    }
//
//
//    public void shareToQZone() {
//        createWaterMaskBitmap(picList.get(galley.getCurrentItem()).getImage_original_url());
//        final Bundle params = new Bundle();
//
//        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
//        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,Constants.watermarkPath + picList.get(galley.getCurrentItem()).getName());
//        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
//        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE);//类型
//
//       /* ArrayList<String> imageUrls = new ArrayList<String>();
//        imageUrls.add(CodeMaoConstant.watermarkPath + picList.get(galley.getCurrentItem()).getName());
//        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);*/
//        doShareToQzone(params);
//
//    }
//
//    private void doShareToQzone(final Bundle params) {
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                Tencent mTencent = Tencent.createInstance(com.ubtechinc.alpha.mini.constants.Constants.QQ_OPEN_ID, AblumDetailActivity.this);
////                mTencent.shareToQQ(AblumDetailActivity.this, params, qqIUiListener);
//            }
//        }).start();
//    }
//
//
//    @Override
//    public void dismissDialog() {
//        super.dismissDialog();
//    }
//
//    private void showShareDialog(final int tip) {
//        final MaterialDialog settingDialog = new MaterialDialog(this);
//        String message_tip = "";
//        switch (tip) {
//            case 0:
//                message_tip = "微信";
//                break;
//            case 1:
//                message_tip = "朋友圈";
//                break;
//            case 2:
//                message_tip = "QQ";
//                break;
//            case 3:
//                message_tip = "QQ空间";
//                break;
//            case 4:
//                message_tip = "微博";
//                break;
//        }
//
//        String message = String.format(getResources().getString(R.string.share_dialog), message_tip);
//
//        settingDialog.setMessage(message)
//                .setCanceledOnTouchOutside(true)
//                .setPositiveButton(R.string.open, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        settingDialog.dismiss();
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                switch (tip) {
//                                    case 0:
//                                        shareToWechat(0);
//                                        break;
//                                    case 1:
//                                        shareToWechat(1);
//                                        break;
//                                    case 2:
//                                        shareToQQ(0);
//                                        break;
//                                    case 3:
//                                        shareToQZone();
//                                        break;
//                                    case 4:
//                                        shareToWeibo();
//                                        break;
//                                }
//
//                            }
//                        }).start();
//
//                    }
//                }).setNegativeButton(R.string.simple_message_cancel, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                settingDialog.dismiss();
//            }
//        }).show();
//
//    }
//
//    private Bitmap createWaterMaskBitmap(String path) {
//        if(FileUtils.isFileExists(Constants.watermarkPath + picList.get(galley.getCurrentItem()).getName())){
//            return getLocaBitmap(path);
//        }
//        Bitmap src = getLocaBitmap(path);
//        if (src == null) {
//            return null;
//        }
//        Resources res = getResources();
//        Bitmap watermark = BitmapFactory.decodeResource(res, R.drawable.img_photo_watermark);
//        int width = src.getWidth();
//        int height = src.getHeight();
//        Log.d("watermark","width = "+width  +"    "+height);
//
//        int watermarkW  = watermark.getWidth();
//        int watermarkH  = watermark.getHeight();
//        int paddingLeft = width  - watermarkW;
//        int paddingTop  = height - watermarkH;
//        //创建一个bitmap
//        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
//        //将该图片作为画布
//        Canvas canvas = new Canvas(newBitmap);
//        //在画布 0，0坐标上开始绘制原始图片
//        canvas.drawBitmap(src, 0, 0, null);
//        //在画布上绘制水印图片
//        canvas.drawBitmap(watermark, paddingLeft, paddingTop, null);
//        Log.d(TAG,"drawBitmap");
//
//        // 保存
//        canvas.save(Canvas.ALL_SAVE_FLAG);
//        // 存储
//        canvas.restore();
//        String name = path.substring(path.lastIndexOf('/') + 1);
//        saveBitmap(newBitmap, name);
//        return newBitmap;
//    }
//
//
////    public static Bitmap zoomImg(Bitmap bm){
////        // 获得图片的宽高
////        int width = bm.getWidth();
////        int height = bm.getHeight();
////        // 计算缩放比例
////        float scaleWidth = ((float) 4160) /1080 ;
////        float scaleHeight = scaleWidth;
////        // 取得想要缩放的matrix参数
////        Matrix matrix = new Matrix();
////        matrix.postScale(scaleWidth, scaleHeight);
////        // 得到新的图片
////        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
////        return newbm;
////    }
//
//    public Bitmap getLocaBitmap(String path) {
//        FileInputStream fis = null;
//        try {
//            fis = new FileInputStream(path);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        Bitmap bitmap = BitmapFactory.decodeStream(fis);
//        return bitmap;
//    }
//
//    public void saveBitmap(Bitmap bm, String picName) {
//        Log.e(TAG, "保存图片");
//        FileUtils.createOrExistsFile(Constants.watermarkPath + picName);
//        File f = new File(Constants.watermarkPath, picName);
//        try {
//            FileOutputStream out = new FileOutputStream(f);
//            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
//            out.flush();
//            out.close();
//            Log.i(TAG, "已经保存");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public void onSingleTapConfirmed() {
//        Log.d("onSingleTapConfirmed","onSingleTapConfirmed....");
//        if(editLay.getVisibility()==View.VISIBLE){
//            editLay.setVisibility(View.GONE);
//            topLine.setVisibility(View.GONE);
//            galley.setBackgroundColor(Color.BLACK);
//            line.setVisibility(View.GONE);
//            isInSingleTap =true ;
//            hideStatusBar();
//            return;
//
//        }else{
//            editLay.setVisibility(View.VISIBLE);
//            topLine.setVisibility(View.VISIBLE);
//            galley.setBackgroundColor(Color.WHITE);
//            line.setVisibility(View.VISIBLE);
//            isInSingleTap=false ;
//            showStatusBar();
//            return;
//        }
//
//    }
//
//    @Override
//    public void onDoubleTap() {
//        if(isInSingleTap){
//            return;
//        }
//        if(editLay.getVisibility()==View.VISIBLE){
//            editLay.setVisibility(View.GONE);
//            topLine.setVisibility(View.GONE);
//            line.setVisibility(View.GONE);
//            galley.setBackgroundColor(Color.BLACK);
//            hideStatusBar();
//            return;
//
//        }else{
//            editLay.setVisibility(View.VISIBLE);
//            topLine.setVisibility(View.VISIBLE);
//            line.setVisibility(View.VISIBLE);
//            galley.setBackgroundColor(Color.WHITE);
//            showStatusBar();
//            return;
//        }
//
//    }
//
//    private void hideStatusBar(){
//        ImmersionBar.with(this)
//                .hideBar(BarHide.FLAG_HIDE_STATUS_BAR)
//                .fitsSystemWindows(false)
//                .init();
//    }
//
//    private void showStatusBar(){
//        ImmersionBar.with(this)
//                .statusBarDarkFont(true)
//                .statusBarColor(android.R.color.white)
//                .hideBar(BarHide.FLAG_SHOW_BAR)
//                .fitsSystemWindows(true)
//                .init();
//    }
//}
