package com.ubtechinc.alpha.mini.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtech.utilcode.utils.ListUtils;
import com.ubtech.utilcode.utils.LogUtils;
import com.ubtech.utilcode.utils.network.NetworkHelper;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseActivity;
import com.ubtechinc.alpha.mini.common.BaseFragment;
import com.ubtechinc.alpha.mini.databinding.FragmentCodingBinding;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.ui.bluetooth.BleAutoConnectActivity;
import com.ubtechinc.alpha.mini.ui.codemao.ConnectActivity;
import com.ubtechinc.alpha.mini.ui.codemao.StarTrekActivity;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.bluetooth.UbtBluetoothManager;
import com.ubtechinc.bluetooth.utils.UbtBluetoothHelper;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

public class CodingFragment extends BaseFragment implements View.OnClickListener{
    FragmentCodingBinding codingBinding;
    private MaterialDialog msgDialog;
    private boolean mIsJumpBanding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         codingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_coding, null, false);
        return codingBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        codingBinding.btnCode.setOnClickListener(this);
        codingBinding.btnExplorer.setOnClickListener(this);
    }



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            if (CollectionUtils.isEmpty(MyRobotsLive.getInstance().getRobots().getData())) {
                if (UbtBluetoothManager.getInstance().isOpenBluetooth() &&
                        AndPermission.hasPermission(getActivity(), Permission.LOCATION)) {
                    UbtBluetoothHelper.getInstance().startScan("");
                    UbtBluetoothHelper.getInstance().setBluetoothScanListener(new UbtBluetoothHelper.BluetoothScanListener() {
                        @Override
                        public void scanEnd() {
                            if (mIsJumpBanding) {
                                jumpBanding();
                                ((BaseActivity) getActivity()).dismissDialog();
                            }
                        }
                    });
                }
            }

        }
    }


    private void jumpBanding() {
        mIsJumpBanding = false;
        LogUtils.d( " jumpBanding");
        if (!NetworkHelper.sharedHelper().isNetworkAvailable()) {
            showMsgDialog(null, getString(R.string.network_invalid_tip));
            return;
        }
        if (UbtBluetoothHelper.getInstance().getPurposeDevice() != null) {

            Intent intent = new Intent(getActivity(), BleAutoConnectActivity.class);
            intent.putExtra(UbtBluetoothHelper.KEY_UBTBLUETOOTH, UbtBluetoothHelper.getInstance().getPurposeDevice());
            startActivity(intent);
        } else if (UbtBluetoothHelper.getInstance().hasDevices()) {
            PageRouter.toBleAutoConnectActivity(getActivity());
        } else {
            PageRouter.toOpenPowerTip(getActivity());
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_code :

                if(ListUtils.isEmpty(MyRobotsLive.getInstance().getRobots().getData())) {

                    showMsgDialog();
                }else {

                     PageRouter.toCodingActivity(getActivity());
//                    Intent intent = new Intent(getActivity(), ConnectActivity.class);
//                    getActivity().startActivity(intent);

                }

                break;
            case R.id.btn_explorer :
//                Intent intent = new Intent(getActivity(), ConnectActivity.class);
//                    getActivity().startActivity(intent);
                Bundle bundle = new Bundle();
                bundle.putString(PageRouter.INTENT_KEY_PAGE_TYPE, PageRouter.INTENT_DATA_COURSE);
                PageRouter.toCodingActivity(getActivity(), bundle);
//                showPrmpt();
                break;
        }
    }


    private void showMsgDialog() {
        if (msgDialog != null) {
            msgDialog.dismiss();
        }else{
            msgDialog = new MaterialDialog(getActivity());
        }
        msgDialog.setTitle(R.string.codemao_bind_alpha_mini).setMessage(R.string.codemao_bind_msg).setPositiveButton(R.string.codemao_bind_robot, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msgDialog.dismiss();
                UbtBluetoothManager.getInstance().setChangeWifi(false);
                UbtBluetoothManager.getInstance().setFromHome(false);
                UbtBluetoothManager.getInstance().setFromCodeMao(true);
                if (UbtBluetoothManager.getInstance().isOpenBluetooth() &&
                        AndPermission.hasPermission(getActivity(), Permission.LOCATION)) {

                    if (UbtBluetoothHelper.getInstance().scanEnd()) {
                        jumpBanding();
                    } else {
                        mIsJumpBanding = true;
                        ((BaseActivity) getActivity()).showLoadingDialog();
                    }

                } else {
                    PageRouter.toOpenBluetooth(getActivity());
                }

            }
        }).setNegativeButton(R.string.codemao_skip, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgDialog.dismiss();
                PageRouter.toCodingActivity(getActivity());
            }
        }).setNeturalButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgDialog.dismiss();
            }
        }).show();
    }

    @Override
    public void onResume() {
        LogUtils.d("onResume");
        super.onResume();
    }

     private void  showPrmpt() {
         final MaterialDialog dialog = new  MaterialDialog(getActivity());
        dialog.setMessage(R.string.coding_coming_soon).setPositiveButton(R.string.codemao_bluetooth_sure, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        }).show();
    }

}
