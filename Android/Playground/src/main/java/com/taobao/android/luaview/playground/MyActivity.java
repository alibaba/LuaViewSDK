package com.taobao.android.luaview.playground;

import android.os.Bundle;

import com.taobao.luaview.activity.LVBasicActivity;
import com.taobao.luaview.global.Constants;

public class MyActivity extends LVBasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getIntent().putExtra(Constants.PARAM_URI, "App");

        super.onCreate(savedInstanceState);
    }
}
