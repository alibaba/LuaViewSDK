package com.taobao.luaview.fun.binder.constants;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.userdata.constants.UDEllipsize;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;

/**
 * Ellipsize 常量
 * @author song
 * @date 15/11/5
 */
public class EllipsizeBinder extends BaseFunctionBinder {

    public EllipsizeBinder() {
        super("Ellipsize");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return null;
    }

    @Override
    public LuaValue createCreator(LuaValue env, final LuaValue metaTable) {
        return new UDEllipsize(env.checkglobals(), metaTable);
    }
}