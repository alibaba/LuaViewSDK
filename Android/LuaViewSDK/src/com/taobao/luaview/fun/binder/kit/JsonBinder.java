package com.taobao.luaview.fun.binder.kit;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.userdata.kit.UDJson;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;

/**
 * Json 接口封装
 * @author song
 */
public class JsonBinder extends BaseFunctionBinder {

    public JsonBinder() {
        super("Json");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return null;
    }

    @Override
    public LuaValue createCreator(LuaValue env, final LuaValue metaTable) {
        return new UDJson(env.checkglobals(), metaTable);
    }
}
