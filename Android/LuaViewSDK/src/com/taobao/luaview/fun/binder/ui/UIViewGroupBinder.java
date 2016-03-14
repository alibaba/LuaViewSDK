package com.taobao.luaview.fun.binder.ui;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.fun.base.BaseVarArgUICreator;
import com.taobao.luaview.fun.mapper.ui.UIViewGroupMethodMapper;
import com.taobao.luaview.view.LVViewGroup;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

/**
 * 容器类，放各个view
 *
 * @author song
 * @date 15/8/20
 */
public class UIViewGroupBinder extends BaseFunctionBinder {

    public UIViewGroupBinder() {
        super("View");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return UIViewGroupMethodMapper.class;
    }

    @Override
    public LuaValue createCreator(LuaValue env, LuaValue metaTable) {
        return new BaseVarArgUICreator(env.checkglobals(), metaTable) {
            @Override
            public ILVView createView(Globals globals, LuaValue metaTable, Varargs varargs) {
                return new LVViewGroup(globals, metaTable, varargs);
            }
        };
    }
}
