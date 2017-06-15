/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.demo.activity;

import android.app.Activity;
import android.os.Bundle;

import com.taobao.luaview.demo.provider.GlideImageProvider;
import com.taobao.luaview.demo.ui.CustomError;
import com.taobao.luaview.demo.ui.CustomLoading;
import com.taobao.luaview.global.Constants;
import com.taobao.luaview.global.LuaScriptLoader;
import com.taobao.luaview.global.LuaView;
import com.taobao.luaview.scriptbundle.ScriptBundle;
import com.taobao.luaview.util.AssetUtil;
import com.taobao.luaview.util.JsonUtil;
import com.taobao.luaview.util.LogUtil;
import com.taobao.luaview.view.LVLoadingDialog;

import org.luaj.vm2.LuaValue;

/**
 * 通过LuaView、注入bridge对象，实现Lua-Java通信
 *
 * @author song
 * @date 15/11/11
 * 主要功能描述
 * 修改描述
 * 下午4:50 song XXX
 */
public class DemoLuaViewActivity extends Activity {
    private LuaView mLuaView;
    private LVLoadingDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionBar();
        showLoading();
        createLuaViewAsync();
    }

    private void initActionBar() {
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void showLoading() {
        mDialog = new LVLoadingDialog(this);
        mDialog.show();
    }

    private void hideLoading() {
        if (mDialog != null) {
            mDialog.cancel();
        }
    }

    /**
     * 异步创建LuaView（推荐方式）
     */
    private void createLuaViewAsync() {
        LuaView.createAsync(this, new LuaView.CreatedCallback() {
            @Override
            public void onCreated(LuaView luaView) {
                hideLoading();
                mLuaView = luaView;
                if (mLuaView != null) {
                    extendsLuaView(mLuaView);
                    loadScript(mLuaView);
                    setContentView(mLuaView);
                }
            }
        });
    }

    /**
     * 同步创建LuaView
     */
    private void createLuaView(){
        mLuaView = LuaView.create(this);
        extendsLuaView(mLuaView);
        loadScript(mLuaView);
        setContentView(mLuaView);
        hideLoading();
    }


    /**
     * 扩展LuaView
     * 注册Panel（如果已经有UI组件，需要在Lua使用）
     * 注册Bridge（有部分API或功能，需要在Lua使用）
     */
    private void extendsLuaView(final LuaView luaView) {
        luaView.registerImageProvider(GlideImageProvider.class);
        luaView.registerPanel(CustomError.class);
        luaView.registerPanel(CustomLoading.class);
        luaView.register("bridge", new LuaViewBridge(this));
        luaView.setUseStandardSyntax(DemoActivity.useStandardSyntax);//是否使用标准语法
    }

    /**
     * 加载数据
     */
    public void loadScript(final LuaView luaView) {
        luaView.load(getLuaUri(), new LuaScriptLoader.ScriptExecuteCallback() {
            @Override
            public boolean onScriptPrepared(ScriptBundle bundle) {
                return false;
            }

            @Override
            public boolean onScriptCompiled(LuaValue value, LuaValue context, LuaValue view) {
                return false;
            }

            @Override
            public void onScriptExecuted(String uri, boolean executedSuccess) {
                //测试调用 lua function
                LogUtil.d("call-lua-function return:", luaView.callLuaFunction("global_fun_test1", 1, "a", 0.1));
                LogUtil.d("call-lua-function return:", JsonUtil.toString(luaView.callLuaFunction("global_fun_test2", 2, "b", 0.2)));
                LogUtil.d("call-window-function return:", luaView.callWindowFunction("window_fun1", 3, "c", 0.3));
                LogUtil.d("call-window-function return:", luaView.callWindowFunction("window_fun2", 4, "d", 0.4));
            }
        });
    }

    /**
     * load bytecode directly
     *
     * @param luaView
     */
    public void loadBytecodeScript(final LuaView luaView) {
        luaView.loadPrototype(AssetUtil.open(this, "test/lvp/UI_Window.luap"), "UI_window", new LuaScriptLoader.ScriptExecuteCallback() {
            @Override
            public boolean onScriptPrepared(ScriptBundle bundle) {
                return false;
            }

            @Override
            public boolean onScriptCompiled(LuaValue value, LuaValue context, LuaValue view) {
                return false;
            }

            @Override
            public void onScriptExecuted(String uri, boolean executedSuccess) {
            }
        });
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

    @Override
    protected void onDestroy() {
        LogUtil.d("LuaView-onDestroy");
        super.onDestroy();
        if (mLuaView != null) {
            mLuaView.onDestroy();
        }
    }
}
