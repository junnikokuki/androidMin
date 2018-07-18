package com.ubtechinc.alpha.mini.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseActivity;
import com.ubtechinc.alpha.mini.ui.strategy.AbstractStrategyFragment;
import com.ubtechinc.alpha.mini.ui.strategy.StrategyFragment;

import java.lang.ref.WeakReference;

/**
 * Created by junsheng.chen on 2018/6/7.
 */
public class StrategyActivity extends BaseActivity implements View.OnClickListener, StrategyFragment.Callback{

    private ImageView mHelpView;
    private TextView mTitle;
    private ImageView mBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_strategy);

        mHelpView = findViewById(R.id.help);
        mTitle = findViewById(android.R.id.title);
        mBack = findViewById(R.id.back);

        mHelpView.setOnClickListener(this);
        mBack.setOnClickListener(this);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        AbstractStrategyFragment f = (AbstractStrategyFragment) Fragment.instantiate(this, StrategyFragment.class.getName());

        ft.replace(R.id.content, f, f.getFragmentTag());
        ft.commitAllowingStateLoss();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AbstractStrategyFragment f = (AbstractStrategyFragment) getSupportFragmentManager().findFragmentByTag(StrategyFragment.TAG);
        if (f.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.help:
                StrategyFragment f = (StrategyFragment) getSupportFragmentManager().findFragmentByTag(StrategyFragment.TAG);
                f.onLoadHelpPage();
                break;
            case R.id.back:
                f = (StrategyFragment) getSupportFragmentManager().findFragmentByTag(StrategyFragment.TAG);
                if (!f.onBackPressed()) {
                    super.onBackPressed();
                }
                break;
        }
    }

    @Override
    public void onTitleChange(WebView webView, String title) {
        mTitle.setText(title);
    }

    /*@Override
    public void notifyActionBar(WebView webView) {
        onTitleChange(webView, webView.getTitle());
    }*/

    @Override
    public void notifyHomePage(boolean homepage) {
        mHelpView.setVisibility(homepage ? View.INVISIBLE: View.VISIBLE);
    }
}
