package com.ubtechinc.alpha.mini.im;

import android.text.TextUtils;

import com.ubtrobot.common.utils.XLog;
import com.ubtrobot.lib.sync.SyncContext;
import com.ubtrobot.lib.sync.SyncFactory;
import com.ubtrobot.lib.sync.db.client.SyncModifyClient;
import com.ubtrobot.lib.sync.db.client.SyncRobotClient;
import com.ubtrobot.lib.sync.db.model.FileModel;
import com.ubtrobot.lib.sync.db.model.ModifyModel;
import com.ubtrobot.lib.sync.db.model.RobotFileModel;
import com.ubtrobot.param.sync.Consts;
import com.ubtrobot.param.sync.Files;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/6.
 */

public class PushImUtils {

    public static void dealResult(List<Files.ModifyData> resultList) {
        if (resultList != null && resultList.size() > 0) {
            XLog.d("sync", " resultList size: %d", resultList.size());
            SyncRobotClient robotClient = (SyncRobotClient) SyncFactory.getClient(SyncContext.CONTEXT_ROBOT);
            SyncModifyClient modifyClient = (SyncModifyClient) SyncFactory.getClient(SyncContext.CONTEXT_MODIFY);
            for (Files.ModifyData operate : resultList) {
                /*if (operate.getCode() == 1) {
                    Files.ModifyData operateItem = operate.getOperate();

                }*/
                ModifyModel modify = ModifyModel.read(operate);
//                    XLog.d("sync", "modifyModel: %s", modify.toString());
                if (Consts.FILE_ACTION_DELETE.equals(modify.getAction())) {
                    RobotFileModel fileModel = robotClient.getFileModel(modify.getPath());
                    if (fileModel != null) {
                        robotClient.remove(fileModel);
                    }
                    modifyClient.remove(modify);
                    XLog.d("sync", "remove success ");
                }
                modifyClient.remove(modify);
            }
        }
    }
}
