package com.ubtechinc.alpha.mini.ui.strategy;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.ui.FeedBackActivity;
import com.ubtechinc.alpha.mini.ui.strategy.circledialog.CircleDialog;
import com.ubtechinc.alpha.mini.ui.strategy.circledialog.callback.ConfigText;
import com.ubtechinc.alpha.mini.ui.strategy.circledialog.param.TextParams;
import com.ubtechinc.alpha.mini.utils.NetUtil;


/**
 * Created by junsheng.chen on 2018/6/7.
 */
public class StrategyFragment extends AbstractStrategyFragment implements IJsBridge {

    public static final String TAG = "StrategyFragment";

    private WebView mWebView;
    private Context mContext;
    private StrategyClient mClient;
    private WebSettings mWebSettings;
    private Handler mHandler;
    private View mErrorView;
    private RelativeLayout mParent;
    private LayoutInflater mInflater;

    public interface Callback {
        void onTitleChange(WebView webView, String title);

        //void notifyActionBar(WebView webView);

        void notifyHomePage(boolean homepage);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInflater = inflater;
        mWebView = new WebView(mContext);
        mParent = new RelativeLayout(mContext);
        mParent.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mWebView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mClient = new StrategyClient();
        mWebSettings = mWebView.getSettings();

        mParent.addView(mWebView);
        return mParent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mWebSettings.setJavaScriptEnabled(true);

        int state = NetUtil.getNetworkState(mContext);
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

        String dbPath =getActivity().getApplicationContext().getDir("databases", Context.MODE_PRIVATE).getPath();
        String appCaceDir =getActivity().getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        mWebSettings.setAppCachePath(appCaceDir);
        mWebSettings.setDatabasePath(dbPath);
        mWebSettings.setAppCacheMaxSize(8 * 1024 * 1024);
        mWebView.setWebViewClient(mClient);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(final WebView view, final String title) {
                super.onReceivedTitle(view, title);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Callback callback = (Callback) getActivity();
                        if (callback != null) {
                            callback.onTitleChange(view, title);
                        }
                    }
                });
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("cjslog", consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }

        });

        mWebView.addJavascriptInterface(new StrategyJsBrige(this), "bridge");

        //mWebView.loadUrl("file:///android_asset/strategy.html");

        mWebView.loadUrl(com.ubtechinc.nets.BuildConfig.HOST+"index/strategy.html");
        //mWebView.loadUrl("https://www.baidu.com");
    }

    public void onLoadHelpPage() {
        //mWebView.loadUrl("file:///android_asset/help.html");
        mWebView.loadUrl(com.ubtechinc.nets.BuildConfig.HOST+"index/help.html");
    }

    private long mCurrentTime;

    @Override
    public void contactService() {
        if (System.currentTimeMillis() - mCurrentTime <= 3000) {
            new CircleDialog.Builder()
                    .setText(getString(R.string.call_operation_fail))
                    .setWidth(0.7f)
                    .setMaxHeight(0.3f)
                    .configText(new ConfigText() {
                        @Override
                        public void onConfig(TextParams params) {
                            params.gravity = Gravity.CENTER;
                            params.textColor = Color.BLACK;
                        }
                    })
                    .setPositive(getString(R.string.i_know), null)
                    .show(getActivity().getSupportFragmentManager());
        } else {

            new CircleDialog.Builder()
                    //.setTitle("标题")
                    .setText(getString(R.string.services_num))
                    .setWidth(0.7f)
                    .setMaxHeight(0.3f)
                    .configText(new ConfigText() {
                        @Override
                        public void onConfig(TextParams params) {
                            params.gravity = Gravity.CENTER;
                            params.textColor = Color.BLACK;
                        }
                    })
                    .setPositive(getString(R.string.call_services), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getString(R.string.services_num).replace(" ", "")));
                            startActivity(intent);
                        }
                    })
                    .setNegative(getString(R.string.simple_message_cancel), null)
                    .setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            //Toast.makeText(MainActivity.this, "显示了！", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            //Toast.makeText(MainActivity.this, "取消了！", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show(getActivity().getSupportFragmentManager());
            mCurrentTime = System.currentTimeMillis();
        }
    }

    @Override
    public void makeSuggestion() {
        startActivity(new Intent(getActivity(), FeedBackActivity.class));
    }

    /*@Override
    public void onHomepageLoad(boolean homepage) {
        final Callback callback = (Callback) getActivity();
        if (homepage) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.notifyActionBar(mWebView);
                }
            });

        }
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }
        }
        return false;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public boolean onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        } else {
            return false;
        }
    }

    private class StrategyClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());

            return true;
        }

        @Override
        public void onPageFinished(final WebView view, String url) {

            //view.loadUrl("javascript:window.bridge.showSource('<head>'+" + "document.getElementsByTagName('head')[0].innerHTML+'</head>');");
            //Document document = Jsoup.parse(view.getWebViewClient().toString());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Callback callback = (Callback) getActivity();
                    if (callback != null) {
                        callback.notifyHomePage(view.canGoBack());
                    }
                }
            });
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            switch (error.getErrorCode()) {
                case WebViewClient.ERROR_HOST_LOOKUP:
                case WebViewClient.ERROR_CONNECT:
                case WebViewClient.ERROR_UNKNOWN:
                    mParent.removeAllViews();
                    mErrorView = mInflater.inflate(R.layout.layout_change_wifi_mobile_no_connection, null, false);
                    mErrorView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                    mParent.addView(mErrorView);
                    break;
                case WebViewClient.ERROR_TIMEOUT:
                    mParent.removeAllViews();
                    mErrorView = mInflater.inflate(R.layout.layout_change_wifi_connect_failed, null, false);
                    mErrorView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                    mParent.addView(mErrorView);
                    break;
            }

        }
    }

    @Override
    public void onDestroy() {
        if( mWebView!=null) {
            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            // destory()
            ViewParent parent = mWebView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mWebView);
            }
            mWebView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            mWebView.getSettings().setJavaScriptEnabled(false);
            mWebView.clearHistory();
            mWebView.clearView();
            mWebView.removeAllViews();
            mWebView.destroy();

        }

        super.onDestroy();
    }
}
