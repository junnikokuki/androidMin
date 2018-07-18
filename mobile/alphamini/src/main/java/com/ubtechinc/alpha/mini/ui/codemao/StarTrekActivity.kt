package com.ubtechinc.alpha.mini.ui.codemao

import android.arch.lifecycle.LifecycleActivity
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import com.ubtech.utilcode.utils.LogUtils
import com.ubtech.utilcode.utils.NetworkUtils
import com.ubtechinc.alpha.mini.R
import com.ubtechinc.alpha.mini.common.StarTrekJsInterface
import com.ubtechinc.alpha.mini.constants.CodeMaoConstant
import com.ubtechinc.alpha.mini.ui.PageRouter
import com.ubtechinc.alpha.mini.ui.codemao.listener.IStartTrekUIControl
import java.lang.reflect.InvocationTargetException

class StarTrekActivity: LifecycleActivity(), IStartTrekUIControl {


    private var mWebView: WebView? = null
    private var loadError = false
    private var isLoadFinish = false
    private var mUrl: String? = null
    private lateinit var mLoadingView: View
    lateinit var mNetworkErrorView: View
    lateinit var mNoConnectionView: View
    lateinit var mTitleBar: View
    val STATUS_NO_CONNECTION = -1
    val STATUS_NETWORK_ERROR = -2
    val STATUS_LOAD_SUCCESS = 1
    var mLoadProgress = 0
    lateinit var startTrekPresenter : StartTrekPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_star_trek)
        initView()
    }

    private fun initView() {
        mWebView = findViewById(R.id.webview_coding)
        mLoadingView = findViewById(R.id.loading)
        mNetworkErrorView = findViewById(R.id.network_error)
        mNoConnectionView = findViewById(R.id.no_connection)
        mTitleBar = findViewById(R.id.toolbar)
        var title = findViewById<TextView>(R.id.toolbar_title)
        title.text = ""
        findViewById<ImageView>(R.id.toolbar_back).setOnClickListener { finish() }
        findViewById<ImageView>(R.id.toolbar_action).visibility = View.INVISIBLE
        try {
            mUrl = intent.getStringExtra("url")
            LogUtils.d("url = $mUrl")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (TextUtils.isEmpty(mUrl)) {
            mUrl = "https://dev_ubt_mini.codemao.cn/#/home"
//            mUrl = "file:///android_asset/index.html"
//            mUrl = "http://192.168.20.227:8000/"
        }

        if (!NetworkUtils.isConnected()) {
            showView(STATUS_NO_CONNECTION)
            return
        }
        startTrekPresenter = StartTrekPresenter(this)
        initWebView()




    }

    private fun initWebView() {
        val webSetting = mWebView?.settings
        webSetting?.allowFileAccess = true
        webSetting?.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        webSetting?.setSupportZoom(true)
        webSetting?.builtInZoomControls = true
        webSetting?.useWideViewPort = true

        webSetting?.setSupportMultipleWindows(false)
        // webSetting.setLoadWithOverviewMode(true);
        webSetting?.setAppCacheEnabled(true)
        // webSetting.setDatabaseEnabled(true);
        webSetting?.domStorageEnabled = true
        webSetting?.javaScriptEnabled = true
        webSetting?.setGeolocationEnabled(true)
        webSetting?.setAppCacheMaxSize(java.lang.Long.MAX_VALUE)
        webSetting?.setAppCachePath(this.getDir("appcache", 0).path)
        webSetting?.databasePath = this.getDir("databases", 0).path
        webSetting?.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .path)
        webSetting?.pluginState = WebSettings.PluginState.ON_DEMAND
        var bundle = Bundle()
        bundle.putBoolean("standardFullScreen", false);
//true表示标准全屏，false表示X5全屏；不设置默认false，
        bundle.putBoolean("supportLiteWnd", false)
//false：关闭小窗；true：开启小窗；不设置默认true，
        bundle.putInt("DefaultVideoScreen", 2)
//1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
        if( mWebView?.x5WebViewExtension != null) {
            mWebView?.x5WebViewExtension?.invokeMiscMethod("setVideoParams", bundle)
        }



        val webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                /**
                 * 防止加载网页时调起系统浏览器
                 */
                view!!.loadUrl(url)
                return true
            }


            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                isLoadFinish = true
                loadError = true
                showView(STATUS_NETWORK_ERROR)
                LogUtils.d("onReceivedError")
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
                super.onPageStarted(view, url, favicon)
                LogUtils.d("onPageStarted")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                isLoadFinish = true

                if (mLoadProgress == 100 && !loadError && isLoadFinish) {
                    showView(STATUS_LOAD_SUCCESS)
                }
                LogUtils.d("onPageFinished")

            }

        }
        mWebView?.webViewClient = webViewClient
        mWebView?.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(p0: WebView?, p1: Int) {
                super.onProgressChanged(p0, p1)
                mLoadProgress = p1
                isLoadFinish = true
                if (mLoadProgress == 100 && !loadError && isLoadFinish) {
                    showView(STATUS_LOAD_SUCCESS)
                }
                LogUtils.d("onProgressChanged  = $p1")
            }
        }
        mWebView?.addJavascriptInterface(StarTrekJsInterface(startTrekPresenter.iStarTrekControl), CodeMaoConstant.JS_INTERFACE_NAME)
        //        mWebView.loadUrl(URL);
        try {
            if (Build.VERSION.SDK_INT >= 16) {
                val clazz = mWebView?.settings?.javaClass
                val method = clazz?.getMethod(
                        "setAllowUniversalAccessFromFileURLs", Boolean::class.javaPrimitiveType)
                method?.invoke(mWebView?.settings, true)
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }

        mWebView?.loadUrl(this@StarTrekActivity.mUrl)
//        WebView.setWebContentsDebuggingEnabled(true)
        LogUtils.d("extension = " + mWebView?.x5WebViewExtension)
    }

    override fun requestJS(js: String) {
        mWebView?.loadUrl(js)
    }

     fun showView(status: Int) {
        when (status) {
            STATUS_LOAD_SUCCESS -> {
                mLoadingView.visibility = View.GONE
                mTitleBar.visibility = View.GONE
            }
            STATUS_NETWORK_ERROR -> {
                mNetworkErrorView.visibility = View.VISIBLE
                mLoadingView.visibility = View.GONE
                mWebView?.visibility = View.GONE
            }
            STATUS_NO_CONNECTION -> {
                mNoConnectionView.visibility = View.VISIBLE
                mLoadingView.visibility = View.GONE
                mWebView?.visibility = View.GONE
            }
        }
    }
    override fun startPlayVideo(url: String) {
        var bundle = Bundle()
        bundle.putString("url", url)
        PageRouter.toVideoActivity(applicationContext, bundle)
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyWebView()


    }


    fun destroyWebView() {

        if (mWebView != null) {
            mWebView?.removeAllViews()
            mWebView?.clearHistory()
            mWebView?.clearCache(true)
            mWebView?.freeMemory()
            mWebView?.destroy()
        }

    }
}
