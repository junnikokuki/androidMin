package com.ubtechinc.alpha.mini.ui.share;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.ubtech.utilcode.utils.KeyboardUtils;
import com.ubtech.utilcode.utils.network.NetworkHelper;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.entity.Message;
import com.ubtechinc.alpha.mini.entity.RobotInfo;
import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.net.CheckBindRobotModule;
import com.ubtechinc.alpha.mini.repository.RobotAuthorizeReponsitory;
import com.ubtechinc.alpha.mini.ui.network.NetworkLoadStatusView;
import com.ubtechinc.alpha.mini.ui.title.TitleActivity;
import com.ubtechinc.alpha.mini.ui.utils.Utils;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.ThrowableWrapper;

/**
 * @desc : 分享列表Activity
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2017/11/1
 */
public class ShareListActivity extends TitleActivity {

    private NetworkLoadStatusView networkLoadStatusView;
    private ShareSlaverListView mShareSlaverView;
    private ShareMasterListView mShareMasterView;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_share_list;
    }

    @Override
    protected void onInitView() {
        networkLoadStatusView.setReload(new NetworkLoadStatusView.IReload() {
            @Override
            public void reload() {
                if (Utils.isNetworkAvailable(ShareListActivity.this)) {
                    toLoad();
                } else {
                    Toast.makeText(ShareListActivity.this, getString(R.string.net_work_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkHelper.sharedHelper().isNetworkAvailable()) {
            // TODO 获取机器人ID
            toLoad();
        } else {
            networkLoadStatusView.toNetworkDisconnect();
        }
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                Log.d(TAG,"inputMethodManager.isActive()" + inputMethodManager.isActive());
                if (inputMethodManager.isActive()) {
                    //因为是在fragment下，所以用了getView()获取view，也可以用findViewById（）来获取父控件
                    getWindow().getDecorView().requestFocus();//使其它view获取焦点.这里因为是在fragment下,所以便用了getView(),可以指定任意其它view
                    inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                }
            }
        }, 200);
    }

    private void toLoad() {
        RobotAuthorizeReponsitory.getInstance().getRobotBindUser(MyRobotsLive.getInstance().getRobotUserId(),
                new ResponseListener<CheckBindRobotModule.Response>() {
                    @Override
                    public void onError(ThrowableWrapper e) {
                        Log.d("1102", " onFail -- e :" + Log.getStackTraceString(e));
                        networkLoadStatusView.toNetworkLoadFail();
                    }

                    @Override
                    public void onSuccess(CheckBindRobotModule.Response response) {
                        boolean isMaster = false;

                        String userId = AuthLive.getInstance().getUserId();
                        if (!TextUtils.isEmpty(userId)) {
                            RobotInfo robotInfo = MyRobotsLive.getInstance().getCurrentRobot().getValue();
                            if (robotInfo != null) {
                                isMaster = userId.equals(robotInfo.getMasterUserId());
                            }
                        }

                        if (isMaster) {
                            if (mShareMasterView == null) {
                                mShareMasterView = new ShareMasterListView(ShareListActivity.this);
                                networkLoadStatusView.setViewContent(mShareMasterView);
                            }
                            mShareMasterView.setSlaverUserList(response.getData().getResult());
                        } else {
                            if (mShareSlaverView == null) {
                                mShareSlaverView = new ShareSlaverListView(ShareListActivity.this);
                                networkLoadStatusView.setViewContent(mShareSlaverView);
                            }
                            mShareSlaverView.setResultBeen(response.getData().getResult());
                        }
                        networkLoadStatusView.toShowContent();
                    }
                });
    }

    @Override
    protected void onFindView() {
        networkLoadStatusView = findViewById(R.id.network_loadstatus);
        Log.d("1102", " onFindView networkLoadStatusView : " + networkLoadStatusView);
    }


    @Override
    protected void receiveMsg(Message message) {
        super.receiveMsg(message);
        if(message != null){
            String commandId = message.getCommandId();
            if (commandId != null) {
                int command = Integer.valueOf(commandId);
                if (IMCmdId.IM_ACCOUNT_INVITATION_ACCEPTED_RESPONSE == command) {
                    toLoad();
                }
            }
        }
    }
}
