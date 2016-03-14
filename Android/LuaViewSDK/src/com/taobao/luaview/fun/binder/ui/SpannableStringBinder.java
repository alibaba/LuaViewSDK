package com.taobao.luaview.fun.binder.ui;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.fun.base.BaseVarArgCreator;
import com.taobao.luaview.fun.mapper.ui.SpannableStringMethodMapper;
import com.taobao.luaview.userdata.ui.UDSpannableString;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

/**
 * StyledString 复合文本
 * @author song
 */
public class SpannableStringBinder extends BaseFunctionBinder {

    public SpannableStringBinder() {
        super("StyledString");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return SpannableStringMethodMapper.class;
    }

    @Override
    public LuaValue createCreator(LuaValue env, LuaValue metaTable) {
        return new BaseVarArgCreator(env.checkglobals(), metaTable) {
            @Override
            public LuaValue createUserdata(Globals globals, LuaValue metaTable, Varargs varargs) {
                return new UDSpannableString(globals, metaTable, varargs);
            }
        };
    }

}
