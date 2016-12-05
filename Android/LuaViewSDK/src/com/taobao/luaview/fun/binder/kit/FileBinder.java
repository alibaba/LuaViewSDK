package com.taobao.luaview.fun.binder.kit;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.userdata.kit.UDFile;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;

/**
 * File operation
 *
 * @author song
 * @date 16/12/5
 * 主要功能描述
 * 修改描述
 * 下午2:41 song XXX
 */
public class FileBinder extends BaseFunctionBinder {

    public FileBinder() {
        super("File");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return null;
    }

    @Override
    public LuaValue createCreator(LuaValue env, final LuaValue metaTable) {
        return new UDFile(env.checkglobals(), metaTable);
    }
}
