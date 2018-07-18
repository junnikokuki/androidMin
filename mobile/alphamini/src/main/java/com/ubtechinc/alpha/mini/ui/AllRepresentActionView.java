package com.ubtechinc.alpha.mini.ui;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.ui.adapter.RepresentActionAdapter;
import com.ubtechinc.alpha.mini.widget.LinearLayoutColorDivider;
import com.ubtechinc.alpha.mini.widget.PopView;
import com.ubtechinc.alpha.mini.net.ActionRepresentiveRecomandListModule;

import java.util.List;

/**
 * @Date: 2017/12/1.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 所有代表动作
 */

public class AllRepresentActionView extends PopView implements View.OnClickListener{

    private RecyclerView recyclerView;
    private RepresentActionAdapter adapter;
    private IOnChooseRepresentiveActionListener listener;
    private TextView confitTv;

    public AllRepresentActionView(View rootView) {
        super(rootView);
    }

    public void setChooseActionListener(IOnChooseRepresentiveActionListener listener){
        this.listener = listener;
    }

    @Override
    protected void setViewContent() {
        recyclerView = mRootView.findViewById(R.id.rv_representive_action);
        adapter = new RepresentActionAdapter(mCtx);
        recyclerView.setLayoutManager(new LinearLayoutManager(mCtx));
        recyclerView.addItemDecoration(new LinearLayoutColorDivider(mCtx.getResources(),R.color.border, R.dimen.one_px,LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        mRootView.findViewById(R.id.space).setOnClickListener(this);
        mRootView.findViewById(R.id.title_cancel).setOnClickListener(this);
        confitTv = mRootView.findViewById(R.id.title_comfirm);
        confitTv.setOnClickListener(this);
    }

    public void setConfirmClickable(boolean clickable){
        if (clickable){
            confitTv.setTextColor(ContextCompat.getColor(mCtx, R.color.open_ble));
        }else{
            confitTv.setTextColor(ContextCompat.getColor(mCtx, R.color.color_gray));
        }
        confitTv.setClickable(clickable);
    }

    public void setData(List<ActionRepresentiveRecomandListModule.RecomandAction> actions){
        adapter.notifyDataChange(actions);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_comfirm:
                setChooseAction();
                dismiss();
                break;
            case R.id.space:
            case R.id.title_cancel:
                dismiss();
                break;
        }
    }

    private void setChooseAction() {
        if (listener != null){
            listener.onChooseAction(adapter.getChooseAction());
        }
    }

    public void loacteFirstIndexOfAction(String action){
        adapter.loacteFirstIndexOfAction(action);
    }


    public interface IOnChooseRepresentiveActionListener{
        void onChooseAction(ActionRepresentiveRecomandListModule.RecomandAction action);
    }



}
