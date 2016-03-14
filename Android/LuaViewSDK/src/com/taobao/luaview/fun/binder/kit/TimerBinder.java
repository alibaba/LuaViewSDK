package com.taobao.luaview.fun.binder.kit;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.fun.base.BaseVarArgCreator;
import com.taobao.luaview.fun.mapper.kit.TimerMethodMapper;
import com.taobao.luaview.userdata.kit.UDTimer;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

/**
 * Timer 接口封装
 *
 * @author song
 * @date 15/8/21
 */
public class TimerBinder extends BaseFunctionBinder {

    public TimerBinder() {
        super("Timer");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return TimerMethodMapper.class;
    }

    @Override
    public LuaValue createCreator(LuaValue env, final LuaValue metaTable) {
        return new BaseVarArgCreator(env.checkglobals(), metaTable) {
            @Override
            public LuaValue createUserdata(Globals globals, LuaValue metaTable, Varargs varargs) {
                return new UDTimer(globals, metaTable, varargs);
            }
        };
    }
}
