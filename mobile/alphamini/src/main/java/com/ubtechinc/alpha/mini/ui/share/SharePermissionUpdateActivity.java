package com.ubtechinc.alpha.mini.ui.share;

import android.text.TextUtils;
import android.view.View;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.constants.Constants;
import com.ubtechinc.alpha.mini.entity.Message;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.net.CheckBindRobotModule;
import com.ubtechinc.alpha.mini.net.QueryPermissionModule;
import com.ubtechinc.alpha.mini.repository.RobotAuthorizeReponsitory;
import com.ubtechinc.alpha.mini.ui.network.NetworkLoadStatusView;
import com.ubtechinc.alpha.mini.ui.title.TitleActivity;
import com.ubtechinc.alpha.mini.ui.utils.Utils;
import com.ubtechinc.alpha.mini.viewmodel.MessageViewModel;
import com.ubtechinc.alpha.mini.viewmodel.unbind.CancelShareDialog;
import com.ubtechinc.alpha.mini.viewmodel.unbind.RobotUnbindBussiness;
import com.ubtechinc.alpha.mini.viewmodel.unbind.UnbindDialog;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.ThrowableWrapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @desc :
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2017/11/3
 */

public class SharePermissionUpdateActivity extends TitleActivity {

    private String userId;
    private String userName;

    private NetworkLoadStatusView networkLoadStatusView;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_share_permission_update;
    }

    @Override
    protected void onFindView() {
        super.onFindView();
        networkLoadStatusView = findViewById(R.id.network_loadstatus);
    }

    @Override
    protected void onInitView() {
        final SharePermissionUpdateView sharePermissionUpdateView = new SharePermissionUpdateView(SharePermissionUpdateActivity.this);
        String content = getIntent().getStringExtra(Constants.RELATIONDATE_KEY);
        userId = getIntent().getStringExtra(Constants.USERID_KEY);
        userName = getIntent().getStringExtra(Constants.USERNAME_KEY);
        final String shareTime = getResources().getString(R.string.accept_time, content);

        if(Utils.isNetworkAvailable(this)) {
            networkLoadStatusView.toLoading();
            //message不为空,取消对应的消息
            if (getIntent().hasExtra(Constants.USER_MESSAGE)) {
                Message message = (Message) getIntent().getExtras().getSerializable(Constants.USER_MESSAGE);
                if (message != null) {
                    MessageViewModel.get().readMessage(message.getNoticeId());
                }

            }
            if (TextUtils.isEmpty(content)) {
                //尝试获取
                //userId = AuthLive.getInstance().getUserId();
                RobotAuthorizeReponsitory.getInstance().getRobotBindUser(MyRobotsLive.getInstance().getRobotUserId(), new ResponseListener<CheckBindRobotModule.Response>() {
                    @Override
                    public void onError(ThrowableWrapper e) {
                        networkLoadStatusView.toNetworkLoadFail();
                    }

                    @Override
                    public void onSuccess(CheckBindRobotModule.Response response) {
                        List<CheckBindRobotModule.User> users = response.getData().getResult();
                        for (CheckBindRobotModule.User user : users) {
                            if (user.getUserId() == Integer.valueOf(userId)) {
                                Long lRelationDate = Long.valueOf(user.getRelationDate());
                                SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                                Date date = new Date(lRelationDate);
                                String content = format.format(date);
                                sharePermissionUpdateView.setTimeText(
                                        getResources().getString(R.string.accept_time, content));
                            }
                        }
                        //networkLoadStatusView.toShowContent();
                        RobotAuthorizeReponsitory.getInstance().queryPermission(userId, MyRobotsLive.getInstance().getRobotUserId(), new ResponseListener<QueryPermissionModule.Response>() {
                            @Override
                            public void onError(ThrowableWrapper e) {
                                networkLoadStatusView.toNetworkLoadFail();
                            }

                            @Override
                            public void onSuccess(QueryPermissionModule.Response response) {
                                //sharePermissionUpdateView.setTimeText(shareTime);
                                sharePermissionUpdateView.setUserId(userId);
                                sharePermissionUpdateView.setResultBeanList(response.getData().getResult());
                                networkLoadStatusView.setViewContent(sharePermissionUpdateView);
                                networkLoadStatusView.toShowContent();
                            }
                        });
                    }
                });
            } else {

                RobotAuthorizeReponsitory.getInstance().queryPermission(userId, MyRobotsLive.getInstance().getRobotUserId(), new ResponseListener<QueryPermissionModule.Response>() {
                    @Override
                    public void onError(ThrowableWrapper e) {
                        networkLoadStatusView.toNetworkLoadFail();
                    }

                    @Override
                    public void onSuccess(QueryPermissionModule.Response response) {
                        sharePermissionUpdateView.setTimeText(shareTime);
                        sharePermissionUpdateView.setUserId(userId);
                        sharePermissionUpdateView.setResultBeanList(response.getData().getResult());
                        networkLoadStatusView.setViewContent(sharePermissionUpdateView);
                        networkLoadStatusView.toShowContent();
                    }
                });
            }

        } else {
            networkLoadStatusView.toNetworkDisconnect();
        }
    }

    public void onCancleShare(View view) {
        RobotUnbindBussiness bussiness = new RobotUnbindBussiness();
        CancelShareDialog dialog = new CancelShareDialog(SharePermissionUpdateActivity.this, userId);
        dialog.addOnUnbindListener(new UnbindDialog.IOnUnbindListener() {
            @Override
            public void onUnbindSuccess() {
                SharePermissionUpdateActivity.this.finish();
            }

            @Override
            public void onUnbindFailed() {

            }
        });
        bussiness.showUnbindDialog(dialog);
    }
}
