package com.ubtechinc.alpha.mini.viewmodel.unbind;


import android.content.Context;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.viewmodel.RobotsViewModel;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;


/**
 * @Date: 2017/11/29.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public abstract class UnbindDialog {

    protected Context ctx;

    protected MaterialDialog dialog;

    protected RobotsViewModel mRobotsViewModel;

    protected IOnUnbindListener listener;

    public UnbindDialog(Context ctx){
        this.ctx = ctx;
        dialog = new MaterialDialog(ctx);
        mRobotsViewModel = RobotsViewModel.get();
    }

    public abstract void setContent();

    public void show(){
        if(!dialog.isShowing()){
            dialog.show();
        }
    }


    public void addOnUnbindListener(IOnUnbindListener listener){
        this.listener = listener;
    }

    protected void setDialogProperty(){
        dialog.setBackgroundResource(R.drawable.dialog_bg_shap).setCanceledOnTouchOutside(true);
    }

    public interface IOnUnbindListener {
        void onUnbindSuccess();

        void onUnbindFailed();
    }

}
