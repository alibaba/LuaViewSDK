package com.taobao.luaview.userdata.base;

import android.content.Context;

import com.taobao.luaview.global.LuaResourceFinder;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.io.Serializable;

/**
 * 基础用户数据
 *
 * @author song
 * @date 15/8/21
 */
public class BaseUserdata extends LuaUserdata implements Serializable {
    private Globals mGlobals;
    public Varargs mVarargs;

    public BaseUserdata(Globals globals, LuaValue metatable) {
        super(globals, metatable);
    }

    public BaseUserdata(Globals globals, LuaValue metatable, Varargs varargs) {
        super(globals, metatable);
        this.mVarargs = varargs;
    }

    public BaseUserdata(Object obj, Globals globals, LuaValue metatable) {
        this(obj, globals, metatable, NIL);
    }

    public BaseUserdata(Object obj, Globals globals, LuaValue metatable, Varargs varargs) {
        super(obj, metatable);
        this.mGlobals = globals;
        this.mVarargs = varargs;
    }

    public Globals getGlobals() {
        return userdata() instanceof Globals ? (Globals) userdata() : mGlobals;
    }

    public LuaResourceFinder getLuaResourceFinder() {
        return getGlobals() != null ? getGlobals().getLuaResourceFinder() : null;
    }

    public Context getContext() {
        return getGlobals() != null ? getGlobals().context : null;
    }
}
