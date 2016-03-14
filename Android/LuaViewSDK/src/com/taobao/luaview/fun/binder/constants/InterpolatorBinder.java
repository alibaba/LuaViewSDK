package com.taobao.luaview.fun.binder.constants;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.userdata.constants.UDInterpolator;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;

/**
 * Interpolator 常量
 *
 * @author song
 * @date 15/8/21
 */
public class InterpolatorBinder extends BaseFunctionBinder {

    public InterpolatorBinder() {
        super("Interpolator");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return null;
    }

    @Override
    public LuaValue createCreator(LuaValue env, final LuaValue metaTable) {
        return new UDInterpolator(env.checkglobals(), metaTable);
    }
}
