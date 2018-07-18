package com.ubtechinc.alpha.mini.ui.friend;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.footer.LoadingView;
import com.ubtechinc.alpha.CmFaceCheckState;
import com.ubtechinc.alpha.CmFaceDelete;
import com.ubtechinc.alpha.CmFaceList;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.databinding.ActivityFriendBinding;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.event.FriendListChangeEvent;
import com.ubtechinc.alpha.mini.im.Phone2RobotMsgMgr;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.ui.adapter.MiniFriendAdapter;
import com.ubtechinc.alpha.mini.widget.LoadingDialog;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.alpha.mini.widget.RefreshView;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.phonerobotcommunite.ICallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Set;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * @Date: 2017/12/15.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 机器人好友页面, 显示机器人存储的人脸信息
 * 有三种状态
 * 1.没有机器人好友
 * 2.有机器人好友, 普通显示状态
 * 3.有机器人好友，编辑状态
 */

public class AlphaMiniFriendActivity extends BaseToolbarActivity implements MiniFriendAdapter.IOnItemSelectedListener {

    public static final int MINI_FREIDN_ACITIVY_NO_DATA_MODE = 0;
    public static final int MINI_FREIDN_ACITIVY_EDIT_MODE    = 1;
    public static final int MINI_FREIDN_ACITIVY_SIMPLE_MODE  = 2;

    public static final int RESULT_DELETE_FACEINFO = 1002;

    private static final int PAGE_SIZE = 9;
    private static final int FIRST_PAGE = 1;
    private int currentPage = 1;

    private ActivityFriendBinding binding;

    private MiniFriendAdapter adapter;

    /**处于编辑状态，是否选中所有**/
    private boolean isSelectAll = false;

    @MiniFriendActivityMode
    private int currentActivityState = MINI_FREIDN_ACITIVY_SIMPLE_MODE;


