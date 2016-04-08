package com.taobao.luaview.fun.binder.ui;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.fun.base.BaseVarArgCreator;
import com.taobao.luaview.fun.mapper.ui.UIAnimatorSetMethodMapper;
import com.taobao.luaview.userdata.ui.UDAnimatorSet;

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
@Deprecated
public class UIAnimatorSetBinder extends BaseFunctionBinder {

    public UIAnimatorSetBinder() {
        super("Animation");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return UIAnimatorSetMethodMapper.class;
    }

    @Override
    public LuaValue createCreator(LuaValue env, final LuaValue metaTable) {
        return new BaseVarArgCreator(env.checkglobals(), metaTable) {
            @Override
            public LuaValue createUserdata(Globals globals, LuaValue metaTable, Varargs varargs) {
                return new UDAnimatorSet(globals, metaTable, varargs);
            }
        };
    }
}
