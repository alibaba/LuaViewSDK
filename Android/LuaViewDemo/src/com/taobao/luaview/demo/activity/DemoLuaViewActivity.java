package com.taobao.luaview.demo.activity;

import com.taobao.luaview.activity.LuaViewActivity;
import com.taobao.luaview.demo.ui.CustomError;
import com.taobao.luaview.demo.ui.CustomLoading;
import com.taobao.luaview.global.LuaView;

/**
 * 通过LuaView、注入bridge对象，实现Lua-Java通信
 *
 * @author song
 * @date 15/11/11
 * 主要功能描述
 * 修改描述
 * 下午4:50 song XXX
 */
public class DemoLuaViewActivity extends LuaViewActivity {

    @Override
    public void registerNameBeforeLoad(LuaView luaView) {
        super.registerNameBeforeLoad(luaView);
        luaView.registerPanel(CustomError.class);
        luaView.registerPanel(CustomLoading.class);
        luaView.register("bridge", new LuaViewBridge(this));
    }
}
