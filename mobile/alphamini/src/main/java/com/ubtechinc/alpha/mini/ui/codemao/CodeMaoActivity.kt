package com.ubtechinc.alpha.mini.ui.codemao

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
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
import com.ubtechinc.alpha.mini.common.BaseActivity
import com.ubtechinc.alpha.mini.common.BlocklyJsInterface
import com.ubtechinc.alpha.mini.constants.CodeMaoConstant
import com.ubtechinc.alpha.mini.ui.PageRouter
import com.ubtechinc.alpha.mini.ui.codemao.PublishFragment.TAKE_PICTURE_CODE
import com.ubtechinc.alpha.mini.ui.codemao.listener.ICodeMaoUIControl
import com.ubtechinc.alpha.mini.widget.CodingMaterialDialog
import com.ubtechinc.alpha.mini.widget.LoadingDialog
import com.ubtechinc.alpha.mini.widget.MaterialDialog
import com.ubtechinc.codemaosdk.HandlerUtils
import com.ubtechinc.codemaosdk.MiniApi

import java.lang.reflect.InvocationTargetException

class CodeMaoActivity : BaseActivity(), ICodeMaoUIControl {


    private lateinit var mWebView: WebView
    private var loadError = false
    private var isLoadFinish = false
    private var mUrl: String? = null
    private lateinit var mCodeMaoPresenter: CodeMaoPresenter
    private lateinit var mLoadingView: View
    private lateinit var mNetworkErrorView: View
    private lateinit var mNoConnectionView: View
    lateinit var mRootView : View
    lateinit var mTitleBar: View
    val STATUS_NO_CONNECTION = -1
    val STATUS_NETWORK_ERROR = -2
    val STATUS_LOAD_SUCCESS = 1
    private lateinit var mCodingDialog: CodingMaterialDialog
    var mLoadProgress = 0
    private var mIsCourse = false
    private var mIsInit = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_codemao)
        initView()

    }

    override fun setStatuesBar() {

    }

    private fun initView() {
        mWebView = findViewById(R.id.webview_coding)
        mLoadingView = findViewById(R.id.loading)
        mNetworkErrorView = findViewById(R.id.network_error)
        mNoConnectionView = findViewById(R.id.no_connection)
        mTitleBar = findViewById(R.id.toolbar)
        mRootView = findViewById(R.id.code_mao_root)
        var title = findViewById<TextView>(R.id.toolbar_title)
        title.text = ""
        findViewById<ImageView>(R.id.toolbar_back).setOnClickListener { finish() }
        findViewById<ImageView>(R.id.toolbar_action).visibility = View.INVISIBLE
        mUrl = intent.extras?.getString(PageRouter.INTENT_KEY_WEB_URL)
        LogUtils.d("url = $mUrl")

        if (TextUtils.isEmpty(mUrl)) {
//            mUrl = "https://dev_ubt_mini.codemao.cn/#/"
            mUrl = "file:///android_asset/dist/index.html"
//            mUrl = "http://192.168.20.227:8000/"
        }

        mIsCourse = intent.extras?.getString(PageRouter.INTENT_KEY_PAGE_TYPE)?.isNotEmpty() ?: false
        mCodeMaoPresenter = CodeMaoPresenter(this)

        if (!NetworkUtils.isConnected()) {
            showView(STATUS_NO_CONNECTION)
            return
        }
        initWebView()

        mCodingDialog = CodingMaterialDialog(this)
        mCodeMaoPresenter.onCreate()
    }


    private fun initWebView() {
        val webSetting = mWebView?.settings
        webSetting.allowFileAccess = true
        webSetting.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        webSetting.setSupportZoom(true)
        webSetting.builtInZoomControls = true
        webSetting.useWideViewPort = true

        webSetting.setSupportMultipleWindows(false)
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true)
        // webSetting.setDatabaseEnabled(true);
        webSetting.domStorageEnabled = true
        webSetting.javaScriptEnabled = true
        webSetting.setGeolocationEnabled(true)
        webSetting.setAppCacheMaxSize(java.lang.Long.MAX_VALUE)
        webSetting.setAppCachePath(this.getDir("appcache", 0).path)
        webSetting.databasePath = this.getDir("databases", 0).path
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .path)
        webSetting.pluginState = WebSettings.PluginState.ON_DEMAND
        var bundle = Bundle()
        bundle.putBoolean("standardFullScreen", false);
