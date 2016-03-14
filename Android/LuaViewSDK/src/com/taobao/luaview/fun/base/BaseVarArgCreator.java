package com.taobao.luaview.fun.base;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

/**
 * 零参数函数
 *
 * @author song
 * @date 15/8/14
 */
public abstract class BaseVarArgCreator extends VarArgFunction {
    public Globals globals;
    public LuaValue metatable;

    public BaseVarArgCreator(Globals globals, LuaValue metatable) {
        this.globals = globals;
        this.metatable = metatable;
    }

    public Varargs invoke(Varargs args) {
        return createUserdata(globals, metatable, args);
    }

    public abstract LuaValue createUserdata(Globals globals, LuaValue metaTable, Varargs varargs);
}
