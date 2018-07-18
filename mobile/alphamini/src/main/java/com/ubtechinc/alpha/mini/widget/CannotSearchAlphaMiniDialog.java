package com.ubtechinc.alpha.mini.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.ubtechinc.alpha.mini.R;

/**
 * @author：wululin
 * @date：2017/11/14 18:33
 * @modifier：ubt
 * @modify_date：2017/11/14 18:33
 * [A brief description]
 * 搜索不到alphamini
 */

public class CannotSearchAlphaMiniDialog {

    private MaterialDialog mMaterialDialog;
    private ImageView mOpenpowerIv;
    private TextView mTextViewTips1;
    private TextView mTextViewTips2;
    private TextView mConnectCustomerTv;
    private TextView mTitleTv;
    private LinearLayout mBottomLl;
    private TextView mCancelTv;
    private ImageView mCloseBtn;

    public CannotSearchAlphaMiniDialog(Context context){
        mMaterialDialog = new MaterialDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.can_not_search_mini_dailog,null);
        mOpenpowerIv = view.findViewById(R.id.open_power_iv);
        mTextViewTips1 = view.findViewById(R.id.tips_1_tv);
        mTextViewTips2 = view.findViewById(R.id.tips_2_tv);
        mTitleTv = view.findViewById(R.id.title_tv);
        mConnectCustomerTv = view.findViewById(R.id.connect_customer_tv);
        mBottomLl = view.findViewById(R.id.bottom_ll);
        mCancelTv = view.findViewById(R.id.cancel_tv);
        mCloseBtn = view.findViewById(R.id.close_btn);
        mMaterialDialog.setContentView(view);
        mMaterialDialog.setCanceledOnTouchOutside(false);
        mMaterialDialog.setBackgroundResource(android.R.color.transparent);
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMaterialDialog.dismiss();
            }
        });
    }

    public void setTitle(String text){
        mTitleTv.setText(text);
    }

    public void setTitle(int resId){
        mTitleTv.setText(resId);
    }

    public void show(){
        mMaterialDialog.show();
    }

    public void dismiss(){
        mMaterialDialog.dismiss();
    }

    public void setDismissListener(DialogInterface.OnDismissListener onDismissListener){
        mMaterialDialog.setOnDismissListener(onDismissListener);
    }

    public void setTips1Gone(){
        mTextViewTips1.setVisibility(View.GONE);
    }
    public void setTips2Gone(){
        mTextViewTips2.setVisibility(View.GONE);
    }

    public void setTips2Text(String tips2){
        mTextViewTips2.setText(tips2);
    }

    public void setConnectCustomerClickListener(View.OnClickListener listener){
        mBottomLl.setVisibility(View.VISIBLE);
        mConnectCustomerTv.setOnClickListener(listener);
    }
    public void setCancelClickListener(View.OnClickListener listener){
        mCancelTv.setOnClickListener(listener);
    }
}
