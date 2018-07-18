package com.ubtechinc.alpha.mini.qqmusic.model;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gyf.barlibrary.ImmersionBar;
import com.ubtechinc.alpha.mini.R;

public class TestQQActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_qq);
        ImmersionBar.with(this)
                .transparentBar()
                .init();
    }
}
