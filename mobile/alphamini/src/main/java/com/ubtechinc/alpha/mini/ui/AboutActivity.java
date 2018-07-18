package com.ubtechinc.alpha.mini.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.SPUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.alpha.mini.BuildConfig;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseActivity;
import com.ubtechinc.alpha.mini.viewmodel.AppUpdateViewModel;

/**
 * Created by hongjie.xiang on 2017/11/29.
 */

public class AboutActivity extends BaseActivity {

    private ImageView right ;
    private TextView version ;
    private ImageView back ;
    private TextView title ;
    private ImageView action  ;
    private RelativeLayout relativeLayout;
    private ImageView aboutIcon;
    private RelativeLayout rlUpdating;
    private View v_redPoint;
    private ImageView iv_Arror;
    private TextView  tv_updateHint;

    private int iconClickCount  = 0;

    private boolean openVideoed = false;

    private boolean isNeedUpdate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_about);
        initView();
        checkUpdate();
    }

    private void initView() {
        openVideoed = SPUtils.get().getBoolean("open_video");

        right = findViewById(R.id.right) ;
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AboutActivity.this,ServiceAgreementActivity.class);
                startActivity(intent);
            }
        });

        back = findViewById(R.id.toolbar_back);
        title =findViewById(R.id.toolbar_title);
        title.setText(R.string.about);
        action= findViewById(R.id.toolbar_action);
        action.setVisibility(View.GONE);
        version=findViewById(R.id.app_version);
        version.setText(BuildConfig.VERSION_NAME);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        aboutIcon = findViewById(R.id.about_icon);
        relativeLayout=findViewById(R.id.service);
        rlUpdating = findViewById(R.id.rl_update);
        tv_updateHint = findViewById(R.id.tv_update_hint);
        v_redPoint = findViewById(R.id.v_red_point);
        iv_Arror = findViewById(R.id.iv_arror);
        rlUpdating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNeedUpdate){
                    PageRouter.toMiniAppUpdateActivity(AboutActivity.this);
                }
            }
        });
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AboutActivity.this,ServiceAgreementActivity.class);
                startActivity(intent);
            }
        });
        aboutIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!openVideoed) {
                    iconClickCount++;
                    if(iconClickCount == 5){
                        SPUtils.get().put("open_video",true);
                        Toast.makeText(AboutActivity.this,"视频监控功能已打开",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void checkUpdate() {
        isNeedUpdate = AppUpdateViewModel.getViewModel().isNeedUpdateMiniApp();
        if (isNeedUpdate){
            v_redPoint.setVisibility(View.VISIBLE);
            iv_Arror.setVisibility(View.VISIBLE);
            tv_updateHint.setVisibility(View.GONE);
        }else{
            v_redPoint.setVisibility(View.GONE);
            iv_Arror.setVisibility(View.GONE);
            tv_updateHint.setVisibility(View.VISIBLE);
        }
    }
}
