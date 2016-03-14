package com.taobao.luaview.userdata.base;

import android.content.Context;

import com.taobao.luaview.global.LuaResourceFinder;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.io.Serializable;

/**
 * 基础Table
 *
 * @author song
 * @date 15/8/21
 */
public class BaseLuaTable extends LuaTable implements Serializable {
    private Object mObj;
    private Globals mGlobals;
    private LuaValue mMetatable;
    public Varargs mVarargs;

    public BaseLuaTable(Globals globals, LuaValue metatable) {
        this(null, globals, metatable, NIL);
    }

    public BaseLuaTable(Globals globals, LuaValue metatable, Varargs varargs) {
        this(null, globals, metatable, varargs);
    }

    public BaseLuaTable(Object obj, Globals globals, LuaValue metatable) {
        this(obj, globals, metatable, NIL);
    }

    public BaseLuaTable(Object obj, Globals globals, LuaValue metatable, Varargs varargs) {
        this.mObj = obj;
        this.mGlobals = globals;
        this.mMetatable = metatable;
        this.mVarargs = varargs;
    }

    public Globals getGlobals() {
        return mGlobals;
    }

    public LuaResourceFinder getLuaResourceFinder() {
        return mGlobals != null ? mGlobals.getLuaResourceFinder() : null;
    }

    public Context getContext() {
        return getGlobals() != null ? getGlobals().context : null;
    }
}