//true表示标准全屏，false表示X5全屏；不设置默认false，
        bundle.putBoolean("supportLiteWnd", false)
//false：关闭小窗；true：开启小窗；不设置默认true，
        bundle.putInt("DefaultVideoScreen", 2)
//1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
        mWebView?.x5WebViewExtension?.invokeMiscMethod("setVideoParams", bundle)
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

            override fun onPageStarted(view: WebView?, url: String, favicon: Bitmap) {
                super.onPageStarted(view, url, favicon)
                LogUtils.d("onPageStarted")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                isLoadFinish = true
                refreshWebView()
                if (mLoadProgress == 100 && !loadError && isLoadFinish) {

                    HandlerUtils.runUITask({
                        showView(STATUS_LOAD_SUCCESS)
                    }, 2000)



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
                if (mLoadProgress == 100) {
                    refreshWebView()
                }
                if (mLoadProgress == 100 && !loadError && isLoadFinish) {

                    HandlerUtils.runUITask({
                        showView(STATUS_LOAD_SUCCESS)
                    }, 2000)

                }
                LogUtils.d("onProgressChanged  = $p1")
            }
        }
        mWebView?.addJavascriptInterface(BlocklyJsInterface(mCodeMaoPresenter.iBlocklyControl), CodeMaoConstant.JS_INTERFACE_NAME)
        //        mWebView.loadUrl(URL);
        try {
            if (Build.VERSION.SDK_INT >= 16) {
                val clazz = mWebView?.settings.javaClass
                val method = clazz.getMethod(
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

        mWebView?.loadUrl(this@CodeMaoActivity.mUrl)
        WebView.setWebContentsDebuggingEnabled(true)
        LogUtils.d("extension = " + mWebView?.x5WebViewExtension)
    }

    private fun refreshWebView() {
        LogUtils.i("refreshWebView")
        if (mIsInit) return

        if (mIsCourse) {
            mCodeMaoPresenter.startRender(CodeMaoConstant.PARAM_HOME)
        } else {
            mCodeMaoPresenter.startRender("")
        }
        mIsInit = true
    }

    override fun showView(status: Int) {
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

    override fun closeBlocklyWindow() {
        finish()
    }

    override fun requestJS(js: String) {
        try {
            mWebView?.loadUrl(js)
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView != null && isLoadFinish && !loadError) {
                LogUtils.d("上报返回按键事件给前端")
                mCodeMaoPresenter.onPhoneKeyBack()
                return true
            } else {
                finish()
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    override fun onResume() {
        super.onResume()
        mCodeMaoPresenter.onResume()

    }

    override fun onPause() {
        mCodeMaoPresenter.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyWebView()
        mCodeMaoPresenter.onDestory()


    }


    private fun destroyWebView() {
        if (mWebView != null) {
            mWebView!!.stopLoading()
            mWebView!!.removeAllViews()
            mWebView!!.clearHistory()
            mWebView!!.clearCache(true)
            mWebView!!.freeMemory()
            mWebView!!.removeJavascriptInterface(CodeMaoConstant.JS_INTERFACE_NAME)
            mWebView!!.destroy()
        }

    }

    override fun showBluetoothDisconnetedWarning() {
        var dialog = MaterialDialog(this)
        dialog.setMessage(R.string.codemao_bluetooth_disconnect).setPositiveButton(R.string.codemao_bluetooth_sure) { dialog.dismiss() }.show()
    }

    override fun showBluetoothCloseWarning() {
        var dialog = MaterialDialog(this)
        dialog.setMessage(R.string.codemao_bluetooth_close).setPositiveButton(R.string.codemao_bluetooth_sure) { dialog.dismiss() }.show()
    }


    override fun showDisconnectRobotHint() {
        var dialog = MaterialDialog(this)
        dialog.setTitle(R.string.codemao_make_sure_disconnect_title)
                .setMessage(R.string.codemao_make_sure_disconnect_msg)
                .setNegativeButton(R.string.codemao_bluetooth_cancel) {
                    dialog.dismiss()
                }
                .setPositiveButton(R.string.codemao_make_sure_disconnect_btn1) {
                    mCodeMaoPresenter.disconnectRobot()
                    dialog.dismiss()
                }.show()
    }

    override fun startOpenBle() {
        PageRouter.toOpenBluetoothForCoding(this)
    }

    override fun startBindRobot() {

        PageRouter.toRemindBindRobotActivity(this)
    }

    override fun showLoadingDialog() {
        LoadingDialog.getInstance(this).show()
    }

    override fun dismissLoadingDialog() {
        LoadingDialog.getInstance(this).dismiss()
    }


    override fun showConnectRobotSuccess(robotSn: String?) {
        if (TextUtils.isEmpty(robotSn)) {
            mCodingDialog.setMessage(R.string.coding_connect_success).setCanceledOnTouchOutside(true).show()

        } else {
            mCodingDialog.setMessage(getString(R.string.coding_connect_success) + "\n" + "当前机器人已切换至：" + robotSn).setCanceledOnTouchOutside(true).show()
        }

        HandlerUtils.runUITask({
            if (mCodingDialog.isShowing) {
                mCodingDialog.dismiss()
            }
        }, 2000)
    }

    override fun showConnectRobotFail() {
        var dialog = MaterialDialog(this)
        dialog.setMessage(R.string.coding_connect_fail).setPositiveButton(R.string.codemao_bluetooth_sure) { dialog.dismiss() }.show()
    }

    override fun skipToSearchBle(data: Bundle) {

        PageRouter.toSearchBleActivity(this, data)
    }

    override fun onNewIntent(intent: Intent?) {
        mCodeMaoPresenter.handleNewIntent(intent)

        super.onNewIntent(intent)
    }

    override fun startPlayVideo(url: String) {
        var bundle = Bundle()
        bundle.putString("url", url)
        PageRouter.toVideoActivity(applicationContext, bundle)
    }

    override fun showRobotOffline() {
        var dialog = MaterialDialog(this)
        dialog.setTitle(R.string.coding_robot_offline_title)
                .setMessage(R.string.coding_robot_offline_tips)
                .setNegativeButton(R.string.codemao_bluetooth_cancel) {
                    dialog.dismiss()
                }
                .setPositiveButton(R.string.switch_wifi) {
                    MiniApi.get().disconnectRobot()
                    PageRouter.toChooseWifiActivity(this@CodeMaoActivity)
                    dialog.dismiss()
                }.show()

    }


    override fun showRobotDisconnectSuccess() {
        var dialog = MaterialDialog(this)
        dialog.setMessage(R.string.coding_disconnect_success).setPositiveButton(R.string.codemao_bluetooth_sure) { dialog.dismiss() }.show()
    }

    override fun showSkillStartFailDialog(msg: String) {

        var dialog = MaterialDialog(this)
        dialog.setMessage(msg).setPositiveButton(R.string.codemao_bluetooth_sure) { dialog.dismiss() }.show()
    }

    override fun showFirtTimeDialog() {
        var dialog = MaterialDialog(this)
        dialog.setTitle(R.string.coding_exit_codemao_title).setMessage(R.string.coding_exit_codemao_msg).setPositiveButton(R.string.codemao_bluetooth_sure) { dialog.dismiss() }.show()
    }

    override fun showUpdateDialog() {

        var dialog = MaterialDialog(this)
        dialog.setMessage(R.string.coding_update).setNegativeButton(R.string.codemao_bluetooth_cancel) { dialog.dismiss() }.setPositiveButton(R.string.codemao_bluetooth_sure) {
            PageRouter.toUpgrade(this)
            dialog.dismiss()
        }.show()
    }

    override fun getContext(): Context {
        return this
    }

    override fun showUploadWindow(content : String) {
        //supportFragmentManager.beginTransaction()
        var publishFragment = PublishFragment()
        var bundle = Bundle();
        bundle.putString("content", content)
        publishFragment.arguments = bundle
        publishFragment.show(supportFragmentManager, PublishFragment.TAG)

        //var contentView = LayoutInflater.from(this).inflate(R.layout.layout_upload_popup_view, null, false)
        //mUploadWindow = CustomPopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true, this)


        //mUploadWindow.showAtLocation(mRootView, Gravity.CENTER, 0, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == TAKE_PICTURE_CODE) {
            var publishFragment = supportFragmentManager.findFragmentByTag(PublishFragment.TAG);
            if (publishFragment != null && publishFragment is PublishFragment) {
                var uri: Uri;
                if (data != null && data.getData() != null) {
                    uri = data.getData();
                    publishFragment.onPictureTake(uri);
                } else {
                    publishFragment.onPictureTake(null)
                }
            }

        }

    }

}
