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
import com.ubtechinc.alpha.mini.databinding.CarDefineOwnWordsFragmentBinding;

/**
 * @作者：liudongyang
 * @日期: 18/7/4 19:46
 * @描述: 自定义词汇
 */
public class CarDefineOwnWordFragment extends BaseFragment {

    private CarDefineOwnWordsFragmentBinding binding;

    private DefineWordsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.car_define_own_words_fragment, null,false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        adapter = new DefineWordsAdapter(getActivity());
        binding.rvWordsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvWordsList.setAdapter(adapter);
    }

    public void addDefineWords(String words){
        adapter.addDefineWords(words);

        binding.rvWordsList.smoothScrollToPosition(adapter.getItemCount() - 1);
    }



    public void setMode(boolean isModeChange){
        adapter.setMode(isModeChange);
    }
}
