package com.ubtechinc.alpha.mini.ui.contacts;

import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtech.utilcode.utils.network.NetworkHelper;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseFragment;
import com.ubtechinc.alpha.mini.databinding.FragmentCallRecordBinding;
import com.ubtechinc.alpha.mini.entity.CallRecord;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.ui.adapter.CallRecordAdapter;
import com.ubtechinc.alpha.mini.viewmodel.CallRecordViewModel;
import com.ubtechinc.alpha.mini.widget.MiniDividerDecoration;
import com.ubtechinc.alpha.mini.entity.RobotInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Date: 2018/1/30.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class AlphaMiniCallRecordFragment extends BaseFragment implements MiniDividerDecoration.IDividerCallBack{

    FragmentCallRecordBinding binding;

    CallRecordAdapter adapter;

    private List<CallRecord> records;

    private CallRecordViewModel callRecordViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_call_record, null, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        records = new ArrayList<>();
        showEmptyLay();
        callRecordViewModel = new CallRecordViewModel();
        adapter = new CallRecordAdapter(getActivity(), records);
        binding.callRecordList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.callRecordList.addItemDecoration(new MiniDividerDecoration(getActivity(), 2 * getResources().getDimensionPixelOffset(R.dimen.ten_dp), this));
        binding.callRecordList.setAdapter(adapter);
        initData();
    }

    private void initData() {
        loadMore();
        binding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                sync();
            }
        });
        binding.refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshLayout) {
                loadMore();
            }
        });
        RobotInfo currentRobot = MyRobotsLive.getInstance().getCurrentRobot().getValue();
        if (NetworkHelper.sharedHelper().isNetworkAvailable() && currentRobot != null
                && currentRobot.getOnlineState() == RobotInfo.ROBOT_STATE_ONLINE) {
            binding.refreshLayout.autoRefresh();
        }else{
            binding.refreshLayout.setEnableRefresh(false);
        }
    }

    private void loadMore() {
        final LiveResult result = callRecordViewModel.load(records.size(), MyRobotsLive.getInstance().getRobotUserId());
        result.observe(this, new Observer<LiveResult>() {
            @Override
            public void onChanged(@Nullable LiveResult liveResult) {
                switch (liveResult.getState()) {
                    case SUCCESS:
                        List<CallRecord> callRecords = (List<CallRecord>) liveResult.getData();
                        if (!CollectionUtils.isEmpty(callRecords)) {
                            showDataLay();
                        } else {
                            if (!CollectionUtils.isEmpty(records)) {
                                binding.refreshLayout.setEnableLoadmore(false);
                            } else {
                                showEmptyLay();
                            }
                        }
                        refreshData(callRecords);
                        binding.refreshLayout.finishLoadmore();
                        result.removeObserver(this);
                        break;
                    case FAIL:
                        toastError(getString(R.string.network_load_fail));
                        break;
                }
            }
        });
    }

    private void sync() {
        final LiveResult result = callRecordViewModel.sync(MyRobotsLive.getInstance().getRobotUserId());
        result.observe(this, new Observer<LiveResult>() {
            @Override
            public void onChanged(@Nullable LiveResult liveResult) {
                List<CallRecord> callRecords = (List<CallRecord>) liveResult.getData();
                switch (liveResult.getState()) {
                    case SUCCESS:
                        refreshData(callRecords);
                        if(getUserVisibleHint()){
                            toastSuccess(getString(R.string.sync_success));
                        }
                        binding.refreshLayout.finishRefresh();
                        result.removeObserver(this);
                        break;
                    case LOADING:
                        refreshData(callRecords);
                        break;
                    case FAIL:
                        if(getUserVisibleHint()){
                            toastError(getString(R.string.sync_fail));
                        }
                        break;
                }
            }
        });
    }

    private void refreshData(List<CallRecord> callRecords) {
        if (!CollectionUtils.isEmpty(callRecords)) {
            records.removeAll(callRecords);
            records.addAll(callRecords);
            Collections.sort(records, new Comparator<CallRecord>() {
                @Override
                public int compare(CallRecord callRecord, CallRecord t1) {
                    return (int) (t1.getCallTime() - callRecord.getCallTime());
                }
            });
            adapter.updateList(records);
            showDataLay();
        }
    }

    private void showDataLay() {
        binding.noDataLay.setVisibility(View.GONE);
        binding.callRecordList.setVisibility(View.VISIBLE);
    }

    private void showEmptyLay() {
        binding.noDataLay.setVisibility(View.VISIBLE);
        binding.callRecordList.setVisibility(View.GONE);
    }


    public void setConnectFail(boolean fail){
        binding.refreshLayout.setEnableRefresh(!fail);
        binding.refreshLayout.setEnableLoadmore(!fail);
    }

    @Override
    public boolean needDivideLineAdjustToPosition(int position) {
        if (position < 0 || position >= records.size()) {
            return false;
        }
        return true;
    }
}
