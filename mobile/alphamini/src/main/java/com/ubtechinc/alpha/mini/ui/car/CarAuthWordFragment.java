package com.ubtechinc.alpha.mini.ui.car;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseFragment;
import com.ubtechinc.alpha.mini.databinding.CarAuthWordsFragmentBinding;

/**
 * @作者：liudongyang
 * @日期: 18/7/4 19:45
 * @描述: 官方词
 */
public class CarAuthWordFragment extends BaseFragment {

    private CarAuthWordsFragmentBinding binding;

    private AuthWordsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.car_auth_words_fragment, null,false);
        return binding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        adapter = new AuthWordsAdapter(getActivity());
        binding.rvAuthWords.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvAuthWords.setAdapter(adapter);
    }


    public void setMode(boolean isPoliceMode){
        adapter.setMode(isPoliceMode);
    }
}
