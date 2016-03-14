package com.taobao.luaview.fun.binder.ui;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.fun.base.BaseVarArgUICreator;
import com.taobao.luaview.fun.mapper.ui.UIEditTextMethodMapper;
import com.taobao.luaview.view.LVEditText;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

/**
 * EditText 普通按钮
 * @author song
 */
public class UIEditTextBinder extends BaseFunctionBinder {

    public UIEditTextBinder() {
        super("TextField");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return UIEditTextMethodMapper.class;
    }

    @Override
    public LuaValue createCreator(LuaValue env, LuaValue metaTable) {
        return new BaseVarArgUICreator(env.checkglobals(), metaTable) {
            @Override
            public ILVView createView(Globals globals, LuaValue metaTable, Varargs varargs) {
                return new LVEditText(globals, metaTable, varargs);
            }
        };
    }

}
