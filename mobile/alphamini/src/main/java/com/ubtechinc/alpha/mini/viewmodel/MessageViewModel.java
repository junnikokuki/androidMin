package com.ubtechinc.alpha.mini.viewmodel;

import android.util.Log;

import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtech.utilcode.utils.SPUtils;
import com.ubtechinc.alpha.im.IMCmdId;
import com.ubtechinc.alpha.mini.constants.CodeMaoConstant;
import com.ubtechinc.alpha.mini.constants.Constants;
import com.ubtechinc.alpha.mini.entity.Message;
import com.ubtechinc.alpha.mini.entity.RobotPermission;
import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.entity.observable.LiveResult;
import com.ubtechinc.alpha.mini.entity.observable.MessageLive;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.push.AlphaMiniPushManager;
import com.ubtechinc.alpha.mini.push.IMiniMsgCallback;
import com.ubtechinc.alpha.mini.repository.MsgRepository;
import com.ubtechinc.alpha.mini.repository.datasource.IMsgDataSource;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理消息相关的业务逻辑
 */
public class MessageViewModel implements IMiniMsgCallback {

    public static final String TAG = "MessageViewModel";

    private MsgRepository msgRepository;

    public List<Message> shareMessage = new ArrayList<>();
    private List<Message> sysMessage = new ArrayList<>();


    private static class MessageViewModelHolder {
        private static MessageViewModel instance = new MessageViewModel();
    }

    public static MessageViewModel get() {
        return MessageViewModelHolder.instance;
    }

    private MessageViewModel() {
        msgRepository = new MsgRepository();
        AlphaMiniPushManager.getInstance().addMessageListener(this);
    }

    public void getNoReadMsgCount() {
        msgRepository.getNoReadMsgCount(new IMsgDataSource.GetNoReadCountCallback() {
            @Override
            public void onSuccess(int systemCount, int shareCount) {
                MessageLive.get().setCount(systemCount, shareCount);
            }

            @Override
            public void onError() {

            }
        });
    }


    public LiveResult<List<Message>> getShareMessage(final int page) {
        LiveResult<List<Message>> liveResult;
        String userId = AuthLive.getInstance().getUserId();
        if (page == 1) {
            liveResult = refreshShareMessage(userId, page);
        } else {
            liveResult = loadMoreShareMessage(userId, page);
        }
        return liveResult;
    }

    private LiveResult<List<Message>> loadMoreShareMessage(final String userId, final int page) {
        final LiveResult<List<Message>> liveResult = new LiveResult<>();
        msgRepository.getShareMessageFromDB(userId, page, new IMsgDataSource.GetMsgsCallback() {
            @Override
            public void onSuccess(List<Message> sysMsgs, List<Message> shareMsgs) {
                if (CollectionUtils.isEmpty(shareMsgs) || shareMsgs.size() < MsgRepository.DEFAULT_PAGE_SIZE) {
                    msgRepository.getShareMessage(userId, page, new IMsgDataSource.GetMsgsCallback() {
                        @Override
                        public void onSuccess(List<Message> sysMsgs, List<Message> shareMsgs) {
                            liveResult.success(shareMsgs);
                        }

                        @Override
                        public void onError() {
                            liveResult.fail("");
                        }
                    });
                } else {
                    liveResult.success(shareMsgs);
                }
            }

            @Override
            public void onError() {
                msgRepository.getShareMessage(userId, page, new IMsgDataSource.GetMsgsCallback() {
                    @Override
                    public void onSuccess(List<Message> sysMsgs, List<Message> shareMsgs) {
                        liveResult.success(shareMsgs);
                    }

                    @Override
                    public void onError() {
                        liveResult.fail("");
                    }
                });
            }
        });
        return liveResult;
    }

