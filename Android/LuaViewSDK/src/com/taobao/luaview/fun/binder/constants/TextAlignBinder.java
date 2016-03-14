package com.taobao.luaview.fun.binder.constants;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.userdata.constants.UDTextAlign;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;

/**
 * TextAlign 常量
 *
 * @author song
 * @date 15/8/21
 */
public class TextAlignBinder extends BaseFunctionBinder {

    public TextAlignBinder() {
        super("TextAlign");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return null;
    }

    @Override
    public LuaValue createCreator(LuaValue env, final LuaValue metaTable) {
        return new UDTextAlign(env.checkglobals(), metaTable);
    }
}
