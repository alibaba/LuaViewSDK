package com.taobao.android.luaview.playground;

import android.os.Bundle;

import com.taobao.android.luaview.common.activity.LuaViewBasicActivity;

/**
 * Copyright 2017 Alibaba Group
 * License: MIT
 * https://alibaba.github.io/LuaViewSDK
 * Created by tuoli on 17/3/27.
 */

public class MyActivity extends LuaViewBasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 开发者可以在这里更改入口主页面, 默认是App.lua
     * @return
     */
    @Override
    public String getMainPage() {
        return "App";
    }

    /**
     * 开发者可以在这里更改Lua代码的主入口
     * 默认使用kit包下的main.lua
     * 该包提供了一套界面描述和业务逻辑分离的机制
     * @return
     */
    @Override
    public String getLuaViewEntry() {
        return "kit/main.lua";
    }
}