    private LiveResult<List<Message>> refreshShareMessage(String userId, int page) {
        final LiveResult<List<Message>> liveResult = new LiveResult<>();
        msgRepository.getShareMessageFromDB(userId, page, new IMsgDataSource.GetMsgsCallback() {
            @Override
            public void onSuccess(List<Message> sysMsgs, List<Message> shareMsgs) {
                liveResult.loading(shareMsgs);
            }

            @Override
            public void onError() {
                msgRepository.clearAll(Message.TYPE_SHARE);
            }
        });
        msgRepository.getShareMessage(userId, page, new IMsgDataSource.GetMsgsCallback() {
            @Override
            public void onSuccess(List<Message> sysMsgs, final List<Message> shareMsgs) {
                if (!CollectionUtils.isEmpty(shareMsgs)) {
                    Message message = shareMsgs.get(shareMsgs.size() - 1);
                    msgRepository.getMessage(message.getNoticeId(), new IMsgDataSource.GetMsgCallback() {
                        @Override
                        public void onSuccess(Message message) {
                            if (message == null) {
                                msgRepository.clearAll(Message.TYPE_SHARE);
                                msgRepository.saveAll(shareMsgs);
                            } else {
                                msgRepository.saveAll(shareMsgs);
                            }
                        }

                        @Override
                        public void onError() {
                            msgRepository.clearAll(Message.TYPE_SHARE);
                            msgRepository.saveAll(shareMsgs);
                        }
                    });
                }
                liveResult.success(shareMsgs);
            }

            @Override
            public void onError() {
                liveResult.fail("");
            }
        });
        return liveResult;
    }


    public LiveResult<List<Message>> getSystemMessage(int page) {
        LiveResult<List<Message>> liveResult;
        String userId = AuthLive.getInstance().getUserId();
        if (page == 1) {
            liveResult = refreshSysMessage(userId, page);
        } else {
            liveResult = loadMoreSysMessage(userId, page);
        }
        return liveResult;

    }

    private LiveResult<List<Message>> loadMoreSysMessage(final String userId, final int page) {
        final LiveResult<List<Message>> liveResult = new LiveResult<>();
        msgRepository.getSysMessageFromDB(userId, page, new IMsgDataSource.GetMsgsCallback() {
            @Override
            public void onSuccess(List<Message> sysMsgs, List<Message> shareMsgs) {
                if (CollectionUtils.isEmpty(sysMsgs) || sysMsgs.size() < MsgRepository.DEFAULT_PAGE_SIZE) {
                    msgRepository.getSysMessages(userId, page, new IMsgDataSource.GetMsgsCallback() {
                        @Override
                        public void onSuccess(List<Message> sysMsgs, List<Message> shareMsgs) {
                            liveResult.success(sysMsgs);
                        }

                        @Override
                        public void onError() {
                            liveResult.fail("");
                        }
                    });
                } else {
                    liveResult.success(sysMsgs);
                }
            }

            @Override
            public void onError() {
                msgRepository.getSysMessages(userId, page, new IMsgDataSource.GetMsgsCallback() {
                    @Override
                    public void onSuccess(List<Message> sysMsgs, List<Message> shareMsgs) {
                        liveResult.success(sysMsgs);
                    }

                    @Override
                    public void onError() {
                        liveResult.fail("");
                    }
                });
            }
        });
        return liveResult;
    }

    private LiveResult<List<Message>> refreshSysMessage(String userId, int page) {
        final LiveResult<List<Message>> liveResult = new LiveResult<>();
        msgRepository.getSysMessageFromDB(userId, page, new IMsgDataSource.GetMsgsCallback() {
            @Override
            public void onSuccess(List<Message> sysMsgs, List<Message> shareMsgs) {
                liveResult.loading(sysMsgs);
            }

            @Override
            public void onError() {
                msgRepository.clearAll(Message.TYPE_SYS);
            }
        });
        msgRepository.getSysMessages(userId, page, new IMsgDataSource.GetMsgsCallback() {
            @Override
            public void onSuccess(final List<Message> sysMsgs, final List<Message> shareMsgs) {
                if (!CollectionUtils.isEmpty(sysMsgs)) {
                    Message message = sysMsgs.get(sysMsgs.size() - 1);
                    msgRepository.getMessage(message.getNoticeId(), new IMsgDataSource.GetMsgCallback() {
                        @Override
                        public void onSuccess(Message message) {
                            if (message == null) {
                                msgRepository.clearAll(Message.TYPE_SYS);
                                msgRepository.saveAll(sysMsgs);
                            } else {
                                msgRepository.saveAll(sysMsgs);
                            }
                        }

                        @Override
                        public void onError() {
                            msgRepository.clearAll(Message.TYPE_SYS);
                            msgRepository.saveAll(sysMsgs);
                        }
                    });
                }
                liveResult.success(sysMsgs);
            }

            @Override
            public void onError() {
                liveResult.fail("");
            }
        });
        return liveResult;
    }

