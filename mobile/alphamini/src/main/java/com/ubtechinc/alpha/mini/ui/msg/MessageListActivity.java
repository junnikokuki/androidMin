package com.ubtechinc.alpha.mini.ui.msg;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.common.ListDecoration;
import com.ubtechinc.alpha.mini.entity.Message;
import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.entity.observable.MessageLive;
import com.ubtechinc.alpha.mini.event.MsgEvent;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.ui.msg.adapter.ShareMsgAdapter;
import com.ubtechinc.alpha.mini.viewmodel.MessageViewModel;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MessageListActivity extends BaseToolbarActivity {

    public static final String TYPE_MSG = "TYPE_MSG";

    MessageViewModel messageViewModel;
    private int page = 1;

    private RecyclerView msgListView;
    private SmartRefreshLayout refreshLayout;

    private ShareMsgAdapter adapter;

    private List<Message> messages = new ArrayList<>();

    private View noDataLay;
    private TextView noDataText;
    private ImageView noDataIcon;

    private int msgType;

    private MaterialDialog readAllDailog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_message);
        msgType = getIntent().getIntExtra(TYPE_MSG, Message.TYPE_SHARE);
        messageViewModel = MessageViewModel.get();
        initView();

    }

    private void initView() {
        View toolbar = findViewById(R.id.toolbar);
        String title = getString(R.string.robot_share);
        String noDataString = getString(R.string.no_shrea_message);
        int notDataIcon = R.drawable.ic_default_share;
        if (msgType == Message.TYPE_SYS) {
            title = getString(R.string.system_msg);
            noDataString = getString(R.string.not_message);
            notDataIcon = R.drawable.ic_msg_default;
        }
        initToolbar(toolbar, title, View.VISIBLE, true);
        actionBtn.setImageResource(R.drawable.read_all_button_selector);
        refreshLayout = findViewById(R.id.refreshLayout);
        noDataLay = findViewById(R.id.msg_not_data_lay);
        noDataIcon = findViewById(R.id.iv_share_icon);
        noDataText = findViewById(R.id.msg_not_data_txt);
        msgListView = findViewById(R.id.msg_share_list);
        msgListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ShareMsgAdapter(this, messages);
        msgListView.setAdapter(adapter);
        msgListView.addItemDecoration(new ListDecoration(2*getResources().getDimensionPixelOffset(R.dimen.ten_dp),true));
        noDataIcon.setImageResource(notDataIcon);
        noDataText.setText(noDataString);
        adapter.setOnItemClickListener(new ShareMsgAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Message msg) {
                if (msg != null) {
                    if (Integer.valueOf(msg.getCommandId()) == IMCmdId.IM_ACCOUNT_PERMISSION_REQUEST_RESPONSE) {
                        PageRouter.toSharePermissionUpdate(MessageListActivity.this, msg.getFromId(), null, null);
                    } else {
                        PageRouter.toMessageDetail(MessageListActivity.this, msg.getNoticeId(), msg, MessageDetailActivity.TYPE_DETAIL);
                    }
                }
            }
        });
        refreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshLayout) {
                loadMessage(page + 1);
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                loadMessage(1);
            }
        });
        refreshLayout.autoRefresh();
    }

    public void loadMessage(final int page) {
        final LiveResult<List<Message>> messagesResult = getMsgs(page);
        messagesResult.observe(this, new Observer<LiveResult>() {
            @Override
            public void onChanged(@Nullable LiveResult liveResult) {
                Log.d(TAG, "loadMessage state = " + liveResult.getState());
                switch (liveResult.getState()) {
                    case LOADING:
                        List<Message> msgses = (List<Message>) liveResult.getData();
                        if (!CollectionUtils.isEmpty(msgses)) {
                            messages.clear();
                            messages.addAll(msgses);
                            adapter.updateList(messages);
                        }
                        break;
                    case SUCCESS:
                        if (page == 1) {
                            refreshLayout.finishRefresh();
                            List<Message> msgs = (List<Message>) liveResult.getData();
                            if (CollectionUtils.isEmpty(msgs)) {
                                noDataLay.setVisibility(View.VISIBLE);
                                actionBtn.setVisibility(View.GONE);
                                refreshLayout.setVisibility(View.GONE);
                            } else {
                                messages = msgs;
                                adapter.updateList(messages);
                            }
                            MessageListActivity.this.page = page;
                        } else {
                            refreshLayout.finishLoadmore(100);
                            List<Message> msgs = (List<Message>) liveResult.getData();
                            if (!CollectionUtils.isEmpty(msgs)) {
                                MessageListActivity.this.page = page;
                                messages.removeAll(msgs);
                                messages.addAll(msgs);
                                adapter.updateList(messages);
                            }
                        }
                        messagesResult.removeObserver(this);
                        break;
                    case FAIL:
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        toast(R.string.op_fail);
                        noDataText.setText(R.string.op_fail);
                        noDataLay.setVisibility(View.VISIBLE);
                        actionBtn.setVisibility(View.GONE);
                        refreshLayout.setVisibility(View.GONE);
                        messagesResult.removeObserver(this);
                        break;
                }
            }
        });
    }

    private LiveResult getMsgs(int page) {
        LiveResult liveResult = null;
        switch (msgType) {
            case Message.TYPE_SHARE:
                liveResult = messageViewModel.getShareMessage(page);
                break;
            case Message.TYPE_SYS:
                liveResult = messageViewModel.getSystemMessage(page);
                break;
            default:
                break;
        }
        return liveResult;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            Message msg = (Message) data.getSerializableExtra(MessageDetailActivity.MESSAGE);
            adapter.updateMessage(msg);
        }
    }

    @Override
    protected void onAction(View view) {
        showReadAllDialog();
    }

    private void showReadAllDialog() {
        if (readAllDailog == null) {
            readAllDailog = new MaterialDialog(this);
            readAllDailog.setMessage(R.string.read_all_msg);
            readAllDailog.setPositiveButton(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    readAllDailog.dismiss();
                    readAllMsg();
                }
            });
            readAllDailog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    readAllDailog.dismiss();
                }
            });
        }
        readAllDailog.show();
    }

    public void readAllMsg() {
        showLoadingDialog();
        messageViewModel.readAllMessage(AuthLive.getInstance().getUserId(), msgType).observe(this, new Observer<LiveResult>() {
            @Override
            public void onChanged(@Nullable LiveResult liveResult) {
                switch (liveResult.getState()) {
                    case SUCCESS:
                        dismissDialog();
                        toastSuccess(getString(R.string.op_success));
                        for (Message message : messages) {
                            message.setIsRead(1);
                        }
                        adapter.updateList(messages);
                        if (msgType == Message.TYPE_SHARE) {
                            MessageLive.get().clearShareCount();
                        }else{
                            MessageLive.get().clearSysCount();
                        }
                        EventBus.getDefault().post(new MsgEvent(MessageLive.get().getMsgCount(), false));
                        break;
                    case FAIL:
                        dismissDialog();
                        toastError(getString(R.string.op_fail));
                        break;
                }
            }
        });
    }
}
