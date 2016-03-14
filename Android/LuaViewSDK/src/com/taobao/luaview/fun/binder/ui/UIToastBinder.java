package com.taobao.luaview.fun.binder.ui;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.fun.base.BaseVarArgCreator;
import com.taobao.luaview.fun.mapper.ui.UIToastMethodMapper;
import com.taobao.luaview.userdata.ui.UDToast;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

/**
 * Toast for LuaView
 *
 * @author song
 * @date 15/9/2
 */
public class UIToastBinder extends BaseFunctionBinder {

    public UIToastBinder() {
        super("Toast");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return UIToastMethodMapper.class;
    }

    @Override
    public LuaValue createCreator(LuaValue env, final LuaValue metaTable) {
        return new BaseVarArgCreator(env.checkglobals(), metaTable) {
            @Override
            public LuaValue createUserdata(Globals globals, LuaValue metaTable, Varargs varargs) {
                return new UDToast(globals, metaTable, varargs);
            }
        };
    }
}
