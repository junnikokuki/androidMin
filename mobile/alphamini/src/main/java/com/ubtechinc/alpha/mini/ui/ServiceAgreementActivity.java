package com.ubtechinc.alpha.mini.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseActivity;

/**
 * Created by Administrator on 2017/12/12.
 */

public class ServiceAgreementActivity extends BaseActivity {
    private TextView textView;
    private ImageView back ;
    private TextView title ;
    private ImageView action  ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_agreement);
        textView =findViewById(R.id.service_agreement);
        textView.setText(R.string.service_agreement);

        back = findViewById(R.id.toolbar_back);
        title =findViewById(R.id.toolbar_title);
        title.setText(R.string.service);
        action= findViewById(R.id.toolbar_action);
        action.setVisibility(View.GONE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
