package com.taobao.luaview.fun.binder.constants;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.userdata.constants.UDTouchEvent;
import com.taobao.luaview.userdata.constants.UDViewEffect;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;

/**
 * Touch Event
 *
 * @author song
 * @date 16/8/15
 * 主要功能描述
 * 修改描述
 * 下午4:03 song XXX
 */
public class TouchEventBinder extends BaseFunctionBinder {

    public TouchEventBinder() {
        super("TouchEvent");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return null;
    }

    @Override
    public LuaValue createCreator(LuaValue env, final LuaValue metaTable) {
        return new UDTouchEvent(env.checkglobals(), metaTable);
    }
}
