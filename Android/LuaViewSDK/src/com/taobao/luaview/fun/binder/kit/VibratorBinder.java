package com.taobao.luaview.fun.binder.kit;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.fun.base.BaseVarArgCreator;
import com.taobao.luaview.fun.mapper.kit.VibratorMethodMapper;
import com.taobao.luaview.userdata.kit.UDVibrator;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

/**
 * Vibrator 接口封装
 *
 * @author song
 * @date 15/8/21
 */
public class VibratorBinder extends BaseFunctionBinder {

    public VibratorBinder() {
        super("Vibrator");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return VibratorMethodMapper.class;
    }

    @Override
    public LuaValue createCreator(LuaValue env, final LuaValue metaTable) {
        return new BaseVarArgCreator(env.checkglobals(), metaTable) {
            @Override
            public LuaValue createUserdata(Globals globals, LuaValue metaTable, Varargs varargs) {
                return new UDVibrator(globals, metaTable, varargs);
            }
        };
    }
}
