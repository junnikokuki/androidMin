package com.ubtechinc.alpha.mini.ui;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.databinding.ActivityAlphaMiniFileBinding;
import com.ubtechinc.alpha.mini.entity.RepresentActionLive;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.entity.observable.RobotPhraseLive;
import com.ubtechinc.alpha.mini.viewmodel.PhraseViewModel;
import com.ubtechinc.alpha.mini.viewmodel.RepresentiveActionViewModel;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.alpha.mini.widget.PopView;
import com.ubtechinc.alpha.mini.net.ActionRepresentiveRecomandListModule;

import java.util.List;


/**
 * @Date: 2017/11/30.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 机器人档案界面(包含机器人口头禅，机器人代表动作)
 */

public class AlphaMiniFileActivity extends BaseToolbarActivity implements AllRepresentActionView.IOnChooseRepresentiveActionListener, PopView.IAnmamtionListener {

    private ActivityAlphaMiniFileBinding binding;

    private final int RequestCode = 10000;

    private PhraseViewModel phraseViewModel;

    private RepresentiveActionViewModel representiveActionViewModel;

    private String currentPhrase;

    private boolean isColsedConnectLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_alpha_mini_file);
        phraseViewModel = new PhraseViewModel();
        representiveActionViewModel = new RepresentiveActionViewModel();
        initView();
        beginToFetchRobotFile();
    }

    private void beginToFetchRobotFile() {
        String userId = MyRobotsLive.getInstance().getRobotUserId();
        phraseViewModel.queryUserPhrase(userId).observe(this, new Observer<RobotPhraseLive>() {
            @Override
            public void onChanged(@Nullable RobotPhraseLive robotPhraseLive) {
                currentPhrase = robotPhraseLive.getUserPhrase();
                if (!TextUtils.isEmpty(currentPhrase)){
                    binding.setPhrase(currentPhrase);
                }
            }
        });
        representiveActionViewModel.quertyRobotRepresentiveAction(userId).observe(this, new Observer<RepresentActionLive>() {
            @Override
            public void onChanged(@Nullable RepresentActionLive representActionLive) {
                String representAction = representActionLive.getAction();
                if (!TextUtils.isEmpty(representAction)){
                    binding.setRepresentAction(representAction);
                }
            }
        });

    }


    TextView tvConnectedText;
    ImageView ivCloseConnectedLayout;
    View connectFailed;
    private void initView() {
        TextView title = binding.toolbar.findViewById(R.id.toolbar_title);
        title.setText(R.string.robot_dangan);
        binding.toolbar.findViewById(R.id.toolbar_action).setVisibility(View.GONE);
        binding.toolbar.findViewById(R.id.toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initConnectFailedLayout(R.string.setting_modify_file_network_error, R.string.robot_connected_error);
        connectFailed = findViewById(R.id.layout_connect);
        tvConnectedText = connectFailed.findViewById(R.id.tv_error_info);
        ivCloseConnectedLayout = connectFailed.findViewById(R.id.iv_close_connect_layout);
        ivCloseConnectedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isColsedConnectLayout = true;
                connectFailed.setVisibility(View.GONE);
            }
        });
    }


    public void modifyRepresentationAction(View view){
        if (isNetworkConnectState()){
            showRepresentActionView();
        }else{
            showErrorHintDialog();
        }
    }

    public void modifyPhrase(View view){
        if (isNetworkConnectState()){
            PageRouter.toMiniModifyPhraseActivity(this, currentPhrase, RequestCode);
        }else{
            showErrorHintDialog();
        }

    }

    private void showErrorHintDialog(){
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setMessage(R.string.setting_file_nettwork_error_hint)
                .setPositiveButton(R.string.i_know, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private AllRepresentActionView allRepresentActionView;

    private void showRepresentActionView() {
        if (allRepresentActionView == null){
            allRepresentActionView = new AllRepresentActionView(binding.llRepresentativeLayout);
            allRepresentActionView.addAnimationListener(this);
            allRepresentActionView.setChooseActionListener(this);
            beginToGetActions();
        }
        allRepresentActionView.loacteFirstIndexOfAction(binding.getRepresentAction());//找到当前Actoin在列表中的位置
        allRepresentActionView.show();
    }

    private void beginToGetActions() {
        representiveActionViewModel.queryRepresentiveActions().observe(this, new Observer<RepresentActionLive>() {
            @Override
            public void onChanged(@Nullable RepresentActionLive representActionLive) {
                List<ActionRepresentiveRecomandListModule.RecomandAction> actions = representActionLive.getRecomandActions();
                if (actions != null){
                    allRepresentActionView.setData(actions);
                    allRepresentActionView.loacteFirstIndexOfAction(binding.getRepresentAction());//找到当前Actoin在列表中的位置
                }else{
                    ToastUtils.showShortToast("没有代表动作");
                }
            }
        });
    }

    @Override
    protected void setConnectFailedLayout(boolean robotConnectState, boolean networkConnectState) {
        if(allRepresentActionView != null){
            allRepresentActionView.setConfirmClickable(networkConnectState);
        }
        super.setConnectFailedLayout(robotConnectState, networkConnectState);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            currentPhrase = data.getStringExtra(AlphaMiniModifyPhraseActivity.UPDATA_PHRASE);
            popOfflineHintModifyFileDialog();
            binding.setPhrase(currentPhrase);
        }
    }

    private void popOfflineHintModifyFileDialog() {
        if (!isRobotConnectState()){
            final MaterialDialog dialog = new MaterialDialog(this);
            dialog.setMessage(R.string.setting_phrase_offline_modify_hint)
                    .setPositiveButton(R.string.i_know, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    @Override
    public void onChooseAction(final ActionRepresentiveRecomandListModule.RecomandAction action) {
        if (!isRobotConnectState()){
            popOfflineHintModifyFileDialog();
        }
        ToastUtils.showShortToast(action.getDescription());
        String userId = MyRobotsLive.getInstance().getRobotUserId();
        representiveActionViewModel.updataRobotAction(userId, action.getId(), new RepresentiveActionViewModel.IOnActionChangeListener() {
            @Override
            public void onActionUpdata() {
                binding.setRepresentAction(action.getDescription());
            }
        });
    }

    @Override
    public void animationStart() {
        Log.d(TAG, "animationStart: ");
        binding.rlRepresentativeAction.setClickable(false);
    }

    @Override
    public void animationEnd() {
        Log.d(TAG, "animationEnd: ");
        binding.rlRepresentativeAction.setClickable(true);
    }
}
