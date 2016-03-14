package com.taobao.luaview.fun.binder.kit;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.userdata.kit.UDSystem;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;

/**
 * System 接口封装
 *
 * @author song
 * @date 15/8/21
 */
public class SystemBinder extends BaseFunctionBinder {

    public SystemBinder() {
        super("System");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return null;
    }

    @Override
    public LuaValue createCreator(LuaValue env, final LuaValue metaTable) {
        return new UDSystem(env.checkglobals(), metaTable);
    }
}
