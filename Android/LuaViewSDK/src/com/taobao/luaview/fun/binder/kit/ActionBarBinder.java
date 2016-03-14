package com.taobao.luaview.fun.binder.kit;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.userdata.kit.UDActionBar;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;

/**
 * Navigation 接口封装
 *
 * @author song
 * @date 15/8/21
 */
public class ActionBarBinder extends BaseFunctionBinder {

    public ActionBarBinder() {
        super("Navigation");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return null;
    }

    @Override
    public LuaValue createCreator(LuaValue env, final LuaValue metaTable) {
        return new UDActionBar(env.checkglobals(), metaTable);
    }
}
