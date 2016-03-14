package com.taobao.luaview.fun.binder.constants;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.userdata.constants.UDAlign;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;

/**
 * Align 常量
 *
 * @author song
 * @date 15/8/21
 */
public class AlignBinder extends BaseFunctionBinder {

    public AlignBinder() {
        super("Align");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return null;
    }

    @Override
    public LuaValue createCreator(LuaValue env, final LuaValue metaTable) {
        return new UDAlign(env.checkglobals(), metaTable);
    }
}
