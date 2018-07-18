package com.ubtechinc.alpha.mini.entity.observable;

import android.arch.lifecycle.LiveData;

import com.ubtechinc.alpha.mini.net.PetphraseRecomandListModule;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2017/12/6.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 机器人的口头禅
 */

public class RobotPhraseLive extends LiveData<RobotPhraseLive>{

    private String mUserPhrase;

    private List<PetphraseRecomandListModule.RecomandPhrase> mPhraseList = new ArrayList<>();

    public String getUserPhrase(){
        return mUserPhrase;
    }

    public void setUserPhrase(String phrase){
        this.mUserPhrase = phrase;
        setValue(this);
    }

    public void setRecomandPhraseList(List<PetphraseRecomandListModule.RecomandPhrase> list){
        mPhraseList.clear();
        mPhraseList.addAll(list);
        setValue(this);
    }

    public List<PetphraseRecomandListModule.RecomandPhrase> getRecomandPhraseList() {
        return mPhraseList;
    }
}
