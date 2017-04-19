/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.base;

import com.taobao.luaview.global.LuaViewConfig;
import com.taobao.luaview.global.LuaViewManager;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;
import org.luaj.vm2.lib.TwoArgFunction;

/**
 * 基础两参数函数对象
 *
 * @author song
 * @date 15/8/14
 */
public abstract class BaseFunctionBinder extends TwoArgFunction {
    public String[] luaNames;

    public BaseFunctionBinder(String... name) {
        this.luaNames = name;
    }

    public String[] getLuaNames() {
        return luaNames;
    }

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        return call(env, getMapperClass());
    }

    private LuaValue call(LuaValue env, Class<? extends LibFunction> libClass) {
        final LuaTable metatable = (libClass == null || LuaViewConfig.isLibsLazyLoad() == false) ? LuaViewManager.createMetatable(libClass) : null;//当不是lazyLoad或者lib为空（常量）的时候直接加载
        if (luaNames != null) {
            for (String name : luaNames) {
                env.set(name, createCreator(env, metatable));
            }
        }
        return metatable;
    }

    public abstract Class<? extends LibFunction> getMapperClass();

    /**
     * 默认返回metatable，如果要使用对象方式调用，则返回一个LuaFunction
     *
     * @param env
     * @param metaTable
     * @return
     */
    public LuaValue createCreator(LuaValue env, LuaValue metaTable) {
        return metaTable;
    }
}
