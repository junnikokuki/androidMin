package com.ubtechinc.alpha.mini.viewmodel.unbind;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ubtechinc.alpha.mini.common.BaseActivity;
import com.ubtechinc.alpha.mini.entity.observable.RobotUnbindLive;
import com.ubtechinc.alpha.mini.viewmodel.RobotAllAccountViewModel;


/**
 * @Date: 2017/11/15.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 解绑的用户需要知道解绑是否成功,需要回调函数
 * 1. 主账号解绑自己
 *    1.1 没有从账号
 *    1.2 有从账号
 * 2. 从账号解绑自己
 * 3. 主账号解绑从账号
 *
 * 1.解绑自己---> 先请求接口获取到解绑视图,三种方式
 *                  1.1没有从账号
 *                  1.2有从账号
 *                  1.3从账号解绑自己
 * 2.解绑从账号---> 直接解绑
 */

public class RobotUnbindBussiness {

    RobotAllAccountViewModel viewModel;

    private IOnUnbinbWaysListener listener;


    public RobotUnbindBussiness(){
        viewModel = new RobotAllAccountViewModel();
    }

    public void setUnbindWaysListener(IOnUnbinbWaysListener listener){
        this.listener = listener;
    }

    public void getRobotUnbindWays(BaseActivity activity,String UserId){
        viewModel.getRobotUnBindWays(UserId).observe(activity, new Observer<RobotUnbindLive>() {
            @Override
            public void onChanged(@Nullable RobotUnbindLive robotUnbindLive) {
                if (listener != null)
                    listener.onGetUnbindWays(robotUnbindLive);
            }
        });
    }


    public void showUnbindDialog(UnbindDialog dialog){
        dialog.setContent();
        dialog.show();
    }


    public interface IOnUnbinbWaysListener{
        void onGetUnbindWays(RobotUnbindLive robotUnbindLive);
    }
}
