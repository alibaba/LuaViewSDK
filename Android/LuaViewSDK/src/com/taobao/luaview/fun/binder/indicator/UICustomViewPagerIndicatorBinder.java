/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.binder.indicator;

import com.taobao.luaview.fun.base.BaseFunctionBinder;
import com.taobao.luaview.fun.base.BaseVarArgUICreator;
import com.taobao.luaview.fun.mapper.indicator.UICustomViewPagerIndicatorMethodMapper;
import com.taobao.luaview.view.indicator.LVCustomViewPagerIndicator;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;

public class UICustomViewPagerIndicatorBinder extends BaseFunctionBinder {

    public UICustomViewPagerIndicatorBinder() {
        super("CustomPagerIndicator");
    }

    @Override
    public Class<? extends LibFunction> getMapperClass() {
        return UICustomViewPagerIndicatorMethodMapper.class;
    }

    @Override
    public LuaValue createCreator(LuaValue env, LuaValue metaTable) {
        return new BaseVarArgUICreator(env.checkglobals(), metaTable, getMapperClass()) {
            @Override
            public ILVView createView(Globals globals, LuaValue metaTable, Varargs varargs) {
                return new LVCustomViewPagerIndicator(globals, metaTable, varargs);
            }
        };
    }
}