    public LiveResult<Message> getMessageById(String noticeId) {
        final LiveResult<Message> messageLive = new LiveResult<>();
        msgRepository.getMessage(noticeId, new IMsgDataSource.GetMsgCallback() {
            @Override
            public void onSuccess(Message message) {
                messageLive.success(message);
            }

            @Override
            public void onError() {
                messageLive.fail("");
            }
        });
        return messageLive;
    }

    private void saveShareMessage(Message message) {
        message.getNoticeExtend(); //把权限列表变为string保存数据库
        msgRepository.save(message);
    }

    private void saveSysMessage(Message message) {
        message.getNoticeExtend(); //把权限列表变为string保存数据库
        msgRepository.save(message);
    }

    private void expiredMsg(String userId, String robotId, String commandId) {
        msgRepository.expiredMsg(userId, robotId, commandId);
    }

    public void readMessage(String noticeId) {
        msgRepository.readMessage(noticeId);
    }

    public LiveResult<Void> readAllMessage(String userId, int type) {
        final LiveResult<Void> result = new LiveResult();
        msgRepository.readAllMessage(userId, type, new IMsgDataSource.ReadMsgCallback() {
            @Override
            public void onSuccess() {
                result.success(null);
            }

            @Override
            public void onError() {
                result.fail("");
            }
        });
        return result;
    }

    @Override
    public void onReceiverMsg(String commandId, Message message) {
        Log.i(TAG, "receiveMessage commandId: " + commandId + " message : " + message);
        if (message == null) {
            return;
        }
        message.setCommandId(commandId);
        if (message.getNoticeType() == Message.TYPE_SHARE) {
            saveShareMessage(message);
            MessageLive.get().receiveMessage(commandId, message);
            handleMessage(commandId, message);
        } else if (message.getNoticeType() == Message.TYPE_SYS) {
            saveSysMessage(message);
            MessageLive.get().receiveMessage(commandId, message);
        }
    }

    private void handleMessage(String commandId, Message message) {
        if (commandId != null) {
            int command = Integer.valueOf(commandId);
            switch (command) {
                case IMCmdId.IM_ACCOUNT_BEEN_UNBIND_RESPONSE:
                    MyRobotsLive.getInstance().remove(message.getRobotId());
                    break;
                case IMCmdId.IM_ACCOUNT_HANDLE_APPLY_RESPONSE:
                    if (1 == message.getAgree()) {
                        RobotsViewModel.get().getMyRobots();
                    }
                    SPUtils.get().put(Constants.REQUEST_SHARE_TIME + message.getRobotId() + message.getToId(), 0l);
                    break;
                case IMCmdId.IM_ACCOUNT_MASTER_UNBINDED_RESPONSE:
                    RobotsViewModel.get().getMyRobots();
                    break;
                case IMCmdId.IM_ACCOUNT_PERMISSION_CHANGE_RESPONSE:
                    List<RobotPermission> permissions = message.getPermissionList();
                    Log.d(TAG, "permission = " + permissions);
                    if (!CollectionUtils.isEmpty(permissions)) {
                        MyRobotsLive.getInstance().permissionChange(message.getRobotId(), permissions.get(0));
                    }
                    break;
                case IMCmdId.IM_ACCOUNT_INVITATION_ACCEPTED_RESPONSE:
                    if (message.getFromId() != null && message.getFromId().equals(AuthLive.getInstance().getUserId())) {
                        RobotsViewModel.get().getMyRobots();
                    }
                    break;
            }
        }
    }
}
