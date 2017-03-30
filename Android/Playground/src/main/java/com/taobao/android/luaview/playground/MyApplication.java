package com.taobao.android.luaview.playground;

import android.app.Application;

import com.taobao.luaview.global.LuaView;
import com.taobao.luaview.global.LuaViewConfig;

/**
 * Copyright 2017 Alibaba Group
 * License: MIT
 * https://alibaba.github.io/LuaViewSDK
 * Created by tuoli on 17/3/27.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    private void init() {
        LuaViewConfig.setDebug(true);
        LuaViewConfig.setOpenDebugger(false);
        LuaViewConfig.setLibsLazyLoad(true);
        LuaViewConfig.setAutoSetupClickEffects(true);

        LuaView.registerImageProvider(GlideImageProvider.class);
    }
}

