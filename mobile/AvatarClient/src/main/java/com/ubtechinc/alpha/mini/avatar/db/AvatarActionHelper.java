package com.ubtechinc.alpha.mini.avatar.db;

import android.content.Context;
import android.text.TextUtils;

import com.ubtechinc.alpha.mini.avatar.R;
import com.ubtechinc.alpha.mini.avatar.common.PreferencesManager;
import com.ubtechinc.alpha.mini.avatar.widget.AvatarActionModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ubt on 2017/8/29.
 */

public class AvatarActionHelper {

    private volatile static AvatarActionHelper instance;

    public static AvatarActionHelper getInstance() {
        if (instance == null) {
            synchronized (AvatarActionHelper.class) {
                if (instance == null) {
                    instance = new AvatarActionHelper();
                }
            }
        }
        return instance;
    }

    private AvatarActionHelper() {

    }

    /**
     * 获取本地数据
     *
     * @return
     */
    public List<AvatarActionModel> getAvatarActionModelList() {
        ArrayList<AvatarActionModel> list = new ArrayList<>();

        AvatarActionModel actionModel1 = new AvatarActionModel();
        actionModel1.setActionNameEN("Surveillance_0001");
        actionModel1.setActionNameAlias("打招呼");
        actionModel1.setActionNameCN("打招呼");
        actionModel1.setLightImageIconId(R.drawable.mini_sayhello);
        actionModel1.setShadowImageIconId(R.drawable.mini_sayhello);
        actionModel1.setActionDuration(4800);

        AvatarActionModel actionModel2 = new AvatarActionModel();
        actionModel2.setActionNameEN("Surveillance_0004");
        actionModel2.setActionNameAlias("飞吻");
        actionModel2.setActionNameCN("飞吻");
        actionModel2.setLightImageIconId(R.drawable.mini_naughty);
        actionModel2.setShadowImageIconId(R.drawable.mini_naughty);
        actionModel2.setActionDuration(6200);

        AvatarActionModel actionModel3 = new AvatarActionModel();
        actionModel3.setActionNameEN("Surveillance_0002");
        actionModel3.setActionNameAlias("拥抱");
        actionModel3.setActionNameCN("拥抱");

        actionModel3.setLightImageIconId(R.drawable.mini_hug);
        actionModel3.setShadowImageIconId(R.drawable.mini_hug);
        actionModel3.setActionDuration(4000);


        AvatarActionModel actionModel4 = new AvatarActionModel();
        actionModel4.setActionNameEN("Surveillance_0003");
        actionModel4.setActionNameAlias("握手");
        actionModel4.setActionNameCN("握手");
        actionModel4.setLightImageIconId(R.drawable.mini_shake_hand);
        actionModel4.setShadowImageIconId(R.drawable.mini_shake_hand);
        actionModel4.setActionDuration(6500);


        list.add(actionModel1);
        list.add(actionModel2);
        list.add(actionModel3);
        list.add(actionModel4);

        return list;
    }


    public List<AvatarActionModel> getAllAvatarActionModelList() {
        List<AvatarActionModel> actions = new ArrayList<>();
        AvatarActionModel actionModel5 = new AvatarActionModel();
        actionModel5.setActionNameEN("Surveillance_0005");
        actionModel5.setActionNameAlias("鞠躬");
        actionModel5.setActionNameCN("鞠躬");
        actionModel5.setActionId("1464835936089");
        actionModel5.setLightImageUrl("https://video.ubtrobot.com/avatar/action/be_happy.png");
        actionModel5.setShadowImageUrl("https://video.ubtrobot.com/avatar/action/be_happy1.png");
        actionModel5.setLightImageIconId(R.drawable.mini_bow);
        actionModel5.setShadowImageIconId(R.drawable.mini_bow);
        actionModel5.setActionDuration(6200);

        AvatarActionModel actionModel6 = new AvatarActionModel();
        actionModel6.setActionNameEN("Surveillance_0006");
        actionModel6.setActionNameAlias("卖萌");
        actionModel6.setActionNameCN("卖萌");
        actionModel6.setActionId("1496222140317");
        actionModel6.setLightImageUrl("https://video.ubtrobot.com/avatar/action/be_happy.png");
        actionModel6.setShadowImageUrl("https://video.ubtrobot.com/avatar/action/be_happy1.png");
        actionModel6.setLightImageIconId(R.drawable.mini_sell_moe);
        actionModel6.setShadowImageIconId(R.drawable.mini_sell_moe);
        actionModel6.setActionDuration(6200);

        actions.add(actionModel5);
        actions.add(actionModel6);
        return actions;
    }


    public List<AvatarActionModel> getFavActionModeList(Context context, String userId) {
        String actionIds = PreferencesManager.getInstance(context).get(userId + "actions", "");
        if (TextUtils.isEmpty(actionIds)) {
            return getAvatarActionModelList();
        }
        List<AvatarActionModel> list = getAllAvatarActionModelList();
        List<AvatarActionModel> result = new ArrayList<>(4);
        Map<String, AvatarActionModel> map = listToHashMap(list);
        for (String s : actionIds.split(",")) {
            result.add(map.get(s));
        }
        return result;
    }

    /**
     * 将AvatarActionModel转换成ActionModel
     *
     * @param actionModels
     * @return
     */
    public void saveActionData(Context context, String userId, List<AvatarActionModel> actionModels) {
        StringBuilder actionIdsBuilder = new StringBuilder();
        for (AvatarActionModel actionModel : actionModels) {
            actionIdsBuilder.append(actionModel.getActionId());
            actionIdsBuilder.append(",");
        }
        PreferencesManager.getInstance(context).put(userId + "actions", actionIdsBuilder.toString());
    }

    /**
     * 是否需要更新数据
     *
     * @param list
     * @param map
     * @return
     */
    public boolean isNeedRequestUpdate(List<AvatarActionModel> list, Map<Integer, AvatarActionModel> map) {
        boolean isNeedRequest = false;

        for (int i = 0; i < 4; i++) {
            if (!list.get(i).getActionId().equals(map.get(i).getActionId())) {
                isNeedRequest = true;
                break;
            }
        }
        return isNeedRequest;
    }


    /**
     * 将HashMap转为list
     *
     * @param map
     * @return
     */
    public List<AvatarActionModel> hashMapToList(Map<Integer, AvatarActionModel> map) {
        ArrayList<AvatarActionModel> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            list.add(map.get(i));
        }

        return list;
    }


    public Map<String, AvatarActionModel> listToHashMap(List<AvatarActionModel> list) {
        Map<String, AvatarActionModel> map = new HashMap<>(6);
        for (AvatarActionModel avatarActionModel : list) {
            map.put(avatarActionModel.getActionId(), avatarActionModel);
        }
        return map;
    }
}
