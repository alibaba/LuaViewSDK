package com.taobao.luaview.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.taobao.luaview.bridge.LVBasicBridge;
import com.taobao.luaview.global.Constants;
import com.taobao.luaview.global.LuaView;

/**
 * Copyright 2017 Alibaba Group
 * License: MIT
 * https://alibaba.github.io/LuaViewSDK
 * Created by tuoli on 17/3/27.
 */

public class LVBasicActivity extends AppCompatActivity implements ILuaViewEntry {

    protected LuaView mLuaView;

    @Override
    public String getLuaViewEntry() {
        return "kit/main.lua";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LuaView.createAsync(this, new LuaView.CreatedCallback() {
            @Override
            public void onCreated(LuaView luaView) {
                mLuaView = luaView;
                if (mLuaView != null) {
                    setContentView(mLuaView);
                    mLuaView.register("Bridge", new LVBasicBridge(LVBasicActivity.this));
                    mLuaView.setUseStandardSyntax(true);     // 使用标准语法
                    mLuaView.load(getLuaViewEntry(), null, null);
                }
            }
        });
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
