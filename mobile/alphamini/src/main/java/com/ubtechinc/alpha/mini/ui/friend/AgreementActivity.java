package com.ubtechinc.alpha.mini.ui.friend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.utils.NetUtil;

/**
 * @作者：liudongyang
 * @日期: 18/7/17 17:28
 * @描述:
 */
public class AgreementActivity extends BaseToolbarActivity {

    private WebView mWvAgreement;

    private WebSettings mWebSettings;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        mWvAgreement = findViewById(R.id.wv_agreement);
        mWebSettings = mWvAgreement.getSettings();
        init();
    }

    private void init() {
        initToolbar(findViewById(R.id.toolbar), "隐私协议", View.GONE, false);
        int state = NetUtil.getNetworkState(this);
        if (state == NetUtil.NETWORN_NONE) {
            mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ONLY);
            // mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        } else {
            mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        }

        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setAppCacheEnabled(true);

        mWvAgreement.loadUrl(com.ubtechinc.nets.BuildConfig.HOST + "/index/privacypolicy.html");
    }


}
