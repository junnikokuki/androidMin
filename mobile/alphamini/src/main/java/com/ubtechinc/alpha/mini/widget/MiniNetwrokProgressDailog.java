package com.ubtechinc.alpha.mini.widget;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

import com.ubtechinc.alpha.mini.R;

/**
 * @author：wululin
 * @date：2017/10/23 17:57
 * @modifier：ubt
 * @modify_date：2017/10/23 17:57
 * [A brief description]
 * 网络加载对话框
 */

public class MiniNetwrokProgressDailog extends ProgressDialog {
    private String mLoadingTip;
    private TextView mLoadingTv;
    private MaterialDialog mMaterialDialog;
    private Context mContext;
    public MiniNetwrokProgressDailog(Context context, String content, int theme) {
        super(context, theme);
        this.mLoadingTip = content;
        this.mContext = context;
        setCanceledOnTouchOutside(false);// 设置不能点击对话框外边取消当前对话框
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_pd);
        initView();
        mLoadingTv.setText(mLoadingTip);
        setCanceledOnTouchOutside(false);
    }
    private void initView(){
        mLoadingTv = findViewById(R.id.text_tv);
    }

    public void setLoadingTipText(int strId){
        mLoadingTv.setText(strId);
    }

    public void show(Activity activity) {
        super.show();
        WindowManager m = activity.getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        android.view.WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.height = d.getHeight();   //高度设置为屏幕的0.3
        p.width = d.getWidth();    //宽度设置为全屏
        this.getWindow().setAttributes(p);     //设置生效
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        if(mMaterialDialog == null){
//            mMaterialDialog = new MaterialDialog(mContext);
//        }
//        mMaterialDialog.setTitle(R.string.wenxin_tips)
//                .setMessage(R.string.bangding_tips)
//                .setNegativeButton(R.string.simple_message_cancel, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        mMaterialDialog.dismiss();
//                    }
//                }).setNegativeButton(R.string.simple_message_comfire, new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mMaterialDialog.dismiss();
//                dismiss();
//                ShowErrorDialogUtil.finishAllWifiAcitivity();
//                PageRouter.toOpenPowerTip((Activity) mContext);
//
//            }
//        }).show();
    }


}
