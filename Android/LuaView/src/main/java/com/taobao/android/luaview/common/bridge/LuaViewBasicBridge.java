package com.taobao.android.luaview.common.bridge;

import android.content.Intent;

import com.taobao.android.luaview.common.activity.LuaViewBasicActivity;
import com.taobao.luaview.global.Constants;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.HashMap;
import java.util.Map;

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

    public void require(LuaTable params) {
        HashMap<String, String> hashMap = new HashMap<String, String>();

        LuaValue[] k = params.keys();
        for (int i=0; i < k.length; i++) {
            hashMap.put(k[i].optjstring(null), params.get(k[i]).optjstring(null));
        }

        Intent intent = new Intent(mActivity, LuaViewBasicActivity.class);
        intent.putExtra(Constants.PAGE_PARAMS, hashMap);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(intent);
    }

    public LuaTable args() {
        if (mActivity.getIntent() != null && mActivity.getIntent().hasExtra(Constants.PAGE_PARAMS)) {
            HashMap<String, String> hashMap = (HashMap<String, String>)mActivity.getIntent().getSerializableExtra(Constants.PAGE_PARAMS);
            LuaTable table = new LuaTable();
            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                table.set(entry.getKey(), entry.getValue());
            }
            return table;
        } else {
            LuaTable table = new LuaTable();
            table.set("page", mActivity.getMainPage());
            return table;
        }
    }
}
