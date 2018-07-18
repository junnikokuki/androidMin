package com.ubtechinc.alpha.mini.viewmodel.unbind;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.ui.adapter.UnBindDialogAdapter;
import com.ubtechinc.alpha.mini.net.CheckBindRobotModule;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2017/11/29.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class MasterUnbindDialog extends UnbindDialog implements View.OnClickListener, AdapterView.OnItemClickListener{

    private List<CheckBindRobotModule.User> robotOwners = new ArrayList<>();
    private int currentMasterPosition = 0;
    private UnBindDialogAdapter adapter;

    public MasterUnbindDialog(Context context, List<CheckBindRobotModule.User> slaverUser){
        super(context);
        if (slaverUser == null || slaverUser.size() == 0){
            throw new IllegalArgumentException();
        }else{
            robotOwners.clear();
            robotOwners.addAll(slaverUser);
        }
    }


    @Override
    public void setContent() {
        View view = LayoutInflater.from(ctx).inflate(R.layout.dialog_unbind, null);
        view.findViewById(R.id.tv_comfire).setOnClickListener(this);
        view.findViewById(R.id.tv_cancel).setOnClickListener(this);
        view.findViewById(R.id.tv_unbind).setOnClickListener(this);
        ListView listview = view.findViewById(R.id.lv_bind);
        listview.setOnItemClickListener(this);
        adapter = new UnBindDialogAdapter(ctx, robotOwners);
        listview.setAdapter(adapter);
        dialog.setContentView(view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_comfire:
                mRobotsViewModel.transferPermission(MyRobotsLive.getInstance().getRobotUserId(),String.valueOf(robotOwners.get(currentMasterPosition).getUserId()), listener);
                dialog.dismiss();
                break;
            case R.id.tv_cancel:
                dialog.dismiss();
                break;
            case R.id.tv_unbind:
                mRobotsViewModel.doThrowCurrentPermission(listener);
                dialog.dismiss();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        currentMasterPosition = position;
        adapter.setMasterSeleted(currentMasterPosition);
    }
}
