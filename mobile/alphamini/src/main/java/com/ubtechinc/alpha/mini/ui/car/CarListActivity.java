package com.ubtechinc.alpha.mini.ui.car;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ubtechinc.alpha.JimuCarGetBleList;
import com.ubtechinc.alpha.JimuErrorCodeOuterClass;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseActivity;
import com.ubtechinc.alpha.mini.entity.CarListInfo;
import com.ubtechinc.alpha.mini.event.JimuCarDriverModeEvent;
import com.ubtechinc.alpha.mini.event.JimuCarListEvent;
import com.ubtechinc.alpha.mini.utils.CustomerCallUtils;
import com.ubtechinc.alpha.mini.widget.LoadingDialog;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.nets.http.ThrowableWrapper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class CarListActivity extends BaseActivity implements View.OnClickListener{


    private CarListAdapter mCarListAdapter;
    private RecyclerView mCarListRv;
    private RelativeLayout mNoFindCarRl;
    private ArrayList<CarListInfo> mCarListInfos = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_car_list);
        initView();
        initData();
    }

    @Override
    protected void setStatuesBar() {

    }

    private void initView() {

        mNoFindCarRl = findViewById(R.id.rl_no_find_car);
        TextView redetect = findViewById(R.id.tv_carlist_redetect);
        redetect.setOnClickListener(this);
        TextView connectCustomer = findViewById(R.id.tv_carlist_contact_customers);
        connectCustomer.setOnClickListener(this);
        ImageView backImage = findViewById(R.id.iv_carlist_back);
        backImage.setOnClickListener(this);
        ImageView closeImage = findViewById(R.id.iv_carlist_close);
        closeImage.setOnClickListener(this);

        mCarListRv = findViewById(R.id.rv_carlist);
        mCarListAdapter = new CarListAdapter(this);
        mCarListInfos = CarMsgManager.mCarList;
        mCarListAdapter.setData(mCarListInfos);
        mCarListAdapter.setOnItemClick(new CarListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int positon) {
                LoadingDialog.getInstance(CarListActivity.this).show();
                CarMsgManager.connectBleCarCmd(mCarListInfos.get(positon).getDeviceMac(), new CarModeInterface.ICarConnectedListener() {
                    @Override
                    public void onSuccess() {
                        LoadingDialog.dissMiss();
                    }

                    @Override
                    public void onFail() {
                        LoadingDialog.dissMiss();
                    }
                });
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mCarListRv.setLayoutManager(layoutManager);
        mCarListRv.addItemDecoration(new SpacesItemDecoration(30));
        mCarListRv.setAdapter(mCarListAdapter);
    }

    private void initData() {
        if (mCarListInfos.size() > 0) {
            mNoFindCarRl.setVisibility(View.GONE);
            mCarListRv.setVisibility(View.VISIBLE);
        } else {
            mCarListRv.setVisibility(View.GONE);
            mNoFindCarRl.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_carlist_back:
                finish();
                break;
            case R.id.iv_carlist_close:
                finish();
                break;
            case R.id.tv_carlist_redetect:
                LoadingDialog.getInstance(this).show();
                CarMsgManager.searchJimuCarBleCmd(CarMsgManager.START_SEARCH_BLE, new CarModeInterface.ICarListListener() {
                    @Override
                    public void onSuccess(JimuCarGetBleList.GetJimuCarBleListResponse response) {
                        if (response != null && response.getErrorCode() == JimuErrorCodeOuterClass.JimuErrorCode.REQUEST_SUCCESS
                                && response.getBleList() != null && response.getBleList().size() > 0) {
                            List<JimuCarGetBleList.JimuCarBle> carList = response.getBleList();
                            mCarListInfos.clear();
                            for (int i = 0; i < carList.size(); i++) {
                                CarListInfo carListInfo = new CarListInfo();
                                carListInfo.setDeviceMac(carList.get(i).getMac());
                                carListInfo.setDeviceBleName(carList.get(i).getName());
                                mCarListInfos.add(carListInfo);
                            }
                            showCarList();
                        }

                        LoadingDialog.dissMiss();
                    }

                    @Override
                    public void onFail(ThrowableWrapper e) {
                        LoadingDialog.dissMiss();
                    }
                });
                break;
            case R.id.tv_carlist_contact_customers:
                showDialog();
                break;
            default:
                break;
        }
    }

    private void showDialog() {
        final MaterialDialog phoneDialog = new MaterialDialog(this);
        phoneDialog.setTitle(R.string.contact_customers)
                .setMessage(R.string.customer_service_hotline)
                .setNegativeButton(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        phoneDialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.call_services, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        phoneDialog.dismiss();
                        CustomerCallUtils.call(CarListActivity.this);
                    }
                });
        phoneDialog.show();
    }

    private void showCarList() {
        if (mCarListRv.getVisibility() != View.VISIBLE) {
            mCarListRv.setVisibility(View.VISIBLE);
            mNoFindCarRl.setVisibility(View.GONE);
        }

        mCarListAdapter.setData(mCarListInfos);
        mCarListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(JimuCarListEvent jimuCarListEvent) {
        Log.i("test", "CarListActivity JimuCarListEvent");
        if (jimuCarListEvent != null && jimuCarListEvent.getCarList() != null && jimuCarListEvent.getCarList().size() > 0) {
            mCarListInfos = jimuCarListEvent.getCarList();
            showCarList();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(JimuCarDriverModeEvent jimuCarDriverModeEvent) {
        Log.i("test","CarWaitingConnectActivity jimuCarDriverModeEvent " + jimuCarDriverModeEvent.getDriveMode());
        if (jimuCarDriverModeEvent.getDriveMode() == JimuCarDriverModeEvent.ENTER_DRIVE_MODE) {
            Intent intent = new Intent(CarListActivity.this, CarRemindActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
