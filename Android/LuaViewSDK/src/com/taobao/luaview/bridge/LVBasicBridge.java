package com.taobao.luaview.bridge;

import android.app.Activity;
import android.content.Intent;

import com.taobao.luaview.activity.LVBasicActivity;
import com.taobao.luaview.global.Constants;

/**
 * Copyright 2017 Alibaba Group
 * License: MIT
 * https://alibaba.github.io/LuaViewSDK
 * Created by tuoli on 17/3/30.
 */

public class LVBasicBridge {

    private LVBasicActivity mActivity;

    public LVBasicBridge(LVBasicActivity activity) {
        this.mActivity = activity;
    }

    public void require(String pageName) {
        Intent intent = new Intent(mActivity, LVBasicActivity.class);
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
