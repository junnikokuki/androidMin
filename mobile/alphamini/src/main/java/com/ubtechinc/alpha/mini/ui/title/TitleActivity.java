package com.ubtechinc.alpha.mini.ui.title;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewStub;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.BaseActivity;

/**
 * @desc : 通用的标题Activity
 * @author: zach.zhang
 * @email : Zach.zhang@ubtrobot.com
 * @time : 2017/11/1
 */
public abstract class TitleActivity extends BaseActivity {

    private ViewStub vsContent;
    protected TitleBackItemView titleBackItemView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);
        findView();
        initView();
    }

    protected void initView() {
        titleBackItemView.setOnBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        onInitView();
    }

    protected abstract @LayoutRes int getContentLayout();

    protected void onInitView(){}

    protected void findView() {
        vsContent = findViewById(R.id.vs_content);
        vsContent.setLayoutResource(getContentLayout());
        vsContent.inflate();
        titleBackItemView = findViewById(R.id.title_back_item_view);
        onFindView();
    }

    protected void onFindView(){}


}
