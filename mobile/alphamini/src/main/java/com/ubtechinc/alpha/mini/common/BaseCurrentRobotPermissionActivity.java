package com.ubtechinc.alpha.mini.common;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ubtech.utilcode.utils.CollectionUtils;
import com.ubtechinc.alpha.mini.entity.RobotPermission;
import com.ubtechinc.alpha.mini.entity.RobotInfo;

import java.util.List;

public abstract class BaseCurrentRobotPermissionActivity extends BaseCurrentRobotActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void onCurrentChange(RobotInfo robotInfo) {
        List<RobotPermission> robotPermissions = robotInfo.getRobotPermissionList();
        List<String> needPermissionCodeList = needPermissionCodes();
        if (!CollectionUtils.isEmpty(needPermissionCodeList)) {
            if (CollectionUtils.isEmpty(robotPermissions)) {
                finish();
            } else {
                boolean needFinish = false;
                for (String s : needPermissionCodeList) {
                    for (RobotPermission robotPermission : robotPermissions) {
                        if (robotPermission.getPermissionCode().equals(s)) {
                            if (robotPermission.getStatus() == 0) {
                                needFinish = true;
                                break;
                            }
                        }
                    }
                    if (needFinish) {
                        break;
                    }
                }
                if (needFinish) {
                    finish();
                }
            }
        }
    }

    protected abstract List<String> needPermissionCodes();


}
