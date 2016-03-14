package com.taobao.luaview.fun.binder.ui;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.fun.base.BaseVarArgCreator;
import com.taobao.luaview.fun.mapper.ui.UILoadingDialogMethodMapper;
import com.taobao.luaview.userdata.ui.UDLoadingDialog;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

/**
 * 菊花（对话框）
 * @author song
 */
public class UILoadingDialogBinder extends BaseFunctionBinder {

    public UILoadingDialogBinder() {
        super("LoadingDialog");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return UILoadingDialogMethodMapper.class;
    }

    @Override
    public LuaValue createCreator(LuaValue env, LuaValue metaTable) {
        return new BaseVarArgCreator(env.checkglobals(), metaTable) {
            @Override
            public LuaValue createUserdata(Globals globals, LuaValue metaTable, Varargs varargs) {
                return new UDLoadingDialog(globals, metaTable, varargs);
            }
        };
    }

}
