package com.taobao.luaview.demo.activity;

import android.app.Activity;
import android.os.Bundle;

import com.taobao.luaview.demo.provider.GlideImageProvider;
import com.taobao.luaview.demo.ui.CustomError;
import com.taobao.luaview.demo.ui.CustomLoading;
import com.taobao.luaview.global.Constants;
import com.taobao.luaview.global.LuaView;
import com.taobao.luaview.view.LVLoadingDialog;

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
        LuaView.createAsync(this, new LuaView.CreatedCallback() {
            @Override
            public void onCreated(LuaView luaView) {
                if (mDialog != null) {
                    mDialog.cancel();
                }
                mLuaView = luaView;
                setContentView(mLuaView);
                if (mLuaView != null) {
                    registerNameBeforeLoad(mLuaView);
                    load(mLuaView);
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
