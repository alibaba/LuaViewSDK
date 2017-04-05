package com.taobao.android.luaview.common.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.taobao.android.luaview.common.bridge.LuaViewBasicBridge;
import com.taobao.android.luaview.common.interfaces.ILuaViewMainEntry;
import com.taobao.android.luaview.common.provider.GlideImageProvider;
import com.taobao.luaview.global.LuaView;
import com.taobao.luaview.global.LuaViewConfig;

/**
 * Copyright 2017 Alibaba Group
 * License: MIT
 * https://alibaba.github.io/LuaViewSDK
 * Created by tuoli on 17/3/31.
 */

public class LuaViewBasicActivity extends AppCompatActivity implements ILuaViewMainEntry {

    protected LuaView mLuaView;

    @Override
    public String getLuaViewEntry() {
        return "kit/main.lua";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        LuaView.createAsync(this, new LuaView.CreatedCallback() {
            @Override
            public void onCreated(LuaView luaView) {
                mLuaView = luaView;
                if (mLuaView != null) {
                    setContentView(mLuaView);
                    mLuaView.register("Bridge", new LuaViewBasicBridge(LuaViewBasicActivity.this));
                    mLuaView.setUseStandardSyntax(true);     // 使用标准语法
                    mLuaView.load(getLuaViewEntry(), null, null);
                }
            }
        });
    }

    private void init() {
        LuaViewConfig.setDebug(true);
        LuaViewConfig.setOpenDebugger(false);
        LuaViewConfig.setLibsLazyLoad(true);
        LuaViewConfig.setAutoSetupClickEffects(true);

        LuaView.registerImageProvider(GlideImageProvider.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLuaView != null) {
            mLuaView.onDestroy();
        }
    }

    public String getMainPage() {
        return "App";
    }
}
