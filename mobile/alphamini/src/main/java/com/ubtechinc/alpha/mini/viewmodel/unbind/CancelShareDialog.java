package com.ubtechinc.alpha.mini.viewmodel.unbind;

import android.content.Context;
import android.view.View;

import com.ubtechinc.alpha.mini.R;

/**
 * @Date: 2017/11/29.
 * @Author: Liu Dongyang
 * @Modifier :
 * @Modify Date:
 * [A brief description] :
 */

public class CancelShareDialog extends UnbindDialog {

    private String mSlaverUserId;

    public CancelShareDialog(Context ctx, String userId){
        super(ctx);
        this.mSlaverUserId = userId;
    }

    @Override
    public void setContent() {
        dialog.setTitle(R.string.title_cancle_share).setMessage(R.string.cancel_share_tip)
                .setPositiveButton(R.string.simple_message_comfire, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        mRobotsViewModel.cancelPermission(mSlaverUserId, listener);
                    }
                }).setNegativeButton(R.string.simple_message_cancel,new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        setDialogProperty();
    }
}
