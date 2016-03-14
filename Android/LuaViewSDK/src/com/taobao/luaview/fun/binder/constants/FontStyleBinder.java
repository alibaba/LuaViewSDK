package com.taobao.luaview.fun.binder.constants;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.userdata.constants.UDFontStyle;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;

/**
 * FontStyle 常量
 *
 * @author song
 * @date 15/8/21
 */
public class FontStyleBinder extends BaseFunctionBinder {

    public FontStyleBinder() {
        super("FontStyle");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return null;
    }

    @Override
    public LuaValue createCreator(LuaValue env, final LuaValue metaTable) {
        return new UDFontStyle(env.checkglobals(), metaTable);
    }
}
