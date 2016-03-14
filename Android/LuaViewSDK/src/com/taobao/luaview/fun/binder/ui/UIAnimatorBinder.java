package com.taobao.luaview.fun.binder.ui;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.fun.base.BaseVarArgCreator;
import com.taobao.luaview.fun.mapper.ui.UIAnimatorMethodMapper;
import com.taobao.luaview.userdata.ui.UDAnimator;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

/**
 * Animator 动画
 *
 * @author song
 * @date 15/9/2
 */
public class UIAnimatorBinder extends BaseFunctionBinder {

    public UIAnimatorBinder() {
        super("Animation");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return UIAnimatorMethodMapper.class;
    }

    @Override
    public LuaValue createCreator(LuaValue env, final LuaValue metaTable) {
        return new BaseVarArgCreator(env.checkglobals(), metaTable) {
            @Override
            public LuaValue createUserdata(Globals globals, LuaValue metaTable, Varargs varargs) {
                return new UDAnimator(globals, metaTable, varargs);
            }
        };
    }
}
