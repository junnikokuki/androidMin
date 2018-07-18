package com.ubtechinc.alpha.mini.avatar.db;

import com.ubtechinc.alpha.mini.avatar.entity.AvatarResponse;
import com.ubtechinc.alpha.mini.avatar.widget.AvatarActionModel;

import java.util.List;

/**
 * Created by ubt on 2017/8/29.
 */

public interface IAvatarActionDao {

    public void saveActionData();

    public List<AvatarActionModel> getAvatarActionModelList();

}
