package com.taobao.luaview.fun.binder.indicator;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.fun.base.BaseVarArgUICreator;
import com.taobao.luaview.fun.mapper.indicator.UICircleViewPagerIndicatorMethodMapper;
import com.taobao.luaview.view.indicator.LVCircleViewPagerIndicator;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

/**
 * Indicator or PagerView
 *
 * @author song
 * @date 15/8/20
 */
public class UICircleViewPagerIndicatorBinder extends BaseFunctionBinder {

    public UICircleViewPagerIndicatorBinder() {
        super("PagerIndicator");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return UICircleViewPagerIndicatorMethodMapper.class;
    }

    @Override
    public LuaValue createCreator(LuaValue env, LuaValue metaTable) {
        return new BaseVarArgUICreator(env.checkglobals(), metaTable) {
            @Override
            public ILVView createView(Globals globals, LuaValue metaTable, Varargs varargs) {
                return new LVCircleViewPagerIndicator(globals, metaTable, varargs);
            }
        };
    }
}
