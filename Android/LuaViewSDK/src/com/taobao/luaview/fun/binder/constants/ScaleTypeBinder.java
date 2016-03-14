package com.taobao.luaview.fun.binder.constants;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.userdata.constants.UDImageScaleType;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;

/**
 * ScaleType 常量
 *
 * @author song
 * @date 15/8/21
 */
public class ScaleTypeBinder extends BaseFunctionBinder {

    public ScaleTypeBinder() {
        super("ScaleType");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return null;
    }

    @Override
    public LuaValue createCreator(LuaValue env, final LuaValue metaTable) {
        return new UDImageScaleType(env.checkglobals(), metaTable);
    }
}
