package com.ubtechinc.alpha.mini.ui.share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.ubtech.utilcode.utils.StringUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.constants.CodeMaoConstant;
import com.ubtechinc.alpha.mini.constants.Constants;
import com.ubtechinc.alpha.mini.entity.UserInfo;
import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.net.GetShareUrlModule;
import com.ubtechinc.alpha.mini.repository.RobotAuthorizeReponsitory;
import com.ubtechinc.alpha.mini.repository.datasource.IRobotAuthorizeDataSource;
import com.ubtechinc.alpha.mini.ui.title.TitleActivity;
import com.ubtechinc.alpha.mini.widget.SwitchButton;
import com.ubtechinc.alpha.mini.widget.taglayout.FlowLayout;
import com.ubtechinc.alpha.mini.widget.taglayout.TagAdapter;
import com.ubtechinc.alpha.mini.widget.taglayout.TagFlowLayout;
import com.ubtechinc.nets.http.ThrowableWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ubtechinc.alpha.mini.constants.Constants.PERMISSIONCODE_AVATAR;
import static com.ubtechinc.alpha.mini.constants.Constants.PERMISSIONCODE_CALL;
import static com.ubtechinc.alpha.mini.constants.Constants.PERMISSIONCODE_PHOTO;
import static com.ubtechinc.alpha.mini.constants.Constants.SHARE_TYPE_QQ;

/**
 * @desc : 权限控制Activity
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2017/11/3
 */

public class SharePermissionActivity extends TitleActivity implements View.OnClickListener{

    private SwitchButton switchButtonVideo;
    private SwitchButton switchButtonGallery;
    private SwitchButton switchButtonCall;
    private boolean isWechat;
    private TagFlowLayout tagFlowLayout;
    private TagAdapter mTagAdapter;
    private List<String> tagList = new ArrayList<>();

    private boolean needFinish = false;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_share_permission;
    }

    @Override
    protected void onFindView() {
        super.onFindView();
        switchButtonVideo = findViewById(R.id.switchbtn_vedio);
        switchButtonGallery = findViewById(R.id.switchbtn_gallery);
        switchButtonCall = findViewById(R.id.switchbtn_call);
        tagFlowLayout = findViewById(R.id.id_flowlayout);
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        tagList.clear();
        tagList.addAll(Arrays.asList(getResources().getStringArray(R.array.funtion)));
        setTitle(getResources().getString(R.string.share_permission));
        isWechat = getIntent().getIntExtra(Constants.KEY_SHARE_TYPE, SHARE_TYPE_QQ) == SHARE_TYPE_QQ ? false : true;
        switchButtonVideo.setOnClickListener(this);
        switchButtonGallery.setOnClickListener(this);
        switchButtonCall.setOnClickListener(this);
        mTagAdapter = new TagAdapter<String>(tagList) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = new TextView(SharePermissionActivity.this);
                tv.setTextColor(ContextCompat.getColor(SharePermissionActivity.this, R.color.first_text));
                tv.setBackgroundResource(R.drawable.shape_grey_rectangle_tag_bg);
                tv.setText(s);
                return tv;
            }
        };
        tagFlowLayout.setAdapter(mTagAdapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, qqIUiListener);
    }

    private String getPermission() {
        boolean isAdd = false;
        StringBuilder stringBuilder = new StringBuilder();
        if (switchButtonGallery.isChecked()) {
            isAdd = true;
            stringBuilder.append(PERMISSIONCODE_PHOTO);
        }
        if (switchButtonVideo.isChecked()) {
            if (isAdd) {
                stringBuilder.append(",");
            }
            isAdd = true;
            stringBuilder.append(PERMISSIONCODE_AVATAR);
        }
        if (switchButtonCall.isChecked()) {
            if (isAdd) {
                stringBuilder.append(",");
            }
            stringBuilder.append(PERMISSIONCODE_CALL);
        }
        String result = stringBuilder.toString();
        if (StringUtils.isEmpty(result)) {
            return null;
        }
        return result;
    }

    public void onNext(View view) {
        share(getPermission(), isWechat);
        showLoadingDialog();
    }

    private void share(String permission, final boolean isWechat) {
        RobotAuthorizeReponsitory.getInstance().getShareUrl(MyRobotsLive.getInstance().getRobotUserId(), isWechat, permission, new IRobotAuthorizeDataSource.IGetShareUrl() {
            @Override
            public void onFail(ThrowableWrapper e) {
                dismissDialog();
                if (e.getErrorCode() == 7004) {
                    Toast.makeText(SharePermissionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SharePermissionActivity.this, getString(R.string.share_fail), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(GetShareUrlModule.Response response) {
                if (!response.isSuccess()) {
                    Toast.makeText(SharePermissionActivity.this, response.getMsg(), Toast.LENGTH_SHORT).show();
                    dismissDialog();
                    return;
                }
                String url = response.getData().getResult().getUrl();
                if (isWechat) {
                    onShareWechat(url);
                    dismissDialog();
                } else {
                    onShareQQ(url);
                    dismissDialog();
                }
            }
        });
    }

    private void onShareQQ(String url) {
        Tencent tencent = Tencent.createInstance(Constants.QQ_OPEN_ID, SharePermissionActivity.this);
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, getResources().getString(R.string.share_title,getCurrentUserName()));
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, getResources().getString(R.string.share_summary));
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
//        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,"");
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, SharePermissionActivity.this.getString(R.string.app_name));
        tencent.shareToQQ(SharePermissionActivity.this, params, qqIUiListener);
        needFinish = true;
    }

    private void onShareWechat(String url) {
        IWXAPI wxApi;
        wxApi = WXAPIFactory.createWXAPI(SharePermissionActivity.this, Constants.WX_APP_ID);
        wxApi.registerApp(Constants.WX_APP_ID);
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = getResources().getString(R.string.share_title,getCurrentUserName());
        msg.description = getResources().getString(R.string.share_summary);
        //这里替换一张自己工程里的图片资源
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_robot_icon);
        msg.setThumbImage(thumb);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        wxApi.sendReq(req);
        needFinish = true;
    }

    private String getCurrentUserName(){
        UserInfo userInfo = AuthLive.getInstance().getCurrentUser();
        String name = null;
        if(userInfo != null){
            name = userInfo.getNickName();
        }
        if(name == null){
            name = "";
        }
        return name;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(needFinish){
            setResult(RESULT_OK);
            finish();
        }
    }

    private IUiListener qqIUiListener = new IUiListener() {
        @Override
        public void onComplete(Object o) {
            Log.d("1103", " onComplete o : " + o);
        }

        @Override
        public void onError(UiError uiError) {
            Log.d("1103", " onError uiError : " + uiError);
        }

        @Override
        public void onCancel() {
            Log.d("1103", " onCancel o  ");
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.switchbtn_call:
            case R.id.switchbtn_gallery:
            case R.id.switchbtn_vedio:
                updataList();
                break;
        }
    }

    private void updataList() {
        tagList.clear();
        tagList.addAll(Arrays.asList(getResources().getStringArray(R.array.funtion)));
        if(switchButtonVideo.isChecked()){
            tagList.add(getString(R.string.video_surveillance));
        }
        if(switchButtonGallery.isChecked()){
            tagList.add(getString(R.string.gallery));
        }
        if(switchButtonCall.isChecked()){
            tagList.add(getString(R.string.contact));
        }
        mTagAdapter.notifyDataChanged();
    }
}
