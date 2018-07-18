package com.ubtechinc.alpha.mini.ui.friend;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseToolbarActivity;
import com.ubtechinc.alpha.mini.databinding.ActivityModifyAvatarBinding;
import com.ubtechinc.alpha.mini.ui.adapter.FriendAvatarAdapter;

/**
 * @Date: 2017/12/20.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] : 更改头像
 */

public class ModifyFriendAvatarActivity extends BaseToolbarActivity {

    private ActivityModifyAvatarBinding binding;
    private FriendAvatarAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_modify_avatar);
        initView();
    }

    private void initView() {
        initToolbar(binding.toolbar, getString(R.string.friend_modify_avatar), View.VISIBLE, true);
        initConnectFailedLayout(R.string.mobile_connected_error, R.string.robot_connected_error);
        backBtn.setImageResource(R.drawable.close_btn_selector);
        actionBtn.setImageResource(R.drawable.ic_top_confirm);
        adapter = new FriendAvatarAdapter(this);
        binding.rvAvatar.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rvAvatar.setAdapter(adapter);

    }

}
