package com.ubtechinc.alpha.mini.ui.utils;

import android.view.View;
import android.widget.TextView;

import com.ubtechinc.alpha.mini.R;

/**
 * Created by ubt on 2018/4/20.
 */

public class RobotReconnectTipUtil implements View.OnClickListener{

    private View tipsView;

    private TextView tipsText;

    private View closeBtn;

    private View retryView;

    private int count;

    private BtnClickListener listener;

    public RobotReconnectTipUtil(View tipsView,BtnClickListener listener) {
        this.tipsView = tipsView;
        this.listener = listener;
        init();
    }

    private void init() {
        if (tipsView == null) {
            throw new RuntimeException("tipsVIew is null");
        }
        tipsText = tipsView.findViewById(R.id.tips_line1);
        closeBtn = tipsView.findViewById(R.id.tips_close_btn);
        retryView = tipsView.findViewById(R.id.tips_switch_btn);
        if (tipsText == null || closeBtn == null || retryView == null) {
            throw new RuntimeException("tipsText or closeBtn or retryView is null");
        }
        setListener();
    }

    public void showOffline() {
        show(R.string.still_offline);
    }

    public void showDisconnect() {
        show(R.string.still_disconnect);
    }

    private void show(int msgId){
        count++;
        tipsText.setText(msgId);
        if(count == 1){
            count = 3;
            tipsView.setVisibility(View.VISIBLE);
//            tipsText.setText(msgId);
        }else{
            int tryCount= count % 3;
            if(tryCount == 0){
                tipsView.setVisibility(View.VISIBLE);
//                tipsText.setText(msgId);
            }
        }

    }

    /**
     * 连接成功自动关闭时调用
     */
    public void hidden() {
        tipsView.setVisibility(View.GONE);
        count = 0;

    }

    /**
     * 用户手动点击按钮时关闭
     */
    public void close() {
        tipsView.setVisibility(View.GONE);
        count = 3;
    }

    private void setListener(){
        retryView.setOnClickListener(this);
        closeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tips_close_btn:
                close();
                break;
            case R.id.tips_switch_btn:
                hidden();
                if(listener != null){
                    listener.onSwitchWifi();
                }
                break;
        }
    }

    public interface BtnClickListener{

        public void onSwitchWifi();
    }
}
