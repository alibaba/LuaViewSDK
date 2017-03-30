package com.taobao.android.luaview.playground;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.taobao.luaview.global.Constants;

/**
 * Copyright 2017 Alibaba Group
 * License: MIT
 * https://alibaba.github.io/LuaViewSDK
 * Created by tuoli on 17/3/27.
 */

public class MyBridge {

    private Activity mActivity;

    public MyBridge(Activity activity) {
        this.mActivity = activity;
    }

    public void require(String pageUri) {
        Intent intent = new Intent(mActivity, MainActivity.class);
        intent.putExtra(Constants.PARAM_URI, pageUri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(R.anim.zoomin,R.anim.zoomout);
    }

    public String args() {
        if (mActivity.getIntent() != null && mActivity.getIntent().hasExtra(Constants.PARAM_URI)) {
            return mActivity.getIntent().getStringExtra(Constants.PARAM_URI);
        }
        return null;
    }
}

