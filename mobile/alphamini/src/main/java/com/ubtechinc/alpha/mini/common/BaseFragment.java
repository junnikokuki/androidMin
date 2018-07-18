package com.ubtechinc.alpha.mini.common;

import android.arch.lifecycle.LifecycleFragment;
import android.view.View;
import android.widget.Toast;

import com.squareup.leakcanary.RefWatcher;
import com.ubtechinc.alpha.mini.AlphaMiniApplication;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.utils.LeakUtils;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.alpha.mini.widget.NotifactionView;


public class BaseFragment extends LifecycleFragment {

    private MaterialDialog msgDialog;

    public void toast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public void toast(int msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public void toastSuccess(String msg){
        NotifactionView notifactionView = new NotifactionView(getActivity());
        notifactionView.setNotifactionText(msg,R.drawable.ic_toast_succeed);
    }

    public void toastError(String msg){
        NotifactionView notifactionView = new NotifactionView(getActivity());
        notifactionView.setNotifactionText(msg,R.color.alexa_red_press,R.drawable.ic_toast_warn);
    }

    public void showMsgDialog(String title, String message) {
        if (msgDialog != null) {
            msgDialog.dismiss();
        }else{
            msgDialog = new MaterialDialog(getActivity());
        }
        msgDialog.setTitle(title);
        msgDialog.setMessage(message);
        msgDialog.setPositiveButton(R.string.i_know, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msgDialog.dismiss();
            }
        });
        msgDialog.show();
    }

    public void showPresentMsgDialog(final IPresentListener presentListener) {
        if (msgDialog != null) {
            msgDialog.dismiss();
        }else{
            msgDialog = new MaterialDialog(getActivity());
        }
        msgDialog.setMessage(R.string.present_tip);
        msgDialog.setPositiveButton(R.string.receive_now, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msgDialog.dismiss();
                presentListener.onReceive();
            }
        });
        msgDialog.setNegativeButton(R.string.face_detect_mobile_next_time, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                msgDialog.dismiss();
                presentListener.onClose();
            }
        });
        msgDialog.show();
    }

    public interface IPresentListener {
        void onReceive();
        void onClose();
    }

    public void showMsgDialog(String title, String message, final View.OnClickListener onClickListener) {
        if (msgDialog != null) {
            msgDialog.dismiss();
        }else{
            msgDialog = new MaterialDialog(getActivity());
        }
        msgDialog.setTitle(title);
        msgDialog.setMessage(message);
        msgDialog.setPositiveButton(R.string.i_know, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msgDialog.dismiss();
                if(onClickListener != null){
                    onClickListener.onClick(view);
                }
            }
        });
        msgDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LeakUtils.watch(this);
    }
}
