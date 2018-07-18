package com.ubtechinc.alpha.mini.ui.bind;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.databinding.ActivityUnbindSlaverBinding;
import com.ubtechinc.alpha.mini.entity.RobotInfo;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.viewmodel.RobotsViewModel;
import com.ubtechinc.alpha.mini.viewmodel.unbind.UnbindDialog;
import com.ubtechinc.alpha.mini.widget.LinearLayoutColorDivider;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.alpha.mini.net.CheckBindRobotModule;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2018/2/27.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class AlphaMiniUnbindActivity extends BaseToolbarActivity {

    private RecyclerView mRvSlave;

    private ActivityUnbindSlaverBinding mBinding;

    private List<CheckBindRobotModule.User> mSlaverList = new ArrayList<>();

    private RobotsViewModel mBindBussinessModel;

    private UnbindSlaverAdapter mAdapter;

    private RobotInfo curremtRobotInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_unbind_slaver);
        mBindBussinessModel = RobotsViewModel.get();
        UserList list = (UserList) getIntent().getSerializableExtra(PageRouter.INTENT_KEY_ROBOT_USER_LIST);
        curremtRobotInfo = (RobotInfo) getIntent().getSerializableExtra(PageRouter.INTENT_KEY_ROBOT_INFO);
        mSlaverList.addAll(list.getUsers());
        initView();
    }

    public void initView(){
        initToolbar(mBinding.toolbar, getString(R.string.robot_detail_choose_master), View.VISIBLE, true);
        actionBtn.setImageResource(R.drawable.ic_top_confirm);
        backBtn.setImageResource(R.drawable.ic_top_cancel);

        mRvSlave = findViewById(R.id.rv_slaver);
        mRvSlave.setLayoutManager(new LinearLayoutManager(this));
        mRvSlave.addItemDecoration(new LinearLayoutColorDivider(getResources(), R.color.text_gray, R.dimen.one_px, DividerItemDecoration.VERTICAL));
        mAdapter = new UnbindSlaverAdapter(this, mSlaverList);
        mRvSlave.setAdapter(mAdapter);
    }

    @Override
    protected void onAction(View view) {
        final MaterialDialog dialog = new MaterialDialog(AlphaMiniUnbindActivity.this);

        View msgView = LayoutInflater.from(this).inflate(R.layout.dialog_unbind_transfer_dialog, null);
        ((TextView)msgView.findViewById(R.id.text_new_manager))
                .setText(getString(R.string.robot_detail_new_master, mSlaverList.get(mAdapter.getmSelectedIndex()).getNickName()));

        dialog.setTitle(R.string.robot_detail_master_unbind_mine_robot_title)
                .setView(msgView)
                .setPositiveButton(R.string.unbind, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        startUnbind();
                    }
                }).setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        }).setPostiveTextColor(R.color.color_unbind_red).show();
    }


    private void startUnbind() {
        String masterUserId = String.valueOf(mSlaverList.get(mAdapter.getmSelectedIndex()).getUserId());
        if(curremtRobotInfo == null){
            final MaterialDialog dialog = new MaterialDialog(AlphaMiniUnbindActivity.this);
            dialog.setTitle(R.string.cancle_permission_fail)
                    .setMessage(R.string.net_work_error)
                    .setNegativeButton(R.string.i_know, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    }).show();
            return;
        }
        mBindBussinessModel.transferPermission(curremtRobotInfo.getRobotUserId(),masterUserId ,new UnbindDialog.IOnUnbindListener() {
            @Override
            public void onUnbindSuccess() {
                ToastUtils.showShortToast(R.string.cancle_permission_success);
                PageRouter.toMineRobotActivity(AlphaMiniUnbindActivity.this);
                finish();
            }

            @Override
            public void onUnbindFailed() {
                if (isNetworkConnectState()){
                    final MaterialDialog dialog = new MaterialDialog(AlphaMiniUnbindActivity.this);
                    dialog.setTitle(R.string.cancle_permission_fail_network)
                            .setNegativeButton(R.string.i_know, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            }).setPositiveButton(R.string.retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            startUnbind();
                        }
                    }).show();
                }else{
                    final MaterialDialog dialog = new MaterialDialog(AlphaMiniUnbindActivity.this);
                    dialog.setTitle(R.string.cancle_permission_fail)
                            .setMessage(R.string.net_work_error)
                            .setNegativeButton(R.string.i_know, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            }).show();
                }

            }
        });
    }

}
