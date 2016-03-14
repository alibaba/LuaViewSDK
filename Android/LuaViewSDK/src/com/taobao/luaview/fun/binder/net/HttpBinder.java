package com.taobao.luaview.fun.binder.net;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.fun.base.BaseVarArgCreator;
import com.taobao.luaview.fun.mapper.net.HttpMethodMapper;
import com.taobao.luaview.userdata.net.UDHttp;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

/**
 * Http 接口封装
 *
 * @author song
 * @date 15/8/21
 */
public class HttpBinder extends BaseFunctionBinder {

    public HttpBinder() {
        super("Http");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return HttpMethodMapper.class;
    }

    @Override
    public LuaValue createCreator(LuaValue env, final LuaValue metaTable) {
        return new BaseVarArgCreator(env.checkglobals(), metaTable) {
            @Override
            public LuaValue createUserdata(Globals globals, LuaValue metaTable, Varargs varargs) {
                return new UDHttp(globals, metaTable, varargs);
            }
        };
    }
}