    @IntDef({MINI_FREIDN_ACITIVY_NO_DATA_MODE, MINI_FREIDN_ACITIVY_EDIT_MODE, MINI_FREIDN_ACITIVY_SIMPLE_MODE})
    @Target({PARAMETER, FIELD})//作用目标
    @Retention(RetentionPolicy.SOURCE)//作用域
    public @interface MiniFriendActivityMode {}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_friend);
        binding.setResponse(new EventResponse());
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        initToolbar(binding.toolbar, getString(R.string.robot_friend), View.VISIBLE , true);
        initConnectFailedLayout(R.string.face_detect_mobile_offline_hint, R.string.face_detect_robot_offline_hint);
        adapter = new MiniFriendAdapter(this);
        adapter.addSelectedAllListener(this);
        binding.msgShareList.setLayoutManager(new GridLayoutManager(this, 3));
        binding.msgShareList.setAdapter(adapter);

        RefreshView headerView = new RefreshView(this);
        headerView.setTextColor(getResources().getColor(R.color.alexa_text_color_black));
        headerView.setRefreshingStr(getString(R.string.robot_friend_loading));
        binding.refreshLayout.setHeaderView(headerView);
        LoadingView loadingView = new LoadingView(this);
        binding.refreshLayout.setBottomView(loadingView);
        binding.refreshLayout.setAutoLoadMore(true);
        binding.refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                refreshConnectFailedLayout();
                loadFriendFaceInfoList(1);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                loadFriendFaceInfoList(currentPage + 1);
            }
        });
        binding.refreshLayout.startRefresh();
        switchActivityState(currentActivityState);
    }

    private void switchActivityState(@MiniFriendActivityMode int mode) {
        currentActivityState = mode;
        adapter.setActivityCurrentMode(currentActivityState);
        switch (mode){
            case MINI_FREIDN_ACITIVY_NO_DATA_MODE:
                setActivityNoDataLayout();
                break;
            case MINI_FREIDN_ACITIVY_SIMPLE_MODE:
                clearEditStateData();
                setActivitySimpleLayout();
                break;
            case MINI_FREIDN_ACITIVY_EDIT_MODE:
                setActivityEditMode();
                break;
        }
    }

    /**
     * 清除编辑时的选中的人脸信息的状态
     */
    private void clearEditStateData() {
        isSelectAll = false;
        binding.setIsSelectedAll(isSelectAll);
        adapter.selectAllFaceInfo(false);
        adapter.clearSelectedData();
    }


    private void setActivityNoDataLayout() {
        actionBtn.setVisibility(View.GONE);
        binding.setHasfriend(false);
    }

    private void setActivityEditMode() {
        actionBtn.setVisibility(View.VISIBLE);
        actionBtn.setImageResource(R.drawable.close_btn_selector);
        binding.setHasfriend(true);
        binding.setEditing(true);
    }

    private void setActivitySimpleLayout() {
        actionBtn.setVisibility(View.VISIBLE);
        actionBtn.setImageResource(R.drawable.friend_edit_btn);
        binding.setHasfriend(true);
        binding.setEditing(false);
    }

    // TODO: 2017/12/23 当前这块代码属于业务逻辑部分，等待移出去
    private void loadFriendFaceInfoList(final int page) {
        CmFaceList.CmFaceListRequest.Builder request =  CmFaceList.CmFaceListRequest.newBuilder();
        request.setPage(page).setPageSize(PAGE_SIZE);
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_FACE_LIST_REQUEST, IMCmdId.IM_VERSION, request.build(),
                MyRobotsLive.getInstance().getRobotUserId(), new ICallback<CmFaceList.CmFaceListResponse>(){

                    @Override
                    public void onSuccess(final CmFaceList.CmFaceListResponse data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toastSuccess(getString(R.string.sync_friend_success));
                                List<FriendFaceInfo> faceInfos = FriendFaceInfo.transFormCmFaceListToFriendList(data.getFaceListList());

                                if (page != FIRST_PAGE){
                                    binding.refreshLayout.finishLoadmore();
                                    switchActivityState(MINI_FREIDN_ACITIVY_SIMPLE_MODE);
                                    adapter.addFaceInfos(faceInfos);
                                    return;
                                }

                                if (faceInfos != null && faceInfos.size() != 0){
                                    switchActivityState(MINI_FREIDN_ACITIVY_SIMPLE_MODE);
                                    adapter.refreshFaceInfos(faceInfos);
                                }else{
                                    switchActivityState(MINI_FREIDN_ACITIVY_NO_DATA_MODE);
                                }
                                binding.refreshLayout.finishRefreshing();

                            }
                        });
                    }

                    @Override
                    public void onError(final ThrowableWrapper e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toastError(getString(R.string.sync_friend_fail));
                                if (page == FIRST_PAGE) {
                                    binding.refreshLayout.finishRefreshing();
                                } else {
                                    binding.refreshLayout.finishLoadmore();
                                }
                                e.printStackTrace();
                            }
                        });
                    }
                });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FriendListChangeEvent event){
        loadFriendFaceInfoList(FIRST_PAGE);
    }


    @Override
    protected void onAction(View view) {
        if (currentActivityState == MINI_FREIDN_ACITIVY_EDIT_MODE){
            switchActivityState(MINI_FREIDN_ACITIVY_SIMPLE_MODE);
        }else{
            switchActivityState(MINI_FREIDN_ACITIVY_EDIT_MODE);
        }
    }

    @Override
    protected void setConnectFailedLayout(boolean robotConnectState, boolean networkConnectState) {
        setAddFriendFaceEnable(robotConnectState, networkConnectState);
        super.setConnectFailedLayout(robotConnectState, networkConnectState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            String friendName = getIntent().getStringExtra(PageRouter.FRIEND_NAME);
            adapter.notifyItemNameChanged(adapter.getIndexOf2MiniInfo(), friendName);
            adapter.resetIndexOf2MiniInfo();
        }else if(resultCode == RESULT_DELETE_FACEINFO){
            binding.refreshLayout.startRefresh();
        }
    }

    private void setAddFriendFaceEnable(boolean robotConnectState, boolean networkConnectState) {
        binding.setRobotenable(robotConnectState);//im连接
        binding.setNetworkenable(networkConnectState);
        if(!robotConnectState || !networkConnectState){
            actionBtn.setClickable(false);
            actionBtn.setAlpha(0.3f);
        }else{
            actionBtn.setClickable(true);
            actionBtn.setAlpha(1f);
        }
    }


    /**
     * 刷新顶部连接失败的红色提示layout
     */
    private void refreshConnectFailedLayout() {
        setColsedConnectLayout(false);
        if (!getNetworkConnectState()){
            connectFailed.setVisibility(View.VISIBLE);
            tvConnectedText.setText(R.string.face_detect_mobile_offline_hint);
            return;
        }
        if (!getRobotConnect()){
            connectFailed.setVisibility(View.VISIBLE);
            tvConnectedText.setText(R.string.face_detect_robot_offline_hint);
            return;
        }
    }

    @Override
    public void checkAllSelected(boolean isSelected) {
        isSelectAll = isSelected;
        binding.setIsSelectedAll(isSelectAll);
    }

    @Override
    public void checkSelectedSize(int size) {
        if (size > 0){
            binding.setDelteEnable(true);
        }else{
            binding.setDelteEnable(false);
        }
    }

    public void toPrivacy(View view){
        PageRouter.toConmunicationActivity(AlphaMiniFriendActivity.this);
    }


    private void deleteFriendFace(Set<String> faceInfos){
        CmFaceDelete.CmFaceDeleteRequest.Builder request = CmFaceDelete.CmFaceDeleteRequest.newBuilder();
        request.addAllIds(faceInfos);
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_FACE_DELETE_REQUEST, IMCmdId.IM_VERSION, request.build(),
                MyRobotsLive.getInstance().getRobotUserId(), new ICallback<CmFaceDelete.CmFaceDeleteResponse>(){

                    @Override
                    public void onSuccess(final CmFaceDelete.CmFaceDeleteResponse data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i(TAG, "deleteFriendFace getResultCode: " + data.getResultCode());
                                if (data.getResultCode() == 0){
                                    loadFriendFaceInfoList(FIRST_PAGE);
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(ThrowableWrapper e) {
                        e.printStackTrace();
                    }
                });
    }


    /**防止重复点击**/
    private boolean isCheckingRobot;
    private void checkRobotFriendFaceState(){
        if (isCheckingRobot){
            Log.e(TAG, "it is Checking Robot! Do not press Add Friend Button again until it finish checking" );
            return;
        }
        isCheckingRobot = true;
        LoadingDialog.getInstance(AlphaMiniFriendActivity.this).show();
        CmFaceCheckState.CmFaceCheckStateRequest.Builder request = CmFaceCheckState.CmFaceCheckStateRequest.newBuilder();
        Phone2RobotMsgMgr.getInstance().sendDataToRobot(IMCmdId.IM_FACE_CHECK_STATE_REQUEST, IMCmdId.IM_VERSION, request.build(),
                MyRobotsLive.getInstance().getRobotUserId(), new ICallback<CmFaceCheckState.CmFaceCheckStateResponse>(){

                    @Override
                    public void onSuccess(final CmFaceCheckState.CmFaceCheckStateResponse response) {
                        isCheckingRobot = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LoadingDialog.getInstance(AlphaMiniFriendActivity.this).dismiss();
                                Log.i(TAG, "checkState result code : " + response.getResultCode() + " result msg : " + response.getErrorMsg());
                                if (response.getResultCode() == 0){
                                    PageRouter.toModifyMiniNameActivity(AlphaMiniFriendActivity.this, null);
                                }else {
                                    showDialogHint(response.getErrorMsg());
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(ThrowableWrapper e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LoadingDialog.getInstance(AlphaMiniFriendActivity.this).dismiss();
                            }
                        });
                        isCheckingRobot = false;
                        e.printStackTrace();
                    }
                });
    }





    private void showDialogHint(String msg){
        final MaterialDialog dialog = new MaterialDialog(AlphaMiniFriendActivity.this);
        dialog.setMessage(msg).setPositiveButton(R.string.i_know, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        }).show();
    }

    /**
     * 响应点击事件的
     */
    public class EventResponse{
        public void addNewFriend(View view){
            checkRobotFriendFaceState();
        }

        public void deleteSelectedFrind(View view){
            // TODO: 2017/12/23 当前这块代码属于业务逻辑部分，等待移出去
            final MaterialDialog deleteFriendDialog = new MaterialDialog(AlphaMiniFriendActivity.this);
            final Set<String> faceInfos = adapter.getSelecteFaceInfoIds();
            int size = faceInfos.size();
            if (size > 1){
                deleteFriendDialog.setMessage(getString(R.string.friend_info_delete_mutil_friends, size));
            }else{
                deleteFriendDialog.setMessage(R.string.friend_info_delete_one_friend);
            }
            deleteFriendDialog.setTitle(R.string.friend_info_delete_friend).setPositiveButton(R.string.common_button_delete, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFriendFace(faceInfos);
                    deleteFriendDialog.dismiss();
                }
            }).setNegativeButton(R.string.common_btn_cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFriendDialog.dismiss();
                }
            }).show();
        }

        public void selectAllFriends(View view){
            isSelectAll = !isSelectAll;
            binding.setIsSelectedAll(isSelectAll);
            adapter.selectAllFaceInfo(isSelectAll);
        }

    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
