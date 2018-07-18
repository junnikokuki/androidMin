package com.ubtechinc.alpha.mini.ui;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ubtech.utilcode.utils.KeyboardUtils;
import com.ubtech.utilcode.utils.StringUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.databinding.ActivityModifyPhraseBinding;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.entity.observable.RobotPhraseLive;
import com.ubtechinc.alpha.mini.viewmodel.PhraseViewModel;
import com.ubtechinc.alpha.mini.widget.taglayout.FlowLayout;
import com.ubtechinc.alpha.mini.widget.taglayout.TagAdapter;
import com.ubtechinc.alpha.mini.net.PetphraseRecomandListModule;

import java.util.List;

/**
 * @Date: 2017/12/4.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 修改口头禅
 */

public class AlphaMiniModifyPhraseActivity extends BaseToolbarActivity {

    private ActivityModifyPhraseBinding activityModifyPhraseBinding;

    private PhraseViewModel phraseViewModel;

    private String currentPhrase;

    private String originPhrase;

    public static final String UPDATA_PHRASE = "updata_phrase";

    private boolean isColsedConnectLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityModifyPhraseBinding = DataBindingUtil.setContentView(this, R.layout.activity_modify_phrase);
        activityModifyPhraseBinding.setPresenter(new Presenter());
        phraseViewModel = new PhraseViewModel();
        initView();
        begin2GetRecomandPhrase();
    }

    private void begin2GetRecomandPhrase() {
        phraseViewModel.queryRecomandPhraseLists().observe(this, new Observer<RobotPhraseLive>() {
            @Override
            public void onChanged(@Nullable RobotPhraseLive robotPhraseLive) {
                final List<PetphraseRecomandListModule.RecomandPhrase> phrases =  robotPhraseLive.getRecomandPhraseList();
                if(phrases != null){
                    setData2TagLayout(phrases);
                }
            }
        });
    }



    private void setData2TagLayout(final List<PetphraseRecomandListModule.RecomandPhrase> phrases) {
        activityModifyPhraseBinding.tfPhrase.setAdapter(new TagAdapter<PetphraseRecomandListModule.RecomandPhrase>(phrases) {
            @Override
            public View getView(FlowLayout parent, int position, PetphraseRecomandListModule.RecomandPhrase recomandPhrase) {
                TextView button = new TextView(AlphaMiniModifyPhraseActivity.this);
                button.setTextColor(getResources().getColor(R.color.open_ble));
                button.setBackgroundResource(R.drawable.selector_rectangle_button_selector);
                button.setText(phrases.get(position).getContent());
                return button;
            }

            @Override
            public void onSelected(int position, View view) {
                activityModifyPhraseBinding.etPhraseContent.setText(phrases.get(position).getContent());
                if (!TextUtils.isEmpty(phrases.get(position).getContent())){
                    activityModifyPhraseBinding.etPhraseContent.setSelection(phrases.get(position).getContent().length());
                }
            }
        });
    }
    
    private void initView() {
        initToolbar(activityModifyPhraseBinding.toolbar, getString(R.string.setting_modify_phrase), View.VISIBLE, true);
        initConnectFailedLayout(R.string.setting_modify_file_network_error, R.string.robot_connected_error);
        currentPhrase = originPhrase = getIntent().getStringExtra(PageRouter.CURRENT_PHRASE);

        if (!TextUtils.isEmpty(currentPhrase)){
            activityModifyPhraseBinding.etPhraseContent.setText(currentPhrase);
            activityModifyPhraseBinding.etPhraseContent.setSelection(currentPhrase.length());
            activityModifyPhraseBinding.setHasContent(true);
        }else{
            activityModifyPhraseBinding.setHasContent(false);
        }
        boolean isPhraseCanSave = checkPhraseValidate(currentPhrase);
        setSavePressState(isPhraseCanSave);
    }

    @Override
    protected void onBack() {
        KeyboardUtils.hideSoftInput(AlphaMiniModifyPhraseActivity.this);
        super.onBack();
    }

    @Override
    protected void onAction(View view) {
        String userId = MyRobotsLive.getInstance().getRobotUserId();
        phraseViewModel.updataRobotPhrase(userId, currentPhrase, new PhraseViewModel.IOnChangePhraseListener() {
            @Override
            public void onPhraseChange() {
                setResult(RESULT_OK, new Intent().putExtra(UPDATA_PHRASE, currentPhrase));
                KeyboardUtils.hideSoftInput(AlphaMiniModifyPhraseActivity.this);
                finish();
            }
        });
    }



    private boolean checkPhraseValidate(String content){
        boolean flag = false;
        if (TextUtils.isEmpty(content)) {
            activityModifyPhraseBinding.setPhraseValidate(true);
            activityModifyPhraseBinding.setHintContent(getString(R.string.setting_modify_phrase_hint));
            return flag;
        }
        if (content.length() >= 20) {//大于20的时候是不能保存的
            activityModifyPhraseBinding.setPhraseValidate(false);
            activityModifyPhraseBinding.setHintContent(getString(R.string.setting_limit_hint_phrase, content.length()));
            return flag;
        }
        if (content.trim().length() != content.length()){
            activityModifyPhraseBinding.setPhraseValidate(false);
            activityModifyPhraseBinding.setHintContent(getString(R.string.setting_limit_no_space_phrase));
            return flag;
        }
        if (!StringUtils.onlyChineseOrEnglish(content)){
            activityModifyPhraseBinding.setPhraseValidate(false);
            activityModifyPhraseBinding.setHintContent(getString(R.string.setting_limit_language_phrase));
            return flag;
        }
        flag = true;
        activityModifyPhraseBinding.setPhraseValidate(true);
        activityModifyPhraseBinding.setHintContent(getString(R.string.setting_modify_phrase_hint));
        return flag;
    }

    private void setSavePressState(boolean isPhraseValidate) {
        if(TextUtils.isEmpty(originPhrase) || originPhrase.equals(currentPhrase)){
            actionBtn.setImageResource(R.drawable.ic_top_confirm);
            actionBtn.setAlpha(0x33);
            actionBtn.setClickable(false);
            return;
        }

        if (!isNetworkConnectState()){
            actionBtn.setImageResource(R.drawable.ic_top_confirm);
            actionBtn.setAlpha(0x33);
            actionBtn.setClickable(false);
            return;
        }

        if (isPhraseValidate){
            actionBtn.setImageResource(R.drawable.ic_top_confirm_p);
            actionBtn.setClickable(isPhraseValidate);
        }else{
            actionBtn.setImageResource(R.drawable.ic_top_confirm);
            actionBtn.setClickable(isPhraseValidate);

        }
        actionBtn.setClickable(isPhraseValidate);
    }

    //响应事件
    public class Presenter{
        public void onTextChanged(CharSequence charSequence, int start, int before, int count){
            Log.d(TAG, "onTextChanged: ");
            currentPhrase = charSequence.toString();
            if (TextUtils.isEmpty(currentPhrase)){
                activityModifyPhraseBinding.setHasContent(false);
            }else{
                activityModifyPhraseBinding.setHasContent(true);
            }
            boolean validate = checkPhraseValidate(charSequence.toString());
            setSavePressState(validate);
        }



        public void clearContent(View view){
            activityModifyPhraseBinding.etPhraseContent.setText("");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
