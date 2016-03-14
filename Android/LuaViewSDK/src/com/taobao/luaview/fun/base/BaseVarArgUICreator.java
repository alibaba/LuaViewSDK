package com.taobao.luaview.fun.base;

import android.view.View;
import android.view.ViewGroup;

import com.taobao.luaview.view.interfaces.ILVView;

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
public abstract class BaseVarArgUICreator extends VarArgFunction {
    public Globals globals;
    public LuaValue metatable;

    public BaseVarArgUICreator(Globals globals, LuaValue metatable) {
        this.globals = globals;
        this.metatable = metatable;
    }

    public Varargs invoke(Varargs args) {
        ILVView view = createView(globals, metatable, args);
        if (globals.container instanceof ViewGroup && view instanceof View && ((View) view).getParent() == null) {
            globals.container.addLVView((View) view, args);
        }
        return view.getUserdata();
    }

    public abstract ILVView createView(Globals globals, LuaValue metaTable, Varargs varargs);
}
