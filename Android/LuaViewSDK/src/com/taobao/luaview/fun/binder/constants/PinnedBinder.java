package com.taobao.luaview.fun.binder.constants;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.userdata.constants.UDPinned;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;

/**
 * Created by tuoli on 11/7/16.
 */

public class PinnedBinder extends BaseFunctionBinder {

    public PinnedBinder() {
        super("Pinned");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return null;
    }

    @Override
    public LuaValue createCreator(LuaValue env, final LuaValue metaTable) {
        return new UDPinned(env.checkglobals(), metaTable);
    }
}
