package com.ubtechinc.alpha.mini.ui.share;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.constants.Constants;
import com.ubtechinc.alpha.mini.ui.title.TitleActivity;

import static com.ubtechinc.alpha.mini.constants.Constants.KEY_SHARE_TYPE;
import static com.ubtechinc.alpha.mini.constants.Constants.PERMISSION_REQUEST_KEY;
import static com.ubtechinc.alpha.mini.constants.Constants.SHARE_TYPE_QQ;

public class ShareActivity extends TitleActivity {

    private ShareType shareType;

    private enum ShareType {
        SHARE_TYPE_QQ,
        SHARE_TYPE_WECHAT,
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        titleBackItemView.setTitle(R.string.choose_share);
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_share;
    }

    public void onShareQQ(View view) {
        Intent intent = new Intent(this, SharePermissionActivity.class);
        intent.putExtra(KEY_SHARE_TYPE, SHARE_TYPE_QQ);
        startActivityForResult(intent, Constants.PERMISSION_REQUEST_KEY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(PERMISSION_REQUEST_KEY == requestCode) {
            if(resultCode == RESULT_OK) {
                finish();
                return ;
            }
        }
    }

    public void onShareWechat(View view) {
        Intent intent = new Intent(this, SharePermissionActivity.class);
        intent.putExtra(KEY_SHARE_TYPE, Constants.SHARE_TYPE_WECHAT);
        startActivityForResult(intent, PERMISSION_REQUEST_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                Log.d(TAG,"inputMethodManager.isActive()" + inputMethodManager.isActive());
                if (inputMethodManager.isActive()) {
                    //因为是在fragment下，所以用了getView()获取view，也可以用findViewById（）来获取父控件
                    getWindow().getDecorView().requestFocus();//使其它view获取焦点.这里因为是在fragment下,所以便用了getView(),可以指定任意其它view
                    inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                }
            }
        }, 200);
    }
}
