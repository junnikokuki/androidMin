package com.ubtechinc.alpha.mini.ui.share;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ubtech.utilcode.utils.StringUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.entity.RobotPermission;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.net.UpdatePermissionModule;
import com.ubtechinc.alpha.mini.repository.RobotAuthorizeReponsitory;
import com.ubtechinc.alpha.mini.widget.SwitchButton;
import com.ubtechinc.alpha.mini.widget.taglayout.FlowLayout;
import com.ubtechinc.alpha.mini.widget.taglayout.TagAdapter;
import com.ubtechinc.alpha.mini.widget.taglayout.TagFlowLayout;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.ThrowableWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ubtechinc.alpha.mini.constants.Constants.PERMISSIONCODE_AVATAR;
import static com.ubtechinc.alpha.mini.constants.Constants.PERMISSIONCODE_CALL;
import static com.ubtechinc.alpha.mini.constants.Constants.PERMISSIONCODE_PHOTO;
import static com.ubtechinc.alpha.mini.constants.Constants.PERMISSION_CODE_CODING;

/**
 * @desc :
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2017/11/3
 */

public class SharePermissionUpdateView extends FrameLayout{

    private static final int SWITCH_OPEN = 1;
    private List<RobotPermission> resultBeanList;
    private TextView tvShareTime;
    private SwitchButton switchButtonVideo;
    private SwitchButton switchButtonGallery;
    private SwitchButton switchButtonCall;
    private SwitchButton switchButtonCode;
    private String userId;
    private TagFlowLayout tagFlowLayout;
    private List<String> tagList = new ArrayList<>();
    private TagAdapter tagAdapter;

    public SharePermissionUpdateView(Context context) {
        super(context);
        initView();
    }

    public SharePermissionUpdateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SharePermissionUpdateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void updateTagList() {
        tagList.clear();
        tagList.addAll(Arrays.asList(getResources().getStringArray(R.array.funtion)));
        if(switchButtonVideo.isChecked()){
            tagList.add(getContext().getString(R.string.video_surveillance));
        }
        if(switchButtonGallery.isChecked()){
            tagList.add(getContext().getString(R.string.gallery));
        }
        if(switchButtonCall.isChecked()){
            tagList.add(getContext().getString(R.string.contact));
        }
        if (switchButtonCode.isChecked()) {
            tagList.add(getContext().getString(R.string.coding));
        }
        tagAdapter.notifyDataChanged();
    }

    private void initView() {
        tagList.clear();
        tagList.addAll(Arrays.asList(getResources().getStringArray(R.array.funtion)));
        LayoutInflater.from(getContext()).inflate(R.layout.layout_share_permission_update, this, true);
        tvShareTime = findViewById(R.id.tv_share_time);
        switchButtonVideo = findViewById(R.id.switchbtn_vedio);
        switchButtonGallery = findViewById(R.id.switchbtn_gallery);
        switchButtonCall = findViewById(R.id.switchbtn_call);
        switchButtonCode = findViewById(R.id.switchbtn_code);
        tagFlowLayout = findViewById(R.id.id_flowlayout);
        tagAdapter = new TagAdapter<String>(tagList) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = new TextView(getContext());
                tv.setTextColor(ContextCompat.getColor(getContext(), R.color.first_text));
                tv.setBackgroundResource(R.drawable.shape_grey_rectangle_tag_bg);
                tv.setText(s);
                return tv;
            }
        };
        tagFlowLayout.setAdapter(tagAdapter);
    }

    public void setTimeText(String text) {
        tvShareTime.setText(text);
    }

    public void setResultBeanList(List<RobotPermission> resultBeanList) {
        this.resultBeanList = resultBeanList;
        switchButtonVideo.setCheckedImmediately(isHasPermisson(PERMISSIONCODE_AVATAR));
        switchButtonGallery.setCheckedImmediately(isHasPermisson(PERMISSIONCODE_PHOTO));
        switchButtonCall.setCheckedImmediately(isHasPermisson(PERMISSIONCODE_CALL));
        switchButtonCode.setCheckedImmediately(isHasPermisson(PERMISSION_CODE_CODING));
        //checkVideoItemAndGalleryItem();
        updateTagList();
        switchButtonGallery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updatePermission(PERMISSIONCODE_PHOTO);
                updateTagList();
            }
        });
        switchButtonVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updatePermission(PERMISSIONCODE_AVATAR);
                updateTagList();
            }
        });
        switchButtonCall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updatePermission(PERMISSIONCODE_CALL);
                updateTagList();
            }
        });
        switchButtonCode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updatePermission(PERMISSION_CODE_CODING);
                updateTagList();
            }
        });
    }

    private void updatePermission(String permissionCode) {
        RobotAuthorizeReponsitory.getInstance().updatePermission(userId, MyRobotsLive.getInstance().getRobotUserId(), permissionCode, new ResponseListener<UpdatePermissionModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                Toast.makeText(getContext(), getResources().getString(R.string.update_fail), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(UpdatePermissionModule.Response response) {
                Toast.makeText(getContext(), getResources().getString(R.string.update_succes), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkVideoItemAndGalleryItem(){
        if (switchButtonVideo.isChecked()){
            tagList.add(getContext().getString(R.string.video_surveillance));
        }
        if (switchButtonGallery.isChecked()){
            tagList.add(getContext().getString(R.string.gallery));
        }
        tagAdapter.notifyDataChanged();
    }

    private String getPermission() {
        boolean isAdd = false;
        StringBuilder stringBuilder = new StringBuilder();
        if(switchButtonGallery.isChecked()) {
            isAdd = true;
            stringBuilder.append(PERMISSIONCODE_PHOTO);
        }
        if(switchButtonVideo.isChecked()) {
            if(isAdd) {
                stringBuilder.append(",");
            }
            stringBuilder.append(PERMISSIONCODE_AVATAR);
        }
        if(StringUtils.isEmpty(stringBuilder.toString())) {
            return "null";
        }
        return stringBuilder.toString();
    }

    private boolean isHasPermisson(String permissionCode) {
        for(RobotPermission resultBean : resultBeanList) {
            if(permissionCode.equals(resultBean.getPermissionCode())) {
                return resultBean.getStatus() == SWITCH_OPEN;
            }
        }
        return false;
    }
}
