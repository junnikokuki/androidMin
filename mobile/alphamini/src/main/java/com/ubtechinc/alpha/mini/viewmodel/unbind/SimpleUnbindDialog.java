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

public class SimpleUnbindDialog extends UnbindDialog {

    public SimpleUnbindDialog(Context ctx) {
        super(ctx);
    }

    private String searialNumber;

    public void setSearialNumber(String searialNumber) {
        this.searialNumber = searialNumber;
    }

    public String getSearialNumber() {
        return searialNumber;
    }

    @Override
    public void setContent() {
        dialog.setTitle(R.string.unbind_simple_title).setMessage(R.string.unbind_simple_message)
                .setNegativeButton(R.string.permission_cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                }).setPositiveButton(R.string.unbind, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRobotsViewModel.doThrowRobotPermission(listener, searialNumber);
                dialog.dismiss();
            }
        }).setPostiveTextColor(ctx.getResources().getColor(R.color.btn_red));
        setDialogProperty();
    }
}
