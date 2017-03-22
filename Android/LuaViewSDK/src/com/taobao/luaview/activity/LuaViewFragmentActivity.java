/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.taobao.luaview.global.Constants;
import com.taobao.luaview.global.LuaView;

/**
 * LuaView Activity
 *
 * @author song
 * @date 15/9/22
 */
public class LuaViewFragmentActivity extends FragmentActivity {
    private LuaView mLuaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLuaView = LuaView.create(this);
        setContentView(mLuaView);
        getSupportFragmentManager().beginTransaction().commit();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mLuaView != null) {
            registerNameBeforeLoad(mLuaView);
            load(mLuaView);
        }
    }

    /**
     * 注册接口，注册各种脚本，panel
     */
    public void registerNameBeforeLoad(final LuaView luaView) {
    }

    /**
     * 加载数据
     */
    public void load(final LuaView luaView) {
        luaView.load(getLuaUri());
    }

    /**
     * 获取文件名称
     *
     * @return
     */
    private String getLuaUri() {
        if (getIntent() != null && getIntent().hasExtra(Constants.PARAM_URI)) {
            return getIntent().getStringExtra(Constants.PARAM_URI);
        }
        return null;
    }

    public LuaView getLuaView() {
        return mLuaView;
    }
}
