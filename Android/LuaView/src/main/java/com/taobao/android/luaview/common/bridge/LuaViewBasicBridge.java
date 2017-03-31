package com.taobao.android.luaview.common.bridge;

import android.content.Intent;

import com.taobao.android.luaview.common.activity.LuaViewBasicActivity;
import com.taobao.luaview.global.Constants;

/**
 * Copyright 2017 Alibaba Group
 * License: MIT
 * https://alibaba.github.io/LuaViewSDK
 * Created by tuoli on 17/3/31.
 */

public class LuaViewBasicBridge {

    private LuaViewBasicActivity mActivity;

    public LuaViewBasicBridge(LuaViewBasicActivity activity) {
        this.mActivity = activity;
    }

    public void require(String pageName) {
        Intent intent = new Intent(mActivity, LuaViewBasicActivity.class);
        intent.putExtra(Constants.PAGE_NAME, pageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(intent);
    }

    public String args() {
        if (mActivity.getIntent() != null && mActivity.getIntent().hasExtra(Constants.PAGE_NAME)) {
            return mActivity.getIntent().getStringExtra(Constants.PAGE_NAME);
        } else {
            return mActivity.getMainPage();
        }
    }
}
