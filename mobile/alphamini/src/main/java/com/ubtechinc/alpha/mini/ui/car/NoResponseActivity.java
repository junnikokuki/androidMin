package com.ubtechinc.alpha.mini.ui.car;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseActivity;
import com.ubtechinc.alpha.mini.databinding.ActivityNoResponseBinding;

/**
 * Created by riley.zhang on 2018/7/4.
 */

public class NoResponseActivity extends BaseActivity {

    private ActivityNoResponseBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_no_response);
        mBinding.setEvent(new EventReponse());
    }

    @Override
    protected void setStatuesBar() {

    }

    public class EventReponse {

        public void onNextStep(View view) {
            Intent intent = new Intent(NoResponseActivity.this, CarListActivity.class);
            startActivity(intent);
        }

        public void closeActivity(View view) {
            finish();
        }
    }

}
