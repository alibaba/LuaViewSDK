package com.taobao.android.luaview.playground;

import android.os.Bundle;

import com.taobao.luaview.activity.LVBasicActivity;

/**
 * Copyright 2017 Alibaba Group
 * License: MIT
 * https://alibaba.github.io/LuaViewSDK
 * Created by tuoli on 17/3/27.
 */

public class MyActivity extends LVBasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 开发者可以在这里更改主入口页面, 默认是App.lua
     * @return
     */
    @Override
    public String getMainPage() {
        return "App";
    }
}
