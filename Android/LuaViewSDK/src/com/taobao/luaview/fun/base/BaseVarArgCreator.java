/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.fun.base;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.fun.mapper.ui.NewIndexFunction;
import com.taobao.luaview.global.LuaViewConfig;
import com.taobao.luaview.global.LuaViewManager;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.LibFunction;
import org.luaj.vm2.lib.VarArgFunction;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * 零参数函数
 *
 * @author song
 * @date 15/8/14
 */
public abstract class BaseVarArgCreator extends VarArgFunction {
    public Globals globals;
    public LuaValue metatable;
    public Class<? extends LibFunction> libClass;

    public BaseVarArgCreator(Globals globals, LuaValue metatable) {
        this(globals, metatable, null);
    }

    public BaseVarArgCreator(Globals globals, LuaValue metatable, Class<? extends LibFunction> libClass) {
        this.globals = globals;
        this.metatable = metatable;
        this.libClass = libClass;
    }

    public Varargs invoke(Varargs args) {
        if (LuaViewConfig.isLibsLazyLoad()) {
            metatable = LuaViewManager.createMetatable(libClass);
        }
        return createUserdata(globals, metatable, args);
    }

    /**
     * 获取所有方法
     *
     * @param clazz
     * @return
     */
    private List<Method> getMapperMethods(final Class clazz) {
        final List<Method> methods = new ArrayList<Method>();
        getMapperMethodsByClazz(methods, clazz);
        return methods.size() > 0 ? methods : null;
    }

    private void getMapperMethodsByClazz(final List<Method> result, final Class clazz) {
        if (clazz != null && clazz.isAnnotationPresent(LuaViewLib.class)) {//XXXMapper
            getMapperMethodsByClazz(result, clazz.getSuperclass());//处理super
            final Method[] methods = clazz.getDeclaredMethods();
            if (methods != null && methods.length > 0) {
                for (final Method method : methods) {//add self
                    if (method.getModifiers() == Modifier.PUBLIC) {//public 方法才行
                        result.add(method);
                    }
                }
            }
        }
    }

    private LuaValue addNewIndex(LuaTable t) {
        return tableOf(new LuaValue[]{INDEX, t, NEWINDEX, new NewIndexFunction(t)});
    }

    public abstract LuaValue createUserdata(Globals globals, LuaValue metaTable, Varargs varargs);
}
