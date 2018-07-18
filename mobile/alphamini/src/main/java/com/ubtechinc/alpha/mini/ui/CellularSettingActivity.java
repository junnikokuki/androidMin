package com.ubtechinc.alpha.mini.ui;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.databinding.ActivityCellDataSettingBinding;
import com.ubtechinc.alpha.mini.entity.SimState;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.viewmodel.CellDataViewModel;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;

/**
 * @Date: 2018/02/06.
 * @Author: shiyi.wu
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 机器人蜂窝数据设置页面
 */

public class CellularSettingActivity extends BaseToolbarActivity {

    public static final String CELL_DATA_KEY = "cell_data";

    public static final String ROAM_DATA_KEY = "roam_data";

    private ActivityCellDataSettingBinding mDataBinding;

    private CellDataViewModel cellDataViewModel;

    boolean cellData;
    boolean roamData;

    private MaterialDialog cellDataDialog;
    private MaterialDialog roamDataDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_cell_data_setting);
        cellDataViewModel = new CellDataViewModel();
        init();
        initData();
    }

    private void init() {
        View toolbar = findViewById(R.id.toolbar);
        initToolbar(toolbar, getString(R.string.cell_data), View.GONE, false);
        initConnectFailedLayout(R.string.mobile_connected_error, R.string.robot_connected_error);
        mDataBinding.cellularSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                opCellData(b);
            }
        });
        mDataBinding.roamSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                opRoamData(b);
            }
        });
        mDataBinding.cellularSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opCellData(!cellData);
            }
        });
        mDataBinding.roamSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opRoamData(!roamData);
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            cellData = intent.getBooleanExtra(CELL_DATA_KEY, false);
            roamData = intent.getBooleanExtra(ROAM_DATA_KEY, false);
        }
        mDataBinding.setCellData(cellData);
        mDataBinding.setRoamData(roamData);
        if (isNetworkConnectState() && isRobotConnectState()) {
            getCellState();
        }
    }

    private void getCellState() {
        mDataBinding.setDisableAll(true);
        cellDataViewModel.getCellDataState().observe(this, new Observer<LiveResult>() {
            @Override
            public void onChanged(@Nullable LiveResult liveResult) {
                switch (liveResult.getState()) {
                    case SUCCESS:
                        SimState state = (SimState) liveResult.getData();
                        mDataBinding.setDisableAll(false);
                        cellData = state.isCellData();
                        roamData = state.isRoamData();
                        mDataBinding.setCellData(cellData);
                        mDataBinding.setRoamData(roamData);
                        liveResult.removeObserver(this);
                        break;
                    case FAIL:
                        mDataBinding.setDisableAll(false);
                        liveResult.removeObserver(this);
                        break;
                }
            }
        });
    }

    private void opCellData(final boolean isOpen) {
        if (isOpen) {
            if (cellDataDialog == null) {
                cellDataDialog = new MaterialDialog(this);
                cellDataDialog.setTitle(R.string.sure_open_cell_data);
                cellDataDialog.setMessage(R.string.sure_open_cell_data_detail);
                cellDataDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cellDataDialog.dismiss();
                        mDataBinding.setCellData(cellData);
                    }
                });
                cellDataDialog.setPositiveButton(R.string.agree, new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        cellDataDialog.dismiss();
                        doOpCellData(isOpen);
                    }
                });
            }
            cellDataDialog.show();
        } else {
            doOpCellData(isOpen);
        }

    }

    private void doOpCellData(final boolean isOpen) {
        showLoadingDialog();
        cellDataViewModel.opCellData(isOpen).observe(CellularSettingActivity.this, new Observer<LiveResult>() {
            @Override
            public void onChanged(@Nullable LiveResult liveResult) {
                switch (liveResult.getState()) {
                    case SUCCESS:
                        dismissDialog();
                        cellData = isOpen;
                        if(!isOpen){
                            roamData = false;
                            mDataBinding.setRoamData(roamData);
                        }
                        mDataBinding.setCellData(cellData);
                        break;
                    case FAIL:
                        dismissDialog();
                        mDataBinding.setCellData(cellData);
                        toastError(getString(R.string.op_fail));
                        break;
                }
            }
        });
    }

    private void opRoamData(final boolean isOpen) {
        if (roamData == isOpen) {
            return;
        }
        if (isOpen) {
            if (roamDataDialog == null) {
                roamDataDialog = new MaterialDialog(this);
                roamDataDialog.setTitle(R.string.sure_open_roam_data);
                roamDataDialog.setMessage(R.string.sure_open_roam_data_detail);
                roamDataDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        roamDataDialog.dismiss();
                        mDataBinding.setRoamData(roamData);
                    }
                });
                roamDataDialog.setPositiveButton(R.string.agree, new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        roamDataDialog.dismiss();
                        doOpRoamData(isOpen);
                    }
                });
            }
            roamDataDialog.show();
        } else {
            doOpRoamData(isOpen);
        }

    }

    private void doOpRoamData(final boolean isOpen) {
        showLoadingDialog();
        cellDataViewModel.opRoamData(isOpen).observe(CellularSettingActivity.this, new Observer<LiveResult>() {
            @Override
            public void onChanged(@Nullable LiveResult liveResult) {
                switch (liveResult.getState()) {
                    case SUCCESS:
                        dismissDialog();
                        roamData = isOpen;
                        mDataBinding.setRoamData(roamData);
                        break;
                    case FAIL:
                        dismissDialog();
                        mDataBinding.setRoamData(roamData);
                        toastError(getString(R.string.op_fail));
                        break;
                }
            }
        });
    }

    @Override
    protected void setConnectFailedLayout(boolean robotConnectState, boolean networkConnectState) {
        super.setConnectFailedLayout(robotConnectState, networkConnectState);
        if (robotConnectState && networkConnectState) {
            if (mDataBinding != null) {
                mDataBinding.setDisableAll(false);
            }
        } else {
            if (mDataBinding != null) {
                mDataBinding.setDisableAll(true);
            }
        }
    }
}
