package com.ubtechinc.alpha.mini.repository.datasource;

/**
 * 账号申请及处理类
 */

public interface IAccountApplyDataSource {

    /**
     * 从账号申请接口
     *
     * @param robotUserId
     * @param callback
     */
    public void applyRobot(String robotUserId, AccountApplyCallback callback);

    /**
     * 主账号同意申请
     *
     * @param noticeId
     * @param callback
     */
    public void applyApprove(String noticeId, String permission, AccountApplyCallback callback);

    /**
     * 主账号拒绝申请
     *
     * @param noticeId
     * @param callback
     */
    public void applyReject(String noticeId, AccountApplyCallback callback);

    public interface AccountApplyCallback {

        public void onSuccess();

        public void onError(int code, String msg);
    }

}
