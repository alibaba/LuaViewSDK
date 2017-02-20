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
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(true);


        LuaView.createAsync(this, new LuaView.CreatedCallback() {
            @Override
            public void onCreated(LuaView luaView) {
                if (mDialog != null) {
                    mDialog.cancel();
                }
                mLuaView = luaView;
                if (mLuaView != null) {
                    registerNameBeforeLoad(mLuaView);
                    load(mLuaView);
                    setContentView(mLuaView);
                }
            }
        });
        LuaView.registerImageProvider(GlideImageProvider.class);
        mDialog = new LVLoadingDialog(this);
        mDialog.show();
    }

    /**
     * 注册接口，注册各种脚本，panel
     */
    public void registerNameBeforeLoad(final LuaView luaView) {
        luaView.registerPanel(CustomError.class);
        luaView.registerPanel(CustomLoading.class);
        luaView.register("bridge", new LuaViewBridge(this));
        luaView.setUseStandardSyntax(DemoActivity.useStandardSyntax);//是否使用标准语法
    }

    /**
     * 加载数据
     */
    public void load(final LuaView luaView) {
        LogUtil.timeStart("plain code");
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
                LogUtil.timeEnd("plain code");
                //测试调用 lua function
                LogUtil.d("call-lua-function return:", luaView.callLuaFunction("global_fun_test1", 1, "a", 0.1));
                LogUtil.d("call-lua-function return:", JsonUtil.toString(luaView.callLuaFunction("global_fun_test2", 2, "b", 0.2)));
                LogUtil.d("call-window-function return:", luaView.callWindowFunction("window_fun1", 3, "c", 0.3));
                LogUtil.d("call-window-function return:", luaView.callWindowFunction("window_fun2", 4, "d", 0.4));
            }
        });

        //load bytecode directly
//        LogUtil.timeStart("prototype");
//        luaView.loadPrototype(AssetUtil.open(this, "test/lvp/UI_Window.luap"), "UI_window", new LuaScriptLoader.ScriptExecuteCallback() {
//            @Override
//            public boolean onScriptPrepared(ScriptBundle bundle) {
//                return false;
//            }
//
//            @Override
//            public boolean onScriptCompiled(LuaValue value, LuaValue context, LuaValue view) {
//                return false;
//            }
//
//            @Override
//            public void onScriptExecuted(String uri, boolean executedSuccess) {
//                LogUtil.timeEnd("prototype");
//            }
//        });
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

    @Override
    protected void onDestroy() {
        LogUtil.d("yesong-onDestroy");
        super.onDestroy();
        if (mLuaView != null) {
            mLuaView.onDestroy();
        }
    }
}
